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
package edu.kit.scc.webreg.hook;

import java.util.List;
import java.util.Map;

import edu.kit.scc.webreg.audit.Auditor;
import edu.kit.scc.webreg.bootstrap.ApplicationConfig;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.exc.UserUpdateException;

public interface UserServiceHook {

	void setAppConfig(ApplicationConfig appConfig);
	
	void preUpdateUserFromAttribute(UserEntity user, Map<String, List<Object>> attributeMap, Auditor auditor) 
			throws UserUpdateException;

	void postUpdateUserFromAttribute(UserEntity user, Map<String, List<Object>> attributeMap, Auditor auditor) 
			throws UserUpdateException;

	boolean isResponsible(UserEntity user, Map<String, List<Object>> attributeMap);

	boolean isCompleteOverride();

}
