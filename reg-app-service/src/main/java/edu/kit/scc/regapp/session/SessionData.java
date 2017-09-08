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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import edu.kit.scc.regapp.entity.RoleEntity;

@Named("sessionData")
@SessionScoped
public class SessionData implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long userId;

	private Set<RoleEntity> roles;
    private Long roleSetCreated;

    private Map<String, Object> authMechData;
    
	@PostConstruct
	public void init() {
		roles = new HashSet<>();
		authMechData = new HashMap<>();
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Set<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}

	public Long getRoleSetCreated() {
		return roleSetCreated;
	}

	public void setRoleSetCreated(Long roleSetCreated) {
		this.roleSetCreated = roleSetCreated;
	}

	public Map<String, Object> getAuthMechData() {
		return authMechData;
	}

	public void setAuthMechData(Map<String, Object> authMechData) {
		this.authMechData = authMechData;
	}
}
