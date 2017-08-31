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
package edu.kit.scc.regapp.service.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.SamlSpConfigurationDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.dao.account.SamlAccountDao;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.HomeOrgGroupEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.account.AccountStatus;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.service.account.SamlAccountService;
import edu.kit.scc.regapp.service.impl.BaseServiceImpl;

@Stateless
public class SamlAccountServiceImpl extends BaseServiceImpl<SamlAccountEntity, Long> implements SamlAccountService {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;
	
	@Inject
	private SamlAccountDao dao;
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private GroupDao groupDao;
	
	@Inject
	private SamlSpConfigurationDao samlSpConfigurationDao;
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public SamlAccountEntity convertUserForSamlAccount(UserEntity user) {
		logger.debug("processing user id {} ({})", user.getId(), user.getEppn());
		SamlAccountEntity entity = dao.createNew();
		
		entity.setUser(user);
		entity.setIdp(user.getIdp());
		user.setIdp(null);
		user.setPersistentIdpId(null);
		entity.setPersistentId(user.getPersistentId());
		user.setPersistentId(null);
		entity.setSp(samlSpConfigurationDao.findByEntityId(user.getPersistentSpId()));
		user.setPersistentSpId(null);
		entity.setGlobalId(user.getEppn());
		user.setEppn(null);
		entity.setAccountStatus(AccountStatus.valueOf(user.getUserStatus().name()));
		entity.setLastFailedUpdate(user.getLastFailedUpdate());
		user.setLastFailedUpdate(null);
		entity.setLastStatusChange(user.getLastStatusChange());
		entity.setLastUpdate(user.getLastUpdate());
		user.setLastUpdate(null);
		
		logger.debug("processing account store for user id {} ({})", user.getId(), user.getEppn());
		Map<String, String> accountStore = new HashMap<>();
		accountStore.putAll(user.getAttributeStore());
		entity.setAccountStore(accountStore);
		user.getAttributeStore().clear();

		logger.debug("persisting user id {} ({})", user.getId(), user.getEppn());
		user = userDao.persist(user);

		logger.debug("persisting samlAccount for user id {} ({})", user.getId(), user.getEppn());
		entity.setUser(user);
		entity = dao.persist(entity);
		
		logger.debug("processing groups for user id {} ({})", user.getId(), user.getEppn());
		List<GroupEntity> migrationGroupList = new ArrayList<>();
		List<GroupEntity> allGroupList = groupDao.findByUser(user);
		for (GroupEntity group : allGroupList) {
			if (group instanceof HomeOrgGroupEntity) {
				migrationGroupList.add(group);
			}
		}
		
		logger.debug("migrating {} groups for user id {} ({})", migrationGroupList.size(), user.getId(), user.getEppn());
		for (GroupEntity group : migrationGroupList) {
			groupDao.removeUserFromGroup(user, group, true);
			groupDao.addAccountToGroup(entity, group, true);
		}
		logger.debug("done migrating groups for user id {} ({})", migrationGroupList.size(), user.getId(), user.getEppn());
		return entity;
	}
	
	@Override
	protected BaseDao<SamlAccountEntity, Long> getDao() {
		return dao;
	}
}
