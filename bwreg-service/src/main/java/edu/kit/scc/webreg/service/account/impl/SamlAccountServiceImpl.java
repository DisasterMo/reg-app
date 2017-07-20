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
package edu.kit.scc.webreg.service.account.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import edu.kit.scc.webreg.dao.BaseDao;
import edu.kit.scc.webreg.dao.SamlSpConfigurationDao;
import edu.kit.scc.webreg.dao.UserDao;
import edu.kit.scc.webreg.dao.account.SamlAccountDao;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.account.SamlAccountEntity;
import edu.kit.scc.webreg.service.account.SamlAccountService;
import edu.kit.scc.webreg.service.impl.BaseServiceImpl;

@Stateless
public class SamlAccountServiceImpl extends BaseServiceImpl<SamlAccountEntity, Long> implements SamlAccountService {

	private static final long serialVersionUID = 1L;

	@Inject
	private SamlAccountDao dao;
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private SamlSpConfigurationDao samlSpConfigurationDao;
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public SamlAccountEntity convertUserForSamlAccount(UserEntity user) {
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
		
		Map<String, String> accountStore = new HashMap<>();
		accountStore.putAll(user.getAttributeStore());
		entity.setAccountStore(accountStore);
		
		user = userDao.persist(user);

		entity.setUser(user);

		entity = dao.persist(entity);
		return entity;
	}
	
	@Override
	protected BaseDao<SamlAccountEntity, Long> getDao() {
		return dao;
	}
}
