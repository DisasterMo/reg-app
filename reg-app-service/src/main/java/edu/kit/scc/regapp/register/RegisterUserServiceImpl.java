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
package edu.kit.scc.regapp.register;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.audit.GroupAuditor;
import edu.kit.scc.regapp.audit.RegistryAuditor;
import edu.kit.scc.regapp.audit.ServiceAuditor;
import edu.kit.scc.regapp.audit.ServiceRegisterAuditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.bpm.KnowledgeSessionWorker;
import edu.kit.scc.regapp.bpm.MissingMandatoryValues;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.RegistryDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dao.ServiceEventDao;
import edu.kit.scc.regapp.dao.ServiceGroupFlagDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.dao.audit.AuditDetailDao;
import edu.kit.scc.regapp.dao.audit.AuditEntryDao;
import edu.kit.scc.regapp.entity.AgreementTextEntity;
import edu.kit.scc.regapp.entity.ApproverRoleEntity;
import edu.kit.scc.regapp.entity.BusinessRulePackageEntity;
import edu.kit.scc.regapp.entity.EventEntity;
import edu.kit.scc.regapp.entity.EventType;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.PolicyEntity;
import edu.kit.scc.regapp.entity.RegistryEntity;
import edu.kit.scc.regapp.entity.RegistryStatus;
import edu.kit.scc.regapp.entity.ServiceBasedGroupEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.ServiceGroupFlagEntity;
import edu.kit.scc.regapp.entity.ServiceGroupStatus;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.UserGroupEntity;
import edu.kit.scc.regapp.entity.UserStatus;
import edu.kit.scc.regapp.entity.audit.AuditDetailEntity;
import edu.kit.scc.regapp.entity.audit.AuditServiceRegisterEntity;
import edu.kit.scc.regapp.entity.audit.AuditStatus;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.event.MultipleGroupEvent;
import edu.kit.scc.regapp.event.ServiceRegisterEvent;
import edu.kit.scc.regapp.exc.EventSubmitException;
import edu.kit.scc.regapp.exc.RegisterException;
import edu.kit.scc.regapp.service.tools.PasswordUtil;

@Stateless
public class RegisterUserServiceImpl implements RegisterUserService {

	@Inject
	private Logger logger;

	@Inject
	private RegistryDao registryDao;

	@Inject
	private ServiceDao serviceDao;

	@Inject
	private ServiceGroupFlagDao groupFlagDao;
	
	@Inject
	private ServiceEventDao serviceEventDao;
	
	@Inject
	private UserDao userDao;

	@Inject
	private GroupDao groupDao;

	@Inject
	private GroupUtil groupUtil;

	@Inject
	private PasswordUtil passwordUtil;
	
	@Inject
	private ApprovalService approvalService;

	@Inject
	private AuditEntryDao auditDao;

	@Inject
	private AuditDetailDao auditDetailDao;

	@Inject
	private KnowledgeSessionWorker knowledgeSessionService;
	
	@Inject
	private EventSubmitter eventSubmitter;
	
	@Inject
	private ApplicationConfig appConfig;
	
	@Override
	public void registerUser(Long userId, Long serviceId, String executor)
			throws RegisterException {
		UserEntity user = userDao.findById(userId);
		ServiceEntity service = serviceDao.findById(serviceId);
		registerUser(user, service, executor, true, null);
	}

