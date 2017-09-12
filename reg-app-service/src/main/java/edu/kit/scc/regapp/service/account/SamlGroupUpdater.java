/*******************************************************************************
 * Copyright (c) 2014 Michael Simon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Michael Simon - initial
 ******************************************************************************/
package edu.kit.scc.regapp.service.account;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.HomeOrgGroupDao;
import edu.kit.scc.regapp.dao.SerialDao;
import edu.kit.scc.regapp.dao.ServiceGroupFlagDao;
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
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.event.MultipleGroupEvent;
import edu.kit.scc.regapp.exc.EventSubmitException;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;

@ApplicationScoped
public class SamlGroupUpdater implements Serializable {

	public static final String SECONDARY_GROUP_ID = "http://bwidm.de/bwidmMemberOf";
	public static final String PRIMARY_GROUP_ID = "http://bwidm.de/bwidmCC";
	public static final String HOME_ID = "http://bwidm.de/bwidmOrgId";

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private HomeOrgGroupDao dao;
	
	@Inject
	private GroupDao groupDao;

	@Inject
	private ServiceGroupFlagDao groupFlagDao;
	
	@Inject
	private AttributeMapHelper attrHelper;

	@Inject 
	private SerialDao serialDao;

	@Inject 
	private EventSubmitter eventSubmitter;
	
	public Set<GroupEntity> updateGroupsForAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap, Auditor auditor)
			throws UserUpdateException {
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


		String homeId = attrHelper.getSingleStringFirst(attributeMap, PRIMARY_GROUP_ID);

		List<String> groupList = new ArrayList<String>();

		if (homeId == null) {
			logger.warn("No Home ID is set for User {}, resetting secondary groups", account.getGlobalId());
		}
		else if (attributeMap.get(SECONDARY_GROUP_ID) == null) {
			logger.info("No http://bwidm.de/bwidmMemberOf is set. Resetting secondary groups");
		}
		else {
			List<String> groupsFromAttr = attrHelper.attributeListToStringList(attributeMap, SECONDARY_GROUP_ID);
			
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
		
		for (String group : groupList) {
			if (group != null && (!group.equals(""))) {

				logger.debug("Analyzing group {}", group);
				HomeOrgGroupEntity groupEntity = dbGroupMap.get(group);
				
				try {
					if (groupEntity == null) {
						int gidNumber = serialDao.next("gid-number-serial").intValue();
						logger.info("Creating group {} with gidNumber {}", group, gidNumber);
						groupEntity = dao.createNew();

						groupEntity.setUsers(new HashSet<UserGroupEntity>());
						groupEntity.setParents(new HashSet<GroupEntity>());
						groupEntity.setName(group);
						auditor.logAction(groupEntity.getName(), "SET FIELD", "name", groupEntity.getName(), AuditStatus.SUCCESS);
						groupEntity.setPrefix(homeId);
						auditor.logAction(groupEntity.getName(), "SET FIELD", "prefix", groupEntity.getPrefix(), AuditStatus.SUCCESS);
						groupEntity.setGidNumber(gidNumber);
						auditor.logAction(groupEntity.getName(), "SET FIELD", "gidNumber", "" + groupEntity.getGidNumber(), AuditStatus.SUCCESS);
						groupEntity.setIdp(account.getIdp());
						auditor.logAction(groupEntity.getName(), "SET FIELD", "idpEntityId", "" + account.getIdp().getEntityId(), AuditStatus.SUCCESS);
						groupEntity = (HomeOrgGroupEntity) groupDao.persistWithServiceFlags(groupEntity);
						auditor.logAction(groupEntity.getName(), "CREATE GROUP", null, "Group created", AuditStatus.SUCCESS);
						
						changedGroups.add(groupEntity);
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
}
