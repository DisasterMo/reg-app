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
package edu.kit.scc.webreg.dao;

import java.util.List;

import edu.kit.scc.webreg.entity.ApproverRoleEntity;
import edu.kit.scc.webreg.entity.UserEntity;

public interface ApproverRoleDao extends BaseDao<ApproverRoleEntity> {

	ApproverRoleEntity findByName(String name);

	ApproverRoleEntity findWithUsers(Long id);

	List<ApproverRoleEntity> findWithServices(UserEntity user);
}