	@Override
	public void registerUser(UserEntity user, ServiceEntity service, String executor, Boolean sendGroupUpdate, Auditor parentAuditor)
			throws RegisterException {
		
		if (! UserStatus.ACTIVE.equals(user.getUserStatus())) {
			logger.warn("Only Users in status ACTIVE can register with a service. User {} is {}", "user-" + user.getId(), user.getUserStatus());
			throw new RegisterException("Only Users in status ACTIVE can register with a service");
		}
        
		if (! knowledgeSessionService.checkServiceAccess(user, service)) {
			logger.warn("user-{} is denied registration.", user.getId());
			throw new RegisterException("Unauthorized");
		}
        
		ServiceRegisterAuditor auditor = new ServiceRegisterAuditor(auditDao, auditDetailDao, appConfig);
		auditor.startAuditTrail(executor, true);
		auditor.setName(this.getClass().getName() + "-ServiceRegister-Audit");
		auditor.setDetail("Register user " + user.getId() + " for service " + service.getName());
		auditor.setParent(parentAuditor);

		if (service.getParentService() != null) {
			logger.info("Service has Parent. Checking parent first.");
			List<RegistryEntity> r = registryDao.findByServiceAndUserAndNotStatus(service.getParentService(), user, 
					RegistryStatus.DELETED, RegistryStatus.DEPROVISIONED);
			if (r.size() == 0) {
				logger.info("User {} is not registered with parent service {} yet", "user-" + user.getId(), service.getParentService().getName());
				registerUser(user, service.getParentService(), executor, true, auditor);
			}
			else {
				logger.debug("User {} is already registered with parent service {}", "user-" + user.getId(), service.getParentService().getName());
			}
		}
		
		RegistryEntity registry = registryDao.createNew();
		
		try {
			ApproverRoleEntity approverRole = service.getApproverRole();
			
			Set<AgreementTextEntity> agrs = new HashSet<AgreementTextEntity>();
			for (PolicyEntity policy : service.getPolicies())
				agrs.add(policy.getActualAgreement());
			registry.setAgreedTexts(agrs);
			registry.setAgreedTime(new Date());
			registry.setService(service);
			registry.setUser(user);
			registry.setRegisterBean(service.getRegisterBean());
			
			if (approverRole != null)
				registry.setApprovalBean(approverRole.getApprovalBean());
			
			registry.setRegistryValues(new HashMap<String, String>());
			
			registry.setRegistryStatus(RegistryStatus.CREATED);
			registry.setLastStatusChange(new Date());
			
			registry = registryDao.persist(registry);

			auditor.logAction("user-" + user.getId(), "CREATED REGISTRY", "registry-" + registry.getId(), "Registry is created", AuditStatus.SUCCESS);
			auditor.setRegistry(registry);

			if (registry.getApprovalBean() != null) {
				logger.debug("Registering {} for approval {}", "user-" + user.getId(), registry.getApprovalBean());
				auditor.logAction("user-" + user.getId(), "STARTING APPROVAL", "registry-" + registry.getId(), "Approval is started: " + registry.getApprovalBean(), AuditStatus.SUCCESS);
				auditor.finishAuditTrail();
				if (parentAuditor == null)
					auditor.commitAuditTrail();
				approvalService.registerApproval(registry, auditor);
			}
			else {
				logger.debug("No approval role for service {}. AutoApproving {}", service.getName(), "user-" + user.getId());
				auditor.logAction("user-" + user.getId(), "STARTING AUTO APPROVE", "registry-" + registry.getId(), "Autoapproving registry", AuditStatus.SUCCESS);
				auditor.finishAuditTrail();
				if (parentAuditor == null)
					auditor.commitAuditTrail();
				approvalService.approve(registry, executor, auditor);
			}
			
		} catch (Throwable t) {
			throw new RegisterException(t);
		}    			
	}

