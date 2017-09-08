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
package edu.kit.scc.regapp.session;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.regapp.entity.RoleEntity;

@Named("sessionManager")
@ApplicationScoped
public class SessionManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SessionData session;

	@PostConstruct
	public void init() {
	}

	public Long getUserId() {
		return session.getUserId();
	}

	public void setUserId(Long userId) {
		session.setUserId(userId);
	}

	public void addAuthMechData(String key, Object value) {
		session.getAuthMechData().put(key, value);
	}
	
	public Object getAuthMechData(String key) {
		return session.getAuthMechData().get(key);
	}
	
	public void addRole(RoleEntity role) {
		session.getRoles().add(role);
	}

	public void addRoles(Set<RoleEntity> rolesToAdd) {
		session.getRoles().addAll(rolesToAdd);
	}

	public boolean isUserInRole(RoleEntity role) {
		return session.getRoles().contains(role);
	}

}
