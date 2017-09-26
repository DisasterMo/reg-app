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
package edu.kit.scc.regapp.register;

import java.util.Set;

import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.RegistryEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.exc.RegisterException;

public interface RegisterUserService {

	Boolean checkWorkflow(String name);

	RegisterUserWorkflow getWorkflowInstance(String className);

	void reconsiliation(Long registryId, Boolean fullRecon,
			String executor, Auditor parentAuditor) throws RegisterException;

	void deprovision(Long registryId, String executor) throws RegisterException;
	
	void purge(Long registryId, String executor) throws RegisterException;
	
	void setPassword(Long registryId, String password, String executor)
			throws RegisterException;

	void deleteGroup(GroupEntity group, ServiceEntity service, String executor)
			throws RegisterException;

	void completeReconciliation(ServiceEntity service, Boolean fullRecon,
			Boolean withGroups, String executor);

	void updateGroups(Set<GroupEntity> groupUpdateSet, String executor)
			throws RegisterException;

	void deletePassword(Long registryId, String executor) throws RegisterException;

	void reconGroupsForRegistry(RegistryEntity registry, String executor)
			throws RegisterException;

	void reconsiliation(Long registryId, Boolean fullRecon,
			String executor) throws RegisterException;

	void registerUser(UserEntity user, ServiceEntity service, String executor,
			Boolean sendGroupUpdate, Auditor parentAuditor)
			throws RegisterException;

	void registerUser(Long userId, Long serviceId, String executor) throws RegisterException;

	void deregisterUser(Long registryId, String executor) throws RegisterException;
	
}