	protected void updateGroups(GroupPerServiceList groupUpdateList, String executor) throws RegisterException {

		for (ServiceEntity service : groupUpdateList.getServices()) {
			RegisterUserWorkflow workflow = getWorkflowInstance(service.getRegisterBean());
			if (! (workflow instanceof GroupCapable)) {
				logger.warn("Workflow " + workflow.getClass() + " is not GroupCapable!");
				continue;
			}
			
			try {
				ServiceAuditor auditor = new ServiceAuditor(auditDao, auditDetailDao, appConfig);
				auditor.startAuditTrail(executor);
				auditor.setName(workflow.getClass().getName() + "-GroupUpdate-Audit");
				auditor.setDetail("Update groups for service " + service.getName());
				auditor.setService(service);

				long a = System.currentTimeMillis();
				
				Set<GroupEntity> groups = new HashSet<GroupEntity>();
				for (ServiceGroupFlagEntity groupFlag : groupUpdateList.getGroupFlagsForService(service))
					groups.add(groupFlag.getGroup());
				logger.debug("Hashing Groups took {} ms", (System.currentTimeMillis() - a)); a = System.currentTimeMillis();
				
				Set<GroupEntity> groupsToRemove = new HashSet<GroupEntity>();
				if (service.getGroupFilterRulePackage() != null) {
					a = System.currentTimeMillis();

					BusinessRulePackageEntity rulePackage = service.getGroupFilterRulePackage();
					KieSession ksession = knowledgeSessionService.getStatefulSession(rulePackage.getPackageName(), rulePackage.getKnowledgeBaseName(), 
							rulePackage.getKnowledgeBaseVersion());

					ksession.setGlobal("logger", logger);
					for (GroupEntity group : groups) {
						ksession.insert(group);
					}
					
					ksession.fireAllRules();
					List<Object> objectList = new ArrayList<Object>(ksession.getObjects());

					Set<GroupEntity> filteredGroups = new HashSet<GroupEntity>();
					for (Object o : objectList) {
						ksession.delete(ksession.getFactHandle(o));
						if (o instanceof GroupEntity)
							filteredGroups.add((GroupEntity) o);
					}

					groupsToRemove.addAll(groups);
					groupsToRemove.removeAll(filteredGroups);
					
					groups = filteredGroups;
					
					logger.debug("Applying group filter drools took {} ms", (System.currentTimeMillis() - a)); a = System.currentTimeMillis();
				}
				
				a = System.currentTimeMillis();
				List<UserEntity> userInServiceList = registryDao.findUserListByServiceAndStatus(service, RegistryStatus.ACTIVE);
				logger.debug("RegistryList took {}ms", (System.currentTimeMillis() - a)); a = System.currentTimeMillis();

				logger.debug("Building group update struct for {}", service.getName());
				GroupUpdateStructure updateStruct = new GroupUpdateStructure();
				for (GroupEntity group : groups) {
					a = System.currentTimeMillis();
					Set<UserEntity> users = groupUtil.rollUsersForGroup(group);
					logger.debug("RollGroup {} took {} ms", group.getName(), (System.currentTimeMillis() - a)); a = System.currentTimeMillis();

					users.retainAll(userInServiceList);
					
					updateStruct.addGroup(group, users);
				}

				for (GroupEntity group : groupsToRemove) {
					updateStruct.addGroup(group, new HashSet<UserEntity>());
				}
				
				((GroupCapable) workflow).updateGroups(service, updateStruct, auditor);
				logger.debug("updateGroups took {}ms", (System.currentTimeMillis() - a)); a = System.currentTimeMillis();

				for (ServiceGroupFlagEntity groupFlag : groupUpdateList.getGroupFlagsForService(service)) {
					groupFlag.setStatus(ServiceGroupStatus.CLEAN);
					groupFlagDao.persist(groupFlag);
				}
				logger.debug("Persist service Flags took {}ms", (System.currentTimeMillis() - a)); a = System.currentTimeMillis();

				auditor.finishAuditTrail();
				auditor.commitAuditTrail();

			}
			catch (Throwable t) {
				throw new RegisterException(t);
			}
		}		
	}
	
	@Override
	public void updateGroups(Set<GroupEntity> groupUpdateSet, String executor) throws RegisterException {
		GroupPerServiceList groupUpdateList = new GroupPerServiceList();
		
		for (GroupEntity group : groupUpdateSet) {
			logger.debug("Analyzing group {} of type {}", group.getName(), group.getClass().getSimpleName());
			
			if (group instanceof ServiceBasedGroupEntity) {
				ServiceBasedGroupEntity serviceBasedGroup = (ServiceBasedGroupEntity) groupDao.findById(group.getId());

				for (ServiceGroupFlagEntity flag : serviceBasedGroup.getServiceGroupFlags()) {
					if (ServiceGroupStatus.DIRTY.equals(flag.getStatus())) {
						logger.info("Group {} at Service {} needs an update", serviceBasedGroup.getName(), flag.getService().getName());
						groupUpdateList.addGroupToUpdate(flag);
					}
					else if (ServiceGroupStatus.TO_DELETE.equals(flag.getStatus())) {
						logger.info("Group {} at Service {} is about to get deleted", serviceBasedGroup.getName(), flag.getService().getName());
						try {
							deleteGroup(serviceBasedGroup, flag.getService(), executor);
							groupFlagDao.delete(flag);
						} catch (RegisterException e) {
							logger.warn("Could not delete group: " + e);
						}
					}
				}
			}
			else {
				logger.debug("Group {} is no ServiceBasedGroup. Doin' nuthin at all for now.");
			}
		}

		updateGroups(groupUpdateList, executor);
	}
	
