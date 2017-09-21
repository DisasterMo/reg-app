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

import java.util.List;

import edu.kit.scc.regapp.entity.ImageEntity;
import edu.kit.scc.regapp.entity.RoleEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;

public interface ServiceDao extends BaseDao<ServiceEntity, Long> {

	ServiceEntity findWithPolicies(Long id);

	List<ServiceEntity> findAllWithPolicies();

	List<ServiceEntity> findAllByImage(ImageEntity image);

	ServiceEntity findByIdWithServiceProps(Long id);

	List<ServiceEntity> findByAdminRole(RoleEntity role);

	List<ServiceEntity> findByApproverRole(RoleEntity role);

	ServiceEntity findByShortName(String shortName);

	List<ServiceEntity> findByHotlineRole(RoleEntity role);

	List<ServiceEntity> findByGroupCapability(Boolean capable);

	List<ServiceEntity> findByGroupAdminRole(RoleEntity role);

	List<ServiceEntity> findByParentService(ServiceEntity service);

	List<ServiceEntity> findAvailableForUser(Long userId);

}
