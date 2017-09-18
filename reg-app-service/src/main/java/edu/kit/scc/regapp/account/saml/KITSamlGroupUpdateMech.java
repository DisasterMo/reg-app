package edu.kit.scc.regapp.account.saml;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.entity.EventType;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.HomeOrgGroupEntity;
import edu.kit.scc.regapp.entity.ServiceBasedGroupEntity;
import edu.kit.scc.regapp.entity.ServiceGroupFlagEntity;
import edu.kit.scc.regapp.entity.ServiceGroupStatus;
import edu.kit.scc.regapp.entity.UserGroupEntity;
import edu.kit.scc.regapp.entity.account.AccountGroupEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.entity.audit.AuditStatus;
import edu.kit.scc.regapp.event.MultipleGroupEvent;
import edu.kit.scc.regapp.exc.EventSubmitException;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.LdapConfig.SearchScope;
import edu.vt.middleware.ldap.handler.ConnectionHandler.ConnectionStrategy;

public class KITSamlGroupUpdateMech extends AbstractSamlGroupUpdateMech implements SamlGroupUpdateMech {

	private static final String MEMBER_OF = "memberOf";
	
	@Override
	protected HashSet<GroupEntity> update(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		HashSet<GroupEntity> changedGroups = new HashSet<GroupEntity>();
		
		changedGroups.addAll(updateSecondary(account, attributeMap, auditor));

		// Also add parent groups, to reflect changes
		HashSet<GroupEntity> allChangedGroups = new HashSet<GroupEntity>(changedGroups.size());
		for (GroupEntity group : changedGroups) {
			allChangedGroups.add(group);
			if (group.getParents() != null) {
				for (GroupEntity parent : group.getParents()) {
					logger.debug("Adding parent group to changed groups: {}", parent.getName());
					allChangedGroups.add(parent);
				}
			}
		}
		
		for (GroupEntity group : allChangedGroups) {
			if (group instanceof ServiceBasedGroupEntity) {
				List<ServiceGroupFlagEntity> groupFlagList = groupFlagDao.findByGroup((ServiceBasedGroupEntity) group);
				for (ServiceGroupFlagEntity groupFlag : groupFlagList) {
					groupFlag.setStatus(ServiceGroupStatus.DIRTY);
					groupFlagDao.persist(groupFlag);
				}
			}
		}
		
		MultipleGroupEvent mge = new MultipleGroupEvent(allChangedGroups);
		try {
			eventSubmitter.submit(mge, EventType.GROUP_UPDATE, auditor.getActualExecutor());
		} catch (EventSubmitException e) {
			logger.warn("Exeption", e);
		}
		
		return allChangedGroups;
	}

