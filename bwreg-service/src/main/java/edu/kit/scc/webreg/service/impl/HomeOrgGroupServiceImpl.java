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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.webreg.dao.BaseDao;
import edu.kit.scc.webreg.dao.GroupDao;
import edu.kit.scc.webreg.dao.HomeOrgGroupDao;
import edu.kit.scc.webreg.entity.HomeOrgGroupEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.service.HomeOrgGroupService;

@Stateless
public class HomeOrgGroupServiceImpl extends BaseServiceImpl<HomeOrgGroupEntity, Long> implements HomeOrgGroupService {

	private static final long serialVersionUID = 1L;

	@Inject
	private HomeOrgGroupDao dao;
	
	@Inject
	private GroupDao groupDao;
	
	@Override
	public HomeOrgGroupEntity findWithUsers(Long id) {
		return dao.findWithUsers(id);
	}	
	
	@Override
	public HomeOrgGroupEntity findByName(String name) {
		return dao.findByName(name);
	}	
	
	@Override
	public List<HomeOrgGroupEntity> findByUser(UserEntity user) {
		return dao.findByUser(user);
	}	
	
	@Override
	public HomeOrgGroupEntity persistWithServiceFlags(HomeOrgGroupEntity entity) {
		return (HomeOrgGroupEntity) groupDao.persistWithServiceFlags(entity);
	}	
	
	@Override
	protected BaseDao<HomeOrgGroupEntity, Long> getDao() {
		return dao;
	}	
}
