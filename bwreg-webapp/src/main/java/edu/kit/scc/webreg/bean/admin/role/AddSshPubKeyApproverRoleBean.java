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
package edu.kit.scc.webreg.bean.admin.role;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.SshPubKeyApproverRoleEntity;
import edu.kit.scc.webreg.service.SshPubKeyApproverRoleService;
import edu.kit.scc.webreg.util.ViewIds;

@Named("addSshPubKeyApproverRoleBean")
@RequestScoped
public class AddSshPubKeyApproverRoleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SshPubKeyApproverRoleService roleService;
	
	private SshPubKeyApproverRoleEntity entity;

	@PostConstruct
	public void init() {
		entity = roleService.createNew();
	}
	 
	public String save() {
		roleService.save(entity);
		return ViewIds.LIST_ROLES;
	}

	public SshPubKeyApproverRoleEntity getEntity() {
		return entity;
	}

	public void setEntity(SshPubKeyApproverRoleEntity entity) {
		this.entity = entity;
	}
	
}
