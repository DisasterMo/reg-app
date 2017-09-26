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
package edu.kit.scc.regapp.bpm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.ServiceRegisterAuditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.dao.RegistryDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.dao.audit.AuditDetailDao;
import edu.kit.scc.regapp.dao.audit.AuditEntryDao;
import edu.kit.scc.regapp.entity.BusinessRulePackageEntity;
import edu.kit.scc.regapp.entity.EventType;
import edu.kit.scc.regapp.entity.RegistryEntity;
import edu.kit.scc.regapp.entity.RegistryStatus;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.UserGroupEntity;
import edu.kit.scc.regapp.entity.account.AccountEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.entity.audit.AuditStatus;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.event.ServiceRegisterEvent;
import edu.kit.scc.regapp.exc.EventSubmitException;
import edu.kit.scc.regapp.exc.MisconfiguredApplicationException;
import edu.kit.scc.regapp.exc.MisconfiguredServiceException;

@ApplicationScoped
public class KnowledgeSessionWorker {

	@Inject
	private Logger logger;
	
	@Inject
	private RegistryDao registryDao;
	
	@Inject
	private AuditEntryDao auditDao;

	@Inject
	private AuditDetailDao auditDetailDao;

	@Inject
	private UserDao userDao;

	@Inject
	private ServiceDao serviceDao;
	
	@Inject
	private EventSubmitter eventSubmitter;

	@Inject
	private ApplicationConfig appConfig;
	
	public KieSession getStatefulSession(String packageName, String knowledgeBaseName, String knowledgeBaseVersion) {
		
		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId(packageName, knowledgeBaseName, knowledgeBaseVersion);
		
		return getStatefulSession(ks, releaseId);
	}

	public KieSession getStatefulSession(String unitId) {
		String[] splits = unitId.split(":");
		
		if (splits.length != 3)
			throw new IllegalArgumentException("unitId must contain two :");
		
		return getStatefulSession(splits[0], splits[1], splits[2]);
	}
	
	protected KieSession getStatefulSession(KieServices ks, ReleaseId releaseId) {
		KieContainer kc = ks.newKieContainer(releaseId);
		return kc.newKieSession();
	}

	public List<Object> checkRule(String unitId, UserEntity user, Map<String, List<Object>> attributeMap,
				Assertion assertion, SamlIdpMetadataEntity idp, EntityDescriptor idpEntityDescriptor, SamlSpConfigurationEntity sp) 
			throws MisconfiguredServiceException {
		
		KieSession ksession = getStatefulSession(unitId);

		if (ksession == null)
			throw new MisconfiguredApplicationException("Es ist keine valide Regel fuer den Benutzerzugriff konfiguriert");

		ksession.setGlobal("logger", logger);
		if (user != null)
			ksession.insert(user);
		ksession.insert(attributeMap);
		ksession.insert(assertion);
		ksession.insert(idp);
		ksession.insert(idpEntityDescriptor);
		ksession.insert(sp);
		ksession.insert(new Date());
		
		ksession.fireAllRules();

		List<Object> objectList = new ArrayList<Object>(ksession.getObjects());

		for (Object o : objectList) {
			if (logger.isTraceEnabled())
				logger.trace("Deleting fact handle for Object {}", o);
			FactHandle factHandle = ksession.getFactHandle(o);
			if (factHandle != null)
				ksession.delete(factHandle);
			else
				logger.warn("Facthandle for Object {} is null", o);
		}

		ksession.dispose();

		return objectList;
	}

	public String resolveSamlAccountUpdateMech(String unitId, SamlAccountEntity account, Map<String, List<Object>> attributeMap) 
		throws MisconfiguredServiceException {
	
		KieSession ksession = getStatefulSession(unitId);
	
		if (ksession == null)
			return null;
	
		ksession.setGlobal("logger", logger);
		ksession.insert(account);
		ksession.insert(attributeMap);
		ksession.insert(new Date());
		
		ksession.fireAllRules();
	
		Collection<FactHandle> facts = ksession.getFactHandles(new ObjectFilter() {
			@Override
			public boolean accept(Object object) {
				if (object instanceof String)
					return true;
				else
					return false;
			}
		});
		
		String className = null;
		
		for (FactHandle fact : facts)
			className = ksession.getObject(fact).toString();
		ksession.dispose();
	
		return className;
	}
	