	@Override
	public void deleteGroup(GroupEntity group, ServiceEntity service, String executor) throws RegisterException {
		group = groupDao.findWithUsers(group.getId());

		RegisterUserWorkflow workflow = getWorkflowInstance(service.getRegisterBean());
		if (! (workflow instanceof GroupCapable)) {
			logger.warn("Workflow " + workflow.getClass() + " is not GroupCapable! But Group will be deleted anyway.");
			return;
		}

		try {
			GroupAuditor auditor = new GroupAuditor(auditDao, auditDetailDao, appConfig);
			auditor.startAuditTrail(executor);
			auditor.setName(workflow.getClass().getName() + "-GroupDelete-Audit");
			auditor.setDetail("Delete group " + group.getName() + " (" + group.getGidNumber() + ") for service " + service.getName());
			auditor.setGroup(group);
			
			((GroupCapable) workflow).deleteGroup(group, service, auditor);
			
			auditor.finishAuditTrail();
			auditor.commitAuditTrail();
		}
		catch (Throwable t) {
			throw new RegisterException(t);
		}
	}

	@Override
	public void reconsiliation(Long registryId, Boolean fullRecon, String executor) throws RegisterException {
		reconsiliation(registryId, fullRecon, executor, null);
	}
	
	@Override
	public void reconsiliation(Long registryId, Boolean fullRecon, String executor, Auditor parentAuditor) throws RegisterException {

		RegistryEntity registry = registryDao.findById(registryId);
		
		RegisterUserWorkflow workflow = getWorkflowInstance(registry.getRegisterBean());

		try {
			ServiceEntity serviceEntity = serviceDao.findById(registry.getService().getId());
			UserEntity user = userDao.findById(registry.getUser().getId());
			
			RegistryAuditor auditor = new RegistryAuditor(auditDao, auditDetailDao, appConfig);
			auditor.startAuditTrail(executor);
			auditor.setName(workflow.getClass().getName() + "-Reconsiliation-Audit");
			auditor.setDetail("Recon user " + "user-" + user.getId() + " for service " + serviceEntity.getName());
			auditor.setParent(parentAuditor);
			auditor.setRegistry(registry);

			Boolean missingMandatoryValues = false;
			
			if (registry.getService().getMandatoryValueRulePackage() != null) {
				
				BusinessRulePackageEntity rulePackage = registry.getService().getMandatoryValueRulePackage();
				KieSession ksession = knowledgeSessionService.getStatefulSession(rulePackage.getPackageName(), rulePackage.getKnowledgeBaseName(), 
						rulePackage.getKnowledgeBaseVersion());
	
				ksession.setGlobal("logger", logger);
				ksession.insert(registry);
				ksession.insert(user);
				ksession.insert(serviceEntity);
				
				ksession.fireAllRules();
				List<Object> objectList = new ArrayList<Object>(ksession.getObjects());
	
				for (Object o : objectList) {
					ksession.delete(ksession.getFactHandle(o));
					if (o instanceof MissingMandatoryValues)
						missingMandatoryValues = true;
				}
			}
			
			if (missingMandatoryValues) {
				logger.info("Missing mandatory values! Skipping update (user-{}, service: {}, registry: {})", user.getId(), serviceEntity.getName(), registry.getId());
				if (RegistryStatus.ACTIVE.equals(registry.getRegistryStatus())) {
					registry.setRegistryStatus(RegistryStatus.INVALID);
					registry.setLastStatusChange(new Date());
				}
			}
			else {
				Boolean updated = workflow.updateRegistry(user, serviceEntity, registry, auditor);

				if (RegistryStatus.INVALID.equals(registry.getRegistryStatus())) {
					registry.setRegistryStatus(RegistryStatus.ACTIVE);
					registry.setLastStatusChange(new Date());
				}

				if (fullRecon) {
					logger.debug("Doing full reconsiliation (user-{}, service: {}, registry: {})", user.getId(), serviceEntity.getName(), registry.getId());
					workflow.reconciliation(user, serviceEntity, registry, auditor);
				}
				else if (updated) {
					logger.debug("Changes detected, starting reconcile (user-{}, service: {}, registry: {})", user.getId(), serviceEntity.getName(), registry.getId());
					workflow.reconciliation(user, serviceEntity, registry, auditor);
				} 
				else {
					logger.debug("No Changes detected (user-{}, service: {}, registry: {})", user.getId(), serviceEntity.getName(), registry.getId());
				}
			}

			registry.setLastReconcile(new Date());
			registry = registryDao.persist(registry);

			auditor.finishAuditTrail();
			if (parentAuditor == null)
				auditor.commitAuditTrail();

		} catch (Throwable t) {
			throw new RegisterException(t);
		}    	
	}

