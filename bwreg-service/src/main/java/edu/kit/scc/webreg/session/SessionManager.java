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
package edu.kit.scc.webreg.session;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.GroupEntity;
import edu.kit.scc.webreg.entity.RoleEntity;
import edu.kit.scc.webreg.entity.ServiceEntity;

@Named("sessionManager")
@SessionScoped
public class SessionManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long authnRequestId;
	private Long authnRequestIdpConfigId;

	// identityId of the actual user
	private Long identityId;
	
	private Long idpId;
	
	private Long spId;

	private Long oidcRelyingPartyId;

	private Map<String, List<Object>> attributeMap;
	
	private String persistentId;
	private String subjectId;
	
	private String originalRequestPath;
	private String originalIdpEntityId;
	
	private Set<RoleEntity> roles;
	private Long roleSetCreated;

	private List<ServiceEntity> serviceApproverList;
	private List<ServiceEntity> serviceSshPubKeyApproverList;
	private List<ServiceEntity> serviceAdminList;
	private List<ServiceEntity> serviceHotlineList;
	private List<ServiceEntity> serviceGroupAdminList;

	private List<ServiceEntity> unregisteredServiceList;
	private Long unregisteredServiceCreated;

	private Set<GroupEntity> groups;
	private Set<String> groupNames;
	private Long groupSetCreated;
	
	private String theme;
	
	private String locale;
	
	private Instant twoFaElevation;
	private Instant loginTime;
	
	@PostConstruct
	public void init() {
		serviceApproverList = new ArrayList<ServiceEntity>();
		serviceSshPubKeyApproverList = new ArrayList<ServiceEntity>();
		serviceAdminList = new ArrayList<ServiceEntity>();
		serviceHotlineList = new ArrayList<ServiceEntity>();
		serviceGroupAdminList = new ArrayList<ServiceEntity>();
		groups = new HashSet<GroupEntity>();
		groupNames = new HashSet<String>();
		roles = new HashSet<RoleEntity>();
	}
	
	public void clearRoleList() {
		serviceApproverList.clear();
		serviceSshPubKeyApproverList.clear();
		serviceAdminList.clear();
		serviceHotlineList.clear();
		serviceGroupAdminList.clear();
	}
	
	public void clearGroups() {
		groups.clear();
	}
	
	public boolean isLoggedIn() {
		return (identityId != null ? true : false);		
	}

	public void logout() {
		
	}

	public void addRole(RoleEntity role) {
		roles.add(role);
	}
	
	public void addRoles(Set<RoleEntity> rolesToAdd) {
		roles.addAll(rolesToAdd);
	}

	public boolean isUserInRole(RoleEntity role) {
        return roles.contains(role);
    }

	public Long getIdpId() {
		return idpId;
	}

	public void setIdpId(Long idpId) {
		this.idpId = idpId;
	}

	public Long getSpId() {
		return spId;
	}

	public void setSpId(Long spId) {
		this.spId = spId;
	}

	public Map<String, List<Object>> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, List<Object>> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public String getPersistentId() {
		return persistentId;
	}

	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public String getTheme() {
		if (theme == null)
			theme = "aristo";
		return theme;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getOriginalIdpEntityId() {
		return originalIdpEntityId;
	}

	public void setOriginalIdpEntityId(String originalIdpEntityId) {
		this.originalIdpEntityId = originalIdpEntityId;
	}

	public String getOriginalRequestPath() {
		return originalRequestPath;
	}

	public void setOriginalRequestPath(String originalRequestPath) {
		this.originalRequestPath = originalRequestPath;
	}

	public Long getRoleSetCreated() {
		return roleSetCreated;
	}

	public void setRoleSetCreated(Long roleSetCreated) {
		this.roleSetCreated = roleSetCreated;
	}

	public Set<GroupEntity> getGroups() {
		return groups;
	}

	public Long getGroupSetCreated() {
		return groupSetCreated;
	}

	public void setGroupSetCreated(Long groupSetCreated) {
		this.groupSetCreated = groupSetCreated;
	}

	public Set<String> getGroupNames() {
		return groupNames;
	}

	public List<ServiceEntity> getServiceApproverList() {
		return serviceApproverList;
	}

	public List<ServiceEntity> getServiceAdminList() {
		return serviceAdminList;
	}

	public List<ServiceEntity> getServiceHotlineList() {
		return serviceHotlineList;
	}

	public List<ServiceEntity> getServiceGroupAdminList() {
		return serviceGroupAdminList;
	}

	public Set<RoleEntity> getRoles() {
		return roles;
	}

	public List<ServiceEntity> getUnregisteredServiceList() {
		return unregisteredServiceList;
	}

	public void setUnregisteredServiceList(
			List<ServiceEntity> unregisteredServiceList) {
		this.unregisteredServiceList = unregisteredServiceList;
	}

	public Long getUnregisteredServiceCreated() {
		return unregisteredServiceCreated;
	}

	public void setUnregisteredServiceCreated(Long unregisteredServiceCreated) {
		this.unregisteredServiceCreated = unregisteredServiceCreated;
	}

	public Long getAuthnRequestId() {
		return authnRequestId;
	}

	public void setAuthnRequestId(Long authnRequestId) {
		this.authnRequestId = authnRequestId;
	}

	public Long getAuthnRequestIdpConfigId() {
		return authnRequestIdpConfigId;
	}

	public void setAuthnRequestIdpConfigId(Long authnRequestIdpConfigId) {
		this.authnRequestIdpConfigId = authnRequestIdpConfigId;
	}

	public List<ServiceEntity> getServiceSshPubKeyApproverList() {
		return serviceSshPubKeyApproverList;
	}

	public Instant getTwoFaElevation() {
		return twoFaElevation;
	}

	public void setTwoFaElevation(Instant twoFaElevation) {
		this.twoFaElevation = twoFaElevation;
	}

	public Instant getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Instant loginTime) {
		this.loginTime = loginTime;
	}

	public Long getIdentityId() {
		return identityId;
	}

	public void setIdentityId(Long identityId) {
		this.identityId = identityId;
	}

	public Long getOidcRelyingPartyId() {
		return oidcRelyingPartyId;
	}

	public void setOidcRelyingPartyId(Long oidcRelyingPartyId) {
		this.oidcRelyingPartyId = oidcRelyingPartyId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
}