	public boolean checkServiceAccess(UserEntity user, ServiceEntity service) {
		List<Object> objectList;

        if (service.getAccessRule() == null) {
                objectList = checkRule("default", "permitAllRule", "1.0.0", user, service, null, "user-self", false);
        }
        else {
                BusinessRulePackageEntity rulePackage = service.getAccessRule().getRulePackage();
                if (rulePackage != null) {
                        objectList = checkRule(rulePackage.getPackageName(), rulePackage.getKnowledgeBaseName(),
                                rulePackage.getKnowledgeBaseVersion(), user, service, null, "user-" + user.getId(), false);
                }
                else {
                        throw new IllegalStateException("checkServiceAccess called with a rule (" +
                                                service.getAccessRule().getName() + ") that has no rulePackage");
                }
        }

        List<String> requirementsList = new ArrayList<String>();
        for (Object o : objectList) {
                if (o instanceof OverrideAccess) {
                        requirementsList.clear();
                        logger.debug("Removing requirements due to OverrideAccess");
                        break;
                }
                else if (o instanceof UnauthorizedUser) {
                        String s = ((UnauthorizedUser) o).getMessage();
                        requirementsList.add(s);
                }
        }

        if (requirementsList.size() == 0) {
                return true;
        }
        else {
                return false;
        }
	}
	
	public List<ServiceEntity> checkServiceFilterRule(String unitId, UserEntity user, List<ServiceEntity> serviceList) 
			throws MisconfiguredServiceException {
		logger.debug("Starting evaluation of rule {}", unitId);
		long start = System.currentTimeMillis();
		
		KieSession ksession = getStatefulSession(unitId);

		if (ksession == null)
			throw new MisconfiguredApplicationException("Es ist keine valide Regel fuer den Benutzerzugriff konfiguriert");

		ksession.setGlobal("logger", logger);
		ksession.insert(user);
		for (UserGroupEntity uge : user.getGroups())
			ksession.insert(uge.getGroup());
		for (AccountEntity account : user.getAccounts())
			ksession.insert(account);
		for (ServiceEntity service : serviceList)
			ksession.insert(service);
		ksession.insert(new Date());
		
		ksession.fireAllRules();

		List<Object> objectList = new ArrayList<Object>(ksession.getObjects());
		List<ServiceEntity> removeList = new ArrayList<ServiceEntity>();
		
		for (Object o : objectList) {
			if (logger.isTraceEnabled())
				logger.trace("Deleting fact handle for Object {}", o);
			
			FactHandle factHandle = ksession.getFactHandle(o);
			if (factHandle != null)
				ksession.delete(factHandle);
			else
				logger.warn("Facthandle for Object {} is null", o);
			
			if (o instanceof ServiceEntity) {
				removeList.add((ServiceEntity) o);
			}
		}

		ksession.dispose();

		List<ServiceEntity> returnList = new ArrayList<ServiceEntity>(serviceList);
		returnList.removeAll(removeList);

		long end = System.currentTimeMillis();
		logger.debug("Rule evaluation of rule {} took {} ms", unitId, (end-start));
		
		return returnList;
	}
	
	public List<Object> checkRule(String packageName, String knowledgeBaseName, String knowledgeBaseVersion, 
			UserEntity user, ServiceEntity service,
			RegistryEntity registry, String executor) 
			throws MisconfiguredServiceException {
		return checkRule(packageName, knowledgeBaseName, knowledgeBaseVersion, user, service, registry, executor, true);
	}
	
