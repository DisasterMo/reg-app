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
package edu.kit.scc.webreg.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.webreg.audit.Auditor;
import edu.kit.scc.webreg.dao.BaseDao;
import edu.kit.scc.webreg.dao.RegistryDao;
import edu.kit.scc.webreg.dao.UserDao;
import edu.kit.scc.webreg.entity.GroupEntity;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.RegistryStatus;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.UserStatus;
import edu.kit.scc.webreg.exc.UserUpdateException;
import edu.kit.scc.webreg.service.UserService;

@Stateless
public class UserServiceImpl extends BaseServiceImpl<UserEntity, Long> implements UserService, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Logger logger;
	
	@Inject
	private UserDao dao;
	
	@Inject
	private UserUpdater userUpdater;
	
	@Inject 
	private RegistryDao registryDao;
	
	@Override
	public List<UserEntity> findOrderByUpdatedWithLimit(Date date, Integer limit) {
		return dao.findOrderByUpdatedWithLimit(date, limit);
	}

	@Override
	public List<UserEntity> findOrderByFailedUpdateWithLimit(Date date, Integer limit) {
		return dao.findOrderByFailedUpdateWithLimit(date, limit);
	}

    @Override
	public List<UserEntity> findGenericStoreKeyWithLimit(String key, Integer limit) {
		return dao.findGenericStoreKeyWithLimit(key, limit);
	}
	
	@Override
	public List<UserEntity> findByGroup(GroupEntity group) {
		return dao.findByGroup(group);
	}
	
	@Override
	public List<UserEntity> findByStatus(UserStatus status) {
		return dao.findByStatus(status);
	}
	
	@Override
	public UserEntity findByIdWithAll(Long id) {
		return dao.findByIdWithAll(id);
	}

	@Override
	public UserEntity findByIdWithStore(Long id) {
		return dao.findByIdWithStore(id);
	}

	@Override
	public UserEntity updateUserFromIdp(UserEntity user, String executor) 
			throws UserUpdateException {
		return userUpdater.updateUserFromIdp(user, null, executor);
	}

	@Override
	public UserEntity updateUserFromIdp(UserEntity user, ServiceEntity service, String executor) 
			throws UserUpdateException {
		return userUpdater.updateUserFromIdp(user, executor);
	}

	@Override
	public UserEntity updateUserFromAttribute(UserEntity user, Map<String, List<Object>> attributeMap, String executor) 
				throws UserUpdateException {
		return userUpdater.updateUser(user, attributeMap, executor);
	}

	@Override
	public boolean updateUserFromAttribute(UserEntity user, Map<String, List<Object>> attributeMap, Auditor auditor) 
				throws UserUpdateException {
		return userUpdater.updateUserFromAttribute(user, attributeMap, false, auditor);
	}

	@Override
	public boolean updateUserFromAttribute(UserEntity user, Map<String, List<Object>> attributeMap, boolean withoutUidNumber, Auditor auditor) 
				throws UserUpdateException {

		return userUpdater.updateUserFromAttribute(user, attributeMap, withoutUidNumber, auditor);
	}

	@Override
	protected BaseDao<UserEntity, Long> getDao() {
		return dao;
	}

	@Override
	public void checkOnHoldRegistries(UserEntity user) {
		if (user.getUserStatus().equals(UserStatus.ON_HOLD)) {
			List<RegistryEntity> registryList = registryDao.findByUserAndStatus(user, 
					RegistryStatus.ACTIVE, RegistryStatus.LOST_ACCESS, RegistryStatus.INVALID);
			for (RegistryEntity registry : registryList) {
				logger.debug("Setting registry {} (user {}) ON_HOLD", registry.getId(), user.getEppn()); 
				registry.setRegistryStatus(RegistryStatus.ON_HOLD);
				registry.setLastStatusChange(new Date());
			}			
		}
	}
}