	@Override
	public void deregisterUser(Long registryId, String executor) throws RegisterException {
		
		RegistryEntity registry = registryDao.findById(registryId);
		
		if (RegistryStatus.DELETED.equals(registry.getRegistryStatus())) {
			throw new RegisterException("Registry " + registry.getId() + " is already deregistered!");
		}
		
		RegisterUserWorkflow workflow = getWorkflowInstance(registry.getRegisterBean());

		try {
			ServiceEntity serviceEntity = serviceDao.findByIdWithServiceProps(registry.getService().getId());
			UserEntity userEntity = userDao.findByIdWithAll(registry.getUser().getId());

			ServiceRegisterAuditor auditor = new ServiceRegisterAuditor(auditDao, auditDetailDao, appConfig);
			auditor.startAuditTrail(executor);
			auditor.setName(workflow.getClass().getName() + "-Deregister-Audit");
			auditor.setDetail("Deregister user-" + registry.getUser().getId() + " for service " + serviceEntity.getName());
			auditor.setRegistry(registry);
			
			workflow.deregisterUser(userEntity, serviceEntity, registry, auditor);

			registry.setRegistryStatus(RegistryStatus.DELETED);
			registry.setLastStatusChange(new Date());
			registry = registryDao.persist(registry);

			HashSet<GroupEntity> userGroups = new HashSet<GroupEntity>(userEntity.getGroups().size());

			for (UserGroupEntity userGroup : userEntity.getGroups()) {
				GroupEntity group = userGroup.getGroup();
				userGroups.add(group);

				if (group instanceof ServiceBasedGroupEntity) {
					List<ServiceGroupFlagEntity> groupFlagList = groupFlagDao.findByGroupAndService((ServiceBasedGroupEntity) group, serviceEntity);
					for (ServiceGroupFlagEntity groupFlag : groupFlagList) {
						groupFlag.setStatus(ServiceGroupStatus.DIRTY);
						groupFlag = groupFlagDao.persist(groupFlag);
					}
				}
			}
			
			MultipleGroupEvent mge = new MultipleGroupEvent(userGroups);
			try {
				eventSubmitter.submit(mge, EventType.GROUP_UPDATE, auditor.getActualExecutor());
			} catch (EventSubmitException e) {
				logger.warn("Exeption", e);
			}
			
			ServiceRegisterEvent serviceRegisterEvent = new ServiceRegisterEvent(registry);
			List<EventEntity> eventList = new ArrayList<EventEntity>(serviceEventDao.findAllByService(serviceEntity));
			eventSubmitter.submit(serviceRegisterEvent, eventList, EventType.SERVICE_DEREGISTER, executor);
			
			auditor.finishAuditTrail();
			auditor.commitAuditTrail();

		} catch (RegisterException e) {
			throw e;
		} catch (Throwable t) {
			throw new RegisterException(t);
		}    	
	}
	
