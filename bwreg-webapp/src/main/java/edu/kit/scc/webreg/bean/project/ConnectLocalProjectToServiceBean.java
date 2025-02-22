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
package edu.kit.scc.webreg.bean.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.project.LocalProjectEntity;
import edu.kit.scc.webreg.entity.project.ProjectAdminType;
import edu.kit.scc.webreg.entity.project.ProjectIdentityAdminEntity;
import edu.kit.scc.webreg.entity.project.ProjectServiceEntity;
import edu.kit.scc.webreg.entity.project.ProjectServiceStatusType;
import edu.kit.scc.webreg.entity.project.ProjectServiceType;
import edu.kit.scc.webreg.exc.NotAuthorizedException;
import edu.kit.scc.webreg.service.ServiceService;
import edu.kit.scc.webreg.service.project.LocalProjectService;
import edu.kit.scc.webreg.service.project.ProjectService;
import edu.kit.scc.webreg.session.SessionManager;

@Named
@ViewScoped
public class ConnectLocalProjectToServiceBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;
	
	@Inject
	private SessionManager session;
	
	@Inject
	private LocalProjectService service;

	@Inject
	private ServiceService serviceService;

	@Inject
	private ProjectService projectService;

	private LocalProjectEntity entity;

	private List<ServiceEntity> serviceList;
	private List<ServiceEntity> selectedServices;
	private List<ProjectServiceEntity> projectServiceList;
	
	private Long id;

	private List<ProjectIdentityAdminEntity> adminList;
	private ProjectIdentityAdminEntity adminIdentity;

	public void preRenderView(ComponentSystemEvent ev) {
		selectedServices = new ArrayList<ServiceEntity>();
		
		for (ProjectIdentityAdminEntity a : getAdminList()) {
			if (a.getIdentity().getId().equals(session.getIdentityId())) {
				adminIdentity = a;
				break;
			}
		}
		
		if (adminIdentity == null) {
			throw new NotAuthorizedException("Nicht autorisiert");
		}		
		else {
			if (! (ProjectAdminType.ADMIN.equals(adminIdentity.getType()) || ProjectAdminType.OWNER.equals(adminIdentity.getType()))) {
				throw new NotAuthorizedException("Nicht autorisiert");
			}
		}
	}

	public List<ProjectIdentityAdminEntity> getAdminList() {
		if (adminList == null) {
			adminList = projectService.findAdminsForProject(getEntity());
		}
		return adminList;
	}

	public String save() {
		for (ServiceEntity s : selectedServices) {
			projectService.addOrChangeService(entity, s, ProjectServiceType.PASSIVE_GROUP, 
					ProjectServiceStatusType.APPROVAL_PENDING, "idty-" + session.getIdentityId());
		}
		return "show-local-project.xhtml?faces-redirect=true&id=" + getEntity().getId();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalProjectEntity getEntity() {
		if (entity == null) {
			entity = service.findByIdWithAttrs(id, "projectServices");
		}

		return entity;
	}

	public void setEntity(LocalProjectEntity entity) {
		this.entity = entity;
	}

	public List<ServiceEntity> getServiceList() {
		if (serviceList == null) {
			serviceList = serviceService.findAllByAttr("projectCapable", Boolean.TRUE);
			for (ProjectServiceEntity pse : getProjectServiceList()) {
				serviceList.remove(pse.getService());
			}
		}
		return serviceList;
	}

	public List<ServiceEntity> getSelectedServices() {
		return selectedServices;
	}

	public void setSelectedServices(List<ServiceEntity> selectedServices) {
		this.selectedServices = selectedServices;
	}

	public List<ProjectServiceEntity> getProjectServiceList() {
		if (projectServiceList == null) {
			projectServiceList = projectService.findServicesForProject(entity);
		}
		return projectServiceList;
	}

	public void setProjectServiceList(List<ProjectServiceEntity> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}
}
