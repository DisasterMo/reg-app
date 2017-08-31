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
package edu.kit.scc.regapp.bootstrap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.slf4j.Logger;

import edu.kit.scc.regapp.dao.AdminUserDao;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.RoleDao;
import edu.kit.scc.regapp.dao.SerialDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.drools.BpmProcessService;
import edu.kit.scc.regapp.entity.AdminUserEntity;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.RoleEntity;
import edu.kit.scc.regapp.entity.SerialEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.service.impl.HookManager;
import edu.kit.scc.regapp.service.mail.TemplateRenderer;
import edu.kit.scc.regapp.service.timer.StandardScheduler;

@Singleton
@Startup
public class ApplicationBootstrap {

	@Inject
	private Logger logger;
	
	@Inject
	private ApplicationConfig appConfig;
	
	@Inject
	private GroupDao groupDao;

	@Inject
	private UserDao userDao;
	
	@Inject
	private RoleDao roleDao;
	
	@Inject
	private ServiceDao serviceDao;
	
	@Inject
	private AdminUserDao adminUserDao;
	
	@Inject
	private SerialDao serialDao;
	
	@Inject
	private StandardScheduler standardScheduler;

	@Inject
	private BpmProcessService bpmProcessService;
	
	@Inject
	private TemplateRenderer velocityRenderer;
	
	@Inject 
	private HookManager hookManager;
	
	@PostConstruct
	public void init() {
		
		logger.info("Initializing Application Configuration");
		appConfig.init();
		
		logger.info("Initializing Serials");
		checkSerial("uid-number-serial", 900000L);
		checkSerial("gid-number-serial", 500000L);

		logger.info("Initializing Groups");
		checkGroup("invalid", 499999);
		
		logger.info("Initializing standard Roles");
    	checkRole("MasterAdmin");
    	checkRole("RoleAdmin");
    	checkRole("UserAdmin");
    	checkRole("GroupAdmin");
    	checkRole("ServiceAdmin");
    	checkRole("RestServiceAdmin");
    	checkRole("SamlAdmin");
    	checkRole("BusinessRuleAdmin");
    	checkRole("BulkAdmin");
    	checkRole("TimerAdmin");
    	checkRole("AuditAdmin");
    	checkRole("User");
    	checkRole("AttributeSourceAdmin");
    	
    	logger.info("Initializing admin Account");
    	if (adminUserDao.findByUsername("admin") == null) {
    		AdminUserEntity a = adminUserDao.createNew();
    		a.setUsername("admin");
    		a.setPassword("secret");
    		Set<RoleEntity> roles = new HashSet<RoleEntity>();
    		roles.add(roleDao.findByName("MasterAdmin"));
    		a.setRoles(roles);
    		adminUserDao.persist(a);
    	}

    	logger.info("Setting PasswordCapable and GroupCapable on all services, according to implemented interfaces");
    	List<ServiceEntity> serviceList = serviceDao.findAll();
    	for (ServiceEntity service : serviceList) {
        	logger.debug("Update capabilities on service {}", service.getName());
        	serviceDao.updateCapabilities(service);
    	}
    	
		logger.info("Initializing Hooks");
    	hookManager.reloadHooks();
		
    	try {
    		logger.info("OpenSAML Bootstrap...");
			InitializationService.initialize();
				        
		} catch (InitializationException e) {
			logger.error("Serious Error happened", e);
		}
        
        bpmProcessService.init();
        
        velocityRenderer.init();
        
        standardScheduler.initialize();
	}
	
    private void checkGroup(String name, Integer createActual) {
    	GroupEntity entity = groupDao.findByName(name);
    	if (entity == null) {
    		entity = groupDao.createNew();
    		entity.setName(name);
    		entity.setGidNumber(createActual);
    		groupDao.persist(entity);
    	}    	
    }

    private void checkRole(String roleName) {
    	if (roleDao.findByName(roleName) == null) {
    		RoleEntity role = roleDao.createNew();
    		role.setName(roleName);
    		roleDao.persist(role);
    	}    	
    }

    private void checkSerial(String serialName, Long createActual) {
    	SerialEntity serial = serialDao.findByName(serialName);
    	if (serial == null) {
    		serial = serialDao.createNew();
    		serial.setName(serialName);
    		serial.setActual(createActual);
    		serialDao.persist(serial);
    	}    	
    }

}
