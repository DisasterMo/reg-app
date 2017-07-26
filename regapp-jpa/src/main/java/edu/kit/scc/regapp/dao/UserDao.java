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
package edu.kit.scc.regapp.dao;

import java.util.Date;
import java.util.List;

import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.UserStatus;

public interface UserDao extends BaseDao<UserEntity, Long> {

	UserEntity findByIdWithAll(Long id);
	List<UserEntity> findByPrimaryGroup(GroupEntity group);
	UserEntity findByIdWithStore(Long id);
	List<UserEntity> findByGroup(GroupEntity group);
	List<UserEntity> findOrderByUpdatedWithLimit(Date date, Integer limit);
	List<UserEntity> findGenericStoreKeyWithLimit(String key, Integer limit);
	List<UserEntity> findOrderByFailedUpdateWithLimit(Date date, Integer limit);
	List<UserEntity> findByStatus(UserStatus status);
}
