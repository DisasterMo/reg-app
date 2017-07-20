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

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.webreg.dao.BaseDao;
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
	
	@Override
	public SamlAccountEntity createSamlAccountForUser(UserEntity user) {
		SamlAccountEntity entity = dao.createNew();
		entity.setUser(user);
		
		entity = dao.persist(entity);
		return entity;
	}
	
	@Override
	protected BaseDao<SamlAccountEntity, Long> getDao() {
		return dao;
	}
}