	@Override
	public void setPassword(Long registryId, String password, String executor) throws RegisterException {
		RegistryEntity registry = registryDao.findById(registryId);

		RegisterUserWorkflow workflow = getWorkflowInstance(registry.getRegisterBean());

		try {
			ServiceEntity serviceEntity = serviceDao.findByIdWithServiceProps(registry.getService().getId());
			UserEntity userEntity = userDao.findByIdWithAll(registry.getUser().getId());

			ServiceRegisterAuditor auditor = new ServiceRegisterAuditor(auditDao, auditDetailDao, appConfig);
			auditor.startAuditTrail(executor);
			auditor.setName(workflow.getClass().getName() + "-SetPassword-Audit");
			auditor.setDetail("Setting service password for user-" + registry.getUser().getId() + " for service " + serviceEntity.getName());
			auditor.setRegistry(registry);
			
			if (serviceEntity.getServiceProps().containsKey("pw_location") && 
					serviceEntity.getServiceProps().get("pw_location").equalsIgnoreCase("registry")) {
				registry.getRegistryValues().put("userPassword", passwordUtil.generatePassword("SHA-512", password));
			}
			else if (serviceEntity.getServiceProps().containsKey("pw_location") && 
					serviceEntity.getServiceProps().get("pw_location").equalsIgnoreCase("both")) {
				registry.getRegistryValues().put("userPassword", passwordUtil.generatePassword("SHA-512", password));
				((SetPasswordCapable) workflow).setPassword(userEntity, serviceEntity, registry, auditor, password);
			}
			else {
				((SetPasswordCapable) workflow).setPassword(userEntity, serviceEntity, registry, auditor, password);
			}

			auditor.finishAuditTrail();
			auditor.commitAuditTrail();

		} catch (RegisterException e) {
			throw e;
		} catch (Throwable t) {
			throw new RegisterException(t);
		}    			
	}
	
	@Override
	public void deletePassword(Long registryId, String executor) throws RegisterException {
		RegistryEntity registry = registryDao.findById(registryId);

		RegisterUserWorkflow workflow = getWorkflowInstance(registry.getRegisterBean());

		try {
			ServiceEntity serviceEntity = serviceDao.findByIdWithServiceProps(registry.getService().getId());
			UserEntity userEntity = userDao.findByIdWithAll(registry.getUser().getId());

			ServiceRegisterAuditor auditor = new ServiceRegisterAuditor(auditDao, auditDetailDao, appConfig);
			auditor.startAuditTrail(executor);
			auditor.setName(workflow.getClass().getName() + "-DeletePassword-Audit");
			auditor.setDetail("Delete service password for user-" + registry.getUser().getId() + " for service " + serviceEntity.getName());
			auditor.setRegistry(registry);
			
			registry.getRegistryValues().remove("userPassword");
			((SetPasswordCapable) workflow).deletePassword(userEntity, serviceEntity, registry, auditor);

			auditor.finishAuditTrail();
			auditor.commitAuditTrail();
		} catch (RegisterException e) {
			throw e;
		} catch (Throwable t) {
			throw new RegisterException(t);
		}    			
	}
	
