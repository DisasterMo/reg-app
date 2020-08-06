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
package edu.kit.scc.webreg.bean.sadm.project;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;

import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.project.ProjectEntity;
import edu.kit.scc.webreg.exc.NotAuthorizedException;
import edu.kit.scc.webreg.sec.AuthorizationBean;
import edu.kit.scc.webreg.service.ServiceService;
import edu.kit.scc.webreg.service.project.ProjectService;
import edu.kit.scc.webreg.util.ViewIds;

@ManagedBean
@ViewScoped
public class ProjectAdminAddProjectBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProjectService service;
	
	@Inject
	private ServiceService serviceService;

	@Inject
    private AuthorizationBean authBean;
	
	private ProjectEntity entity;

	private ServiceEntity serviceEntity;
	
	private Long serviceId;
	
	public void preRenderView(ComponentSystemEvent ev) {
		if (serviceEntity == null) 
			serviceEntity = serviceService.findById(serviceId);

		if (! authBean.isUserServiceGroupAdmin(serviceEntity))
			throw new NotAuthorizedException("Nicht autorisiert");
		
		if (entity == null)
			entity = service.createNew();
		
	}
	
	public String save() {
		entity = service.save(entity, serviceEntity);
		
		return ViewIds.PROJECT_ADMIN_INDEX + "?serviceId=" + serviceEntity.getId() + "&faces-redirect=true";
	}

	public String cancel() {
		return ViewIds.PROJECT_ADMIN_INDEX + "?serviceId=" + serviceEntity.getId() + "&faces-redirect=true";
	}

	public ProjectEntity getEntity() {
		return entity;
	}

	public void setEntity(ProjectEntity entity) {
		this.entity = entity;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
}
