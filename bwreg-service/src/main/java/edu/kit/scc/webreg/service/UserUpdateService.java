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
import java.util.Map;

import org.opensaml.saml2.core.Assertion;

import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.exc.UserUpdateException;

public interface UserUpdateService {

	UserEntity updateUserFromIdp(UserEntity user) throws UserUpdateException;

	UserEntity updateUser(UserEntity user,
			Map<String, List<Object>> attributeMap, String executor,
			ServiceEntity service) throws UserUpdateException;

	UserEntity updateUser(UserEntity user, Assertion assertion,
			String executor, ServiceEntity service) throws UserUpdateException;

	UserEntity updateUserFromIdp(UserEntity user, ServiceEntity service)
			throws UserUpdateException;

	UserEntity updateUser(UserEntity user,
			Map<String, List<Object>> attributeMap, String executor)
			throws UserUpdateException;

}