	@Override
	public Boolean checkWorkflow(String name) {
		if (getWorkflowInstance(name) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public RegisterUserWorkflow getWorkflowInstance(String className) {
		try {
			Object o = Class.forName(className).newInstance();
			if (o instanceof RegisterUserWorkflow)
				return (RegisterUserWorkflow) o;
			else {
				logger.warn("Service Register bean misconfigured, Object not Type RegisterUserWorkflow but: {}", o.getClass());
				return null;
			}
		} catch (InstantiationException e) {
			logger.warn("Service Register bean misconfigured: {}", e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			logger.warn("Service Register bean misconfigured: {}", e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			logger.warn("Service Register bean misconfigured: {}", e.getMessage());
			return null;
		}
	}
	
	@Override
	public void reconGroupsForRegistry(RegistryEntity registry, String executor) throws RegisterException {
		ServiceEntity serviceEntity = serviceDao.findByIdWithServiceProps(registry.getService().getId());
		UserEntity userEntity = userDao.findByIdWithAll(registry.getUser().getId());

		HashSet<GroupEntity> userGroups = new HashSet<GroupEntity>(userEntity.getGroups().size());

		for (UserGroupEntity userGroup : userEntity.getGroups()) {
			GroupEntity group = userGroup.getGroup();
			userGroups.add(group);

			if (group instanceof ServiceBasedGroupEntity) {
				List<ServiceGroupFlagEntity> groupFlagList = groupFlagDao.findByGroupAndService((ServiceBasedGroupEntity) group, serviceEntity);
				for (ServiceGroupFlagEntity groupFlag : groupFlagList) {
					groupFlag.setStatus(ServiceGroupStatus.DIRTY);
					groupFlag = groupFlagDao.persist(groupFlag);
				}
			}
		}
		
		MultipleGroupEvent mge = new MultipleGroupEvent(userGroups);
		try {
			eventSubmitter.submit(mge, EventType.GROUP_UPDATE, executor);
		} catch (EventSubmitException e) {
			logger.warn("Exeption", e);
		}
	}
	
	@Override
	@Asynchronous
	public void completeReconciliation(ServiceEntity service, Boolean fullRecon, Boolean withGroups, String executor) {
		List<RegistryEntity> registryList = registryDao.findByServiceAndStatus(service, RegistryStatus.ACTIVE);
		
		logger.info("Found {} registries for service {}", registryList.size(), service.getName());
		
		for (RegistryEntity registry : registryList) {
			logger.info("Recon registry {}", registry.getId());
			try {
				reconsiliation(registry.getId(), fullRecon, executor);
			} catch (RegisterException e) {
				logger.warn("Could not recon registry {}: {}", registry.getId(), e);
			}
		}
		
		if (withGroups && service.getGroupCapable()) {
			HashSet<GroupEntity> groups = new HashSet<GroupEntity>();
			List<HashSet<GroupEntity>> chunkList = new ArrayList<>();
			
			List<ServiceGroupFlagEntity> groupFlagList = groupFlagDao.findByService(service);

			logger.info("Setting groupFlags to dirty");
			int i = 0;
			
			for (ServiceGroupFlagEntity groupFlag : groupFlagList) {
				if ((i % 50) == 0) {
					groups = new HashSet<GroupEntity>();
					chunkList.add(groups);
				}
				groupFlag.setStatus(ServiceGroupStatus.DIRTY);
				groupFlag = groupFlagDao.persist(groupFlag);
				groups.add(groupFlag.getGroup());
				i++;
			}

			logger.info("Sending Group Update Events");
			
			for (HashSet<GroupEntity> innerGroup : chunkList) {
				MultipleGroupEvent mge = new MultipleGroupEvent(innerGroup);
				try {
					eventSubmitter.submit(mge, EventType.GROUP_UPDATE, executor);
				} catch (EventSubmitException e) {
					logger.warn("Exeption", e);
				}
			}
		}
		
		logger.info("Reconciliation is done.");
	}

	@Override
	public void deprovision(Long registryId, String executor) throws RegisterException {
		RegistryEntity registry = registryDao.findById(registryId);

		if (! RegistryStatus.DELETED.equals(registry.getRegistryStatus())) {
			throw new RegisterException("only registry with status deleted can be deprovisioned");
		}
			
		ServiceRegisterAuditor auditor = new ServiceRegisterAuditor(auditDao, auditDetailDao, appConfig);
		auditor.startAuditTrail(executor);
		auditor.setName(registry.getService().getShortName() + "-Deregister-Audit");
		auditor.setDetail("Deprovision user-" + registry.getUser().getId() + " for service " + registry.getService().getShortName());
		auditor.setRegistry(registry);
		
		registry.setRegistryStatus(RegistryStatus.DEPROVISIONED);
		registry.setLastStatusChange(new Date());

		auditor.finishAuditTrail();
		auditor.commitAuditTrail();
	}

	@Override
	public void purge(Long registryId, String executor) throws RegisterException {
		RegistryEntity registry = registryDao.findById(registryId);
		if (RegistryStatus.ACTIVE.equals(registry.getRegistryStatus()) || 
				RegistryStatus.INVALID.equals(registry.getRegistryStatus()) ||
				RegistryStatus.LOST_ACCESS.equals(registry.getRegistryStatus()) ||
				RegistryStatus.BLOCKED.equals(registry.getRegistryStatus())) {
			
			/*
			 * Deregister user first, if the registration is somewhat active
			 */
			deregisterUser(registry.getId(), executor);
		}
		
		List<AuditServiceRegisterEntity> auditList = auditDao.findAllServiceRegister(registry);
		
		logger.info("There are {} AuditServiceRegisterEntity for Registry {} to be deleted", auditList.size(), registry.getId());
		
		for (AuditServiceRegisterEntity audit : auditList) {
			logger.debug("Deleting audit {} with {} auditentries", audit.getId(), audit.getAuditDetails().size());
			for (AuditDetailEntity detail : audit.getAuditDetails()) {
				auditDetailDao.delete(detail);
			}
			auditDao.delete(audit);
		}

		registryDao.delete(registry);
	}
}
