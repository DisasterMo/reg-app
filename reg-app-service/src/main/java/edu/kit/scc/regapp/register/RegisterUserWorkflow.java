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

import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.entity.RegistryEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.exc.RegisterException;

public interface RegisterUserWorkflow {

	void registerUser(UserEntity user, ServiceEntity service, RegistryEntity registry, Auditor auditor) throws RegisterException;
	void deregisterUser(UserEntity user, ServiceEntity service, RegistryEntity registry, Auditor auditor) throws RegisterException;
	void reconciliation(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor) throws RegisterException;
	Boolean updateRegistry(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor) throws RegisterException;
}
