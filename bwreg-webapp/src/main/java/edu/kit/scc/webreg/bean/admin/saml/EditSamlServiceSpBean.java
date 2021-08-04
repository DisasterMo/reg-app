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
package edu.kit.scc.webreg.bean.admin.saml;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.SamlIdpConfigurationEntity;
import edu.kit.scc.webreg.entity.ScriptEntity;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.ServiceSamlSpEntity;
import edu.kit.scc.webreg.service.SamlIdpConfigurationService;
import edu.kit.scc.webreg.service.ScriptService;
import edu.kit.scc.webreg.service.ServiceSamlSpService;
import edu.kit.scc.webreg.service.ServiceService;

@Named
@ViewScoped
public class EditSamlServiceSpBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ServiceSamlSpService service;
	
	@Inject
	private SamlIdpConfigurationService idpConfigurationService;
	
	@Inject
	private ServiceService serviceService;
	
	@Inject
	private ScriptService scriptService;
	
	private ServiceSamlSpEntity entity;
	private List<SamlIdpConfigurationEntity> idpList;
	private List<ServiceEntity> serviceList;
	private List<ScriptEntity> scriptList;
	
	private Long id;

	public void preRenderView(ComponentSystemEvent ev) {
	}
	
	public String save() {
		entity = service.save(entity);
		return "list-idp-configs.xhtml";
	}

	public String cancel() {
		return "list-idp-configs.xhtml";
	}

	public ServiceSamlSpEntity getEntity() {
		if (entity == null) {
			entity = service.findById(id);
		}
		return entity;
	}

	public void setEntity(ServiceSamlSpEntity entity) {
		this.entity = entity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<SamlIdpConfigurationEntity> getIdpList() {
		if (idpList == null) {
			idpList = idpConfigurationService.findAll();
		}
		return idpList;
	}

	public void setIdpList(List<SamlIdpConfigurationEntity> idpList) {
		this.idpList = idpList;
	}

	public List<ServiceEntity> getServiceList() {
		if (serviceList == null) {
			serviceList = serviceService.findAll();
		}
		return serviceList;
	}

	public void setServiceList(List<ServiceEntity> serviceList) {
		this.serviceList = serviceList;
	}

	public List<ScriptEntity> getScriptList() {
		if (scriptList == null) {
			scriptList = scriptService.findAll();
		}
		return scriptList;
	}

	public void setScriptList(List<ScriptEntity> scriptList) {
		this.scriptList = scriptList;
	}

}
