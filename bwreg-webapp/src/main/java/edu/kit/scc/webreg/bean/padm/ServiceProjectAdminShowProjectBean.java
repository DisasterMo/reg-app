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
package edu.kit.scc.webreg.bean.padm;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.project.LocalProjectEntity;
import edu.kit.scc.webreg.entity.project.ProjectIdentityAdminEntity;
import edu.kit.scc.webreg.entity.project.ProjectServiceEntity;
import edu.kit.scc.webreg.exc.NotAuthorizedException;
import edu.kit.scc.webreg.sec.AuthorizationBean;
import edu.kit.scc.webreg.service.ServiceService;
import edu.kit.scc.webreg.service.project.LocalProjectService;
import edu.kit.scc.webreg.service.project.ProjectService;
import edu.kit.scc.webreg.session.SessionManager;
import edu.kit.scc.webreg.util.FacesMessageGenerator;

@Named
@ViewScoped
public class ServiceProjectAdminShowProjectBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject 
	private SessionManager session;
	
	@Inject
	private LocalProjectService service;

	@Inject
	private ProjectService projectService;

	@Inject
	private ServiceService serviceService;
	
    @Inject
    private AuthorizationBean authBean;

    @Inject
    private FacesMessageGenerator messageGenerator;
    
	private LocalProjectEntity entity;
	private ServiceEntity serviceEntity;
	private ProjectServiceEntity projectServiceEntity;
	private List<ProjectServiceEntity> serviceList;

	private ProjectIdentityAdminEntity adminIdentity;

	private Long projectId;
	private Long serviceId;
	
	public void preRenderView(ComponentSystemEvent ev) {
		if (entity == null) {
			entity = service.findByIdWithAttrs(projectId, "projectServices");
		}
		
		if (serviceEntity == null) {
			serviceEntity = serviceService.findById(serviceId);
		}
		
		if (entity == null || serviceEntity == null)
			throw new NotAuthorizedException("Nicht autorisiert");
		
		if (! authBean.isUserServiceProjectAdmin(serviceEntity)) 
			throw new NotAuthorizedException("No service project admin");
		
		projectServiceEntity = entity.getProjectServices().stream().filter(
				ps -> ps.getService().equals(serviceEntity))
				.findFirst().orElseThrow(() -> new NotAuthorizedException("Nicht autorisiert"));
	}
	
	public void approve() {
		service.approve(projectServiceEntity, "idty-" + session.getIdentityId());
		messageGenerator.addResolvedInfoMessage("project.local_project.approver_admin_approved", "project.local_project.approver_admin_approved_detail", true);
		entity = service.findByIdWithAttrs(projectId, "projectServices");
		serviceList = null;
	}
	
	public void deny() {
		service.deny(projectServiceEntity, null, "idty-" + session.getIdentityId());
		messageGenerator.addResolvedInfoMessage("project.local_project.approver_admin_declined", "project.local_project.approver_admin_declined_detail", true);
		entity = service.findByIdWithAttrs(projectId, "projectServices");
		serviceList = null;
	}
	
	public LocalProjectEntity getEntity() {
		return entity;
	}

	public void setEntity(LocalProjectEntity entity) {
		this.entity = entity;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public List<ProjectServiceEntity> getServiceList() {
		if (serviceList == null) {
			serviceList = projectService.findServicesForProject(entity);
		}
		return serviceList;
	}

	public ProjectIdentityAdminEntity getAdminIdentity() {
		return adminIdentity;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public ServiceEntity getServiceEntity() {
		return serviceEntity;
	}

	public void setServiceEntity(ServiceEntity serviceEntity) {
		this.serviceEntity = serviceEntity;
	}

	public ProjectServiceEntity getProjectServiceEntity() {
		return projectServiceEntity;
	}

	public void setProjectServiceEntity(ProjectServiceEntity projectServiceEntity) {
		this.projectServiceEntity = projectServiceEntity;
	}

}
