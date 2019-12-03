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
package edu.kit.scc.webreg.bean.admin.oidc;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.oidc.OidcOpConfigurationEntity;
import edu.kit.scc.webreg.service.oidc.OidcOpConfigurationService;

@Named("addOidcOpConfigurationBean")
@RequestScoped
public class AddOidcOpConfigurationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private OidcOpConfigurationService service;

	private OidcOpConfigurationEntity entity;
	
	@PostConstruct
	public void init() {
		entity = service.createNew();
	}

	public String save() {
		entity = service.save(entity);
		return "show-op-config.xhtml?faces-redirect=true&id=" + entity.getId();
	}
	
	public OidcOpConfigurationEntity getEntity() {
		return entity;
	}

	public void setEntity(OidcOpConfigurationEntity entity) {
		this.entity = entity;
	}
}