	protected Set<GroupEntity> updateSecondary(SamlAccountEntity account, Map<String, List<Object>> attributeMap, Auditor auditor)
			throws UserUpdateException {
		Set<GroupEntity> changedGroups = new HashSet<GroupEntity>();


		String homeId = mapHelper.getSingleStringFirst(attributeMap, SamlGroupUpdater.HOME_ID);

		List<String> groupList = new ArrayList<String>();

		if (homeId == null) {
			logger.warn("No Home ID is set for User {}, resetting secondary groups", account.getGlobalId());
		}
		else if (attributeMap.get(MEMBER_OF) == null) {
			logger.info("No memberOf is set. Resetting secondary groups");
		}
		else {
			List<String> groupsFromAttr = mapHelper.attributeListToStringList(attributeMap, MEMBER_OF);
			
			//Check if a group name contains a ';', and divide this group
			for (String group : groupsFromAttr) {
				if (group.contains(";")) {
					String[] splitGroups = group.split(";");
					for (String g : splitGroups) {
						groupList.add(filterGroup(g));
					}
				}
				else {
					groupList.add(filterGroup(group));
				}
			}
		}
		
		if (account.getGroups() == null)
			account.setGroups(new HashSet<AccountGroupEntity>());

		Set<GroupEntity> groupsFromAssertion = new HashSet<GroupEntity>();

		logger.debug("Looking up groups from database");
		Map<String, HomeOrgGroupEntity> dbGroupMap = new HashMap<String, HomeOrgGroupEntity>();
		logger.debug("Indexing groups from database");
		for (HomeOrgGroupEntity dbGroup : dao.findByNameListAndPrefix(groupList, homeId)) {
			dbGroupMap.put(dbGroup.getName(), dbGroup);
		}

		Ldap ldap = getLdapConnection();
		
		for (String group : groupList) {
			if (group != null && (!group.equals(""))) {

				logger.debug("Analyzing group {}", group);
				HomeOrgGroupEntity groupEntity = dbGroupMap.get(group);
				
				try {
					if (groupEntity == null) {
						// Check for gidNumber from LDAP
						Iterator<SearchResult> iterator = ldap.search(new SearchFilter("cn="+group), new String[] {"gidNumber"});
						if (iterator.hasNext()) {
							SearchResult sr = iterator.next();
							if (sr.getAttributes() != null &&
									sr.getAttributes().get("gidNumber") != null) {
								Object o = sr.getAttributes().get("gidNumber").get();
								if (o instanceof String) {
									Integer gidNumber = Integer.parseInt((String) o);
									
									groupEntity = dao.findByGidNumber(gidNumber);
									
									if (groupEntity == null) {
										logger.info("Creating group {} with gidNumber {}", group, gidNumber);
										groupEntity = dao.createNew();
										groupEntity.setUsers(new HashSet<UserGroupEntity>());
										groupEntity.setName(group);
										groupEntity.setPrefix("ka");
										groupEntity.setGidNumber(gidNumber);
										groupEntity.setIdp(account.getIdp());
										groupEntity = (HomeOrgGroupEntity) groupDao.persistWithServiceFlags(groupEntity);
										auditor.logAction(groupEntity.getName(), "SET FIELD (LDAP)", "idpEntityId", "" + account.getIdp().getEntityId(), AuditStatus.SUCCESS);
										auditor.logAction(groupEntity.getName(), "SET FIELD (LDAP)", "name", groupEntity.getName(), AuditStatus.SUCCESS);
										auditor.logAction(groupEntity.getName(), "SET FIELD (LDAP)", "prefix", groupEntity.getPrefix(), AuditStatus.SUCCESS);
										auditor.logAction(groupEntity.getName(), "SET FIELD (LDAP)", "gidNumber", "" + groupEntity.getGidNumber(), AuditStatus.SUCCESS);
										
										auditor.logAction(groupEntity.getName(), "CREATE GROUP (LDAP)", null, "Group created", AuditStatus.SUCCESS);
										
										changedGroups.add(groupEntity);
									}
									else {
										logger.info("Renaming Group {} to name -> {}", groupEntity.getName(), group);
										auditor.logAction(groupEntity.getName(), "RENAME GROUP (LDAP)", group, groupEntity.getName() + " -> " + group, AuditStatus.SUCCESS);
										groupEntity.setName(group);
										groupEntity = dao.persist(groupEntity);
										auditor.logAction(groupEntity.getName(), "SET FIELD (LDAP)", "name", groupEntity.getName(), AuditStatus.SUCCESS);
										
										changedGroups.add(groupEntity);
									}
								}
								else
									logger.warn("GidNumber not of type String for group {}", group);
							}
							else
								logger.warn("No SearchResult Attributes available from ldap for group {}", group);
						}
						else
							logger.warn("No SearchResults available from kitldap for group {}", group);
					}
				
					if (groupEntity != null) {
						groupsFromAssertion.add(groupEntity);
						
						if (! groupDao.isAccountInGroup(account, groupEntity)) {
							logger.debug("Adding user {} to group {}", account.getGlobalId(), groupEntity.getName());
							groupDao.addAccountToGroup(account, groupEntity, false);
							changedGroups.remove(groupEntity);
							auditor.logAction(account.getGlobalId(), "ADD TO GROUP", groupEntity.getName(), null, AuditStatus.SUCCESS);

							changedGroups.add(groupEntity);
						}
					}
				} catch (NumberFormatException e) {
					logger.warn("GidNumber has a bad number format: {}", e.getMessage());
				} catch (NamingException e) {
					logger.warn("Problem with LDAP: {}", e.getMessage());
				}
			}
		}
		
		
		Set<GroupEntity> groupsToRemove = new HashSet<GroupEntity>(groupDao.findByAccount(account));
		groupsToRemove.removeAll(groupsFromAssertion);

		for (GroupEntity removeGroup : groupsToRemove) {
			if (removeGroup instanceof HomeOrgGroupEntity) {
				logger.debug("Removing user {} from group {}", account.getGlobalId(), removeGroup.getName());
				groupDao.removeAccountFromGroup(account, removeGroup, false);
				
				auditor.logAction(account.getGlobalId(), "REMOVE FROM GROUP", removeGroup.getName(), null, AuditStatus.SUCCESS);

				changedGroups.add(removeGroup);
			}
			else {
				logger.debug("Group {} of type {}. Doing nothing.", removeGroup.getName(), removeGroup.getClass().getSimpleName());
			}
		}
		
		return changedGroups;
	}