	public List<Object> checkRule(String packageName, String knowledgeBaseName, String knowledgeBaseVersion, 
			UserEntity user, ServiceEntity service,
			RegistryEntity registry, String executor, Boolean withCache) 
			throws MisconfiguredServiceException {

		user = userDao.merge(user);
		
		if (withCache) {
			service = serviceDao.findById(service.getId());

			// Default expiry Time after which an registry is checked
			Long expireTime = 10000L;
			
			if (service.getServiceProps() != null && service.getServiceProps().containsKey("access_check_expire_time")) {
				expireTime = Long.parseLong(service.getServiceProps().get("access_check_expire_time"));
			}
			
			if (registry.getLastAccessCheck() != null &&
					(System.currentTimeMillis() - registry.getLastAccessCheck().getTime()) < expireTime) {
				logger.info("Skipping access check for user {} and service {}", new Object[] {user.getEppn(), 
						service.getName()});
				return null;
			}
		}
		
		KieSession ksession = getStatefulSession(packageName, knowledgeBaseName, knowledgeBaseVersion);

		if (ksession == null)
			throw new MisconfiguredServiceException("Der Registrierungsprozess für den Dienst ist nicht korrekt konfiguriert (Keine Zugangsregel geladen)");

		ksession.setGlobal("logger", logger);
		
		ksession.insert(user);
		ksession.insert(service);
		ksession.insert(registry);
		ksession.insert(new Date());
		
		logger.debug("Test all Rules for service {}", service.getName());
		ksession.fireAllRules();

		List<Object> objectList = new ArrayList<Object>(ksession.getObjects());

		for (Object o : objectList) {
			if (logger.isTraceEnabled())
				logger.trace("Deleting fact handle for Object {}", o);
			FactHandle factHandle = ksession.getFactHandle(o);
			if (factHandle != null)
				ksession.delete(factHandle);
			else
				logger.warn("Facthandle for Object {} is null", o);
		}
		
		ksession.dispose();
		
		if (registry != null) {
			ServiceRegisterEvent serviceRegisterEvent = new ServiceRegisterEvent(registry);

			if (RegistryStatus.LOST_ACCESS.equals(registry.getRegistryStatus()) ||
					RegistryStatus.ON_HOLD.equals(registry.getRegistryStatus())) {
				if (hasAccess(objectList)) {
					logger.debug("{} {} {}: registry status changed from {} to ACTIVE", new Object[] {user.getEppn(), 
							service.getShortName(), registry.getId(), registry.getRegistryStatus()});
					registry.setRegistryStatus(RegistryStatus.ACTIVE);
					registry.setLastStatusChange(new Date());
					auditAccessChange(user, service, registry, false, executor);
					try {
						eventSubmitter.submit(serviceRegisterEvent, EventType.USER_GAINED_ACCESS, executor);
					} catch (EventSubmitException e) {
						logger.warn("Could not submit event", e);
					}
				}
				else {
					logger.debug("{} {} {}: stays in status {}", new Object[] {user.getEppn(), 
							service.getShortName(), registry.getId(), registry.getRegistryStatus()});
				}
			}
			else {
				if (! hasAccess(objectList)) {
					logger.debug("{} {} {}: registry status changed from {} to LOST_ACCESS", new Object[] {user.getEppn(), 
							service.getShortName(), registry.getId(), registry.getRegistryStatus()});
					registry.setRegistryStatus(RegistryStatus.LOST_ACCESS);
					registry.setLastStatusChange(new Date());
					auditAccessChange(user, service, registry, true, executor);
					try {
						eventSubmitter.submit(serviceRegisterEvent, EventType.USER_LOST_ACCESS, executor);
					} catch (EventSubmitException e) {
						logger.warn("Could not submit event", e);
					}
				}
			}
			
			registry.setLastAccessCheck(new Date());
			registry = registryDao.persist(registry);
		}
		
		return objectList;
	}

	public Map<RegistryEntity, List<Object>> checkRules(List<RegistryEntity> registryList, UserEntity user, String executor) {
		return checkRules(registryList, user, executor, true);
	}
	
	public Map<RegistryEntity, List<Object>> checkRules(List<RegistryEntity> registryList, UserEntity user, 
			String executor, Boolean withCache) {
		
		Map<RegistryEntity, List<Object>> returnMap = new HashMap<RegistryEntity, List<Object>>();
		
		user = userDao.findById(user.getId());

		for (RegistryEntity registry : registryList) {
			ServiceEntity service = registry.getService();
			
			List<Object> objectList;
			
			if (service.getAccessRule() == null) {
				objectList = checkRule("default", "permitAllRule", "1.0.0", user, service, registry, executor, withCache);
			}
			else {
				BusinessRulePackageEntity rulePackage = service.getAccessRule().getRulePackage();
				objectList = checkRule(rulePackage.getPackageName(), rulePackage.getKnowledgeBaseName(), 
						rulePackage.getKnowledgeBaseVersion(), user, service, registry, executor, withCache);
			}

			returnMap.put(registry, objectList);
		}
		
		return returnMap;
	}
	
	private boolean hasAccess(List<Object> objectList) {
		
		for (Object o : objectList) {
			if (o instanceof OverrideAccess) {
				return true;
			}
		}
		
		for (Object o : objectList) {
			if (o instanceof UnauthorizedUser) {
				return false;
			}
		}
		return true;
	}
	
	private void auditAccessChange(UserEntity user, ServiceEntity service,
			RegistryEntity registry, boolean lostAccess, String executor) {
		ServiceRegisterAuditor auditor = new ServiceRegisterAuditor(auditDao, auditDetailDao, appConfig);
		auditor.startAuditTrail(executor);
		auditor.setName(getClass().getName() + "-CheckRule-Audit");
		if (lostAccess) {
			auditor.setDetail("User " + user.getEppn() + " lost Access for service " + service.getName());
			auditor.logAction(user.getEppn(), "LOST ACCESS", service.getName(), "User " + user.getEppn() + " lost Access for service " + service.getName(), AuditStatus.SUCCESS);
		}
		else {
			auditor.setDetail("User " + user.getEppn() + " gained Access for service " + service.getName());
			auditor.logAction(user.getEppn(), "GAINED ACCESS", service.getName(), "User " + user.getEppn() + " gained Access for service " + service.getName(), AuditStatus.SUCCESS);
		}
		auditor.setRegistry(registry);

		auditor.finishAuditTrail();
		auditor.commitAuditTrail();
	}
}
