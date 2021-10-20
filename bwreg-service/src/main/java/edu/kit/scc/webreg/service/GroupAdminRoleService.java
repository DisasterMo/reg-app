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
package edu.kit.scc.webreg.service;

import java.util.List;

import edu.kit.scc.webreg.entity.GroupAdminRoleEntity;
import edu.kit.scc.webreg.entity.UserEntity;

public interface GroupAdminRoleService extends BaseService<GroupAdminRoleEntity> {

	GroupAdminRoleEntity findByName(String name);

	GroupAdminRoleEntity findWithUsers(Long id);

	List<GroupAdminRoleEntity> findWithServices(UserEntity user);
	
}