	private String filterGroup(String groupName) {
		//Filter all non character from groupName
		groupName = Normalizer.normalize(groupName, Normalizer.Form.NFD);
		groupName = groupName.toLowerCase();
		groupName = groupName.replaceAll("[^a-z0-9\\-_]", "");
		
		return groupName;
	}	

	protected Ldap getLdapConnection() {
		String ldapUrl = "ldap://localhost:636";
		String ldapBase = "ou=Groups,dc=example,dc=com";
		String bindDn = "uid=user1,ou=ProxyUser,dc=example,dc=com";
		String bindPassword = "asdf";
		String ssl = "";
		String connectionStrategy = "RANDOM";
		String scope = "SUBTREE";
		int timeout = 5000;
		
		if (appConfig != null) {
			if (appConfig.getConfigValue("LdapUidNumberGroupHook_ldapUrl") != null) {
				ldapUrl = appConfig.getConfigValue("LdapUidNumberGroupHook_ldapUrl");
			}

			if (appConfig.getConfigValue("LdapUidNumberGroupHook_ldapBase") != null) {
				ldapBase = appConfig.getConfigValue("LdapUidNumberGroupHook_ldapBase");
			}

			if (appConfig.getConfigValue("LdapUidNumberGroupHook_bindDn") != null) {
				bindDn = appConfig.getConfigValue("LdapUidNumberGroupHook_bindDn");
			}
			
			if (appConfig.getConfigValue("LdapUidNumberGroupHook_bindPassword") != null) {
				bindPassword = appConfig.getConfigValue("LdapUidNumberGroupHook_bindPassword");
			}
			
			if (appConfig.getConfigValue("LdapUidNumberGroupHook_ssl") != null) {
				ssl = appConfig.getConfigValue("LdapUidNumberGroupHook_ssl");
			}
			
			if (appConfig.getConfigValue("LdapUidNumberGroupHook_timeout") != null) {
				timeout = Integer.parseInt(appConfig.getConfigValue("LdapUidNumberGroupHook_ssl"));
			}

			if (appConfig.getConfigValue("LdapUidNumberGroupHook_connectionStrategy") != null) {
				connectionStrategy = appConfig.getConfigValue("LdapUidNumberGroupHook_connectionStrategy").toUpperCase(Locale.US);
			}
						
			if (appConfig.getConfigValue("LdapUidNumberGroupHook_scope") != null) {
				scope = appConfig.getConfigValue("LdapUidNumberGroupHook_scope").toUpperCase(Locale.US);
			}
						
		}
		
		LdapConfig config = new LdapConfig(ldapUrl, ldapBase);
		config.setBindDn(bindDn);
		config.setBindCredential(bindPassword);

		config.setSearchScope(SearchScope.valueOf(scope));
		config.getConnectionHandler().setConnectionStrategy(ConnectionStrategy.valueOf(connectionStrategy));
		
		if ("ssl".equals(ssl))
			config.setSsl(true);
		else if ("tls".equals(ssl))
			config.setTls(true);
			
		config.setTimeout(timeout);
		
		Ldap ldap = new Ldap(config);
		return ldap;
	}
}
