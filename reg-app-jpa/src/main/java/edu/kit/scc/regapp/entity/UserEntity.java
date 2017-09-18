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
package edu.kit.scc.regapp.entity;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.kit.scc.regapp.entity.as.ASUserAttrEntity;

@Entity(name = "UserEntity")
@Table(name = "usertable")
public class UserEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "persistent_id", length = 1024)
	private String persistentId;
	
	@Column(name = "persistent_spid", length = 1024)
	private String persistentSpId;
	
	@Column(name = "persistent_idpid", length = 1024)
	private String persistentIdpId;

	@ManyToOne(targetEntity = SamlIdpMetadataEntity.class)
	private SamlIdpMetadataEntity idp;
	
	@Column(name = "eppn", length = 1024)
	private String eppn;
	
	@Column(name = "email", length = 1024)
	private String email;
	
	@ElementCollection
	@JoinTable(name = "user_email_addresses")
	private Set<String> emailAddresses;
	
	@Column(name = "given_name", length = 256)
	private String givenName;
	
	@Column(name = "sur_name", length = 256)
	private String surName;

	@Column(name = "uid_number", unique = true, nullable = false)
	private Integer uidNumber;
	
	@OneToMany(targetEntity=UserRoleEntity.class, mappedBy = "user")
	private Set<UserRoleEntity> roles;
	
	@ElementCollection
	@JoinTable(name = "user_store")
    @MapKeyColumn(name = "key_data", length = 128)
    @Column(name = "value_data", length = 2048)
    private Map<String, String> genericStore; 
	
	@ElementCollection
	@JoinTable(name = "user_attribute_store")
    @MapKeyColumn(name = "key_data", length = 1024)
    @Column(name = "value_data", length = 2048)
    private Map<String, String> attributeStore; 

	@OneToMany(targetEntity = UserGroupEntity.class, mappedBy="user")
	private Set<UserGroupEntity> groups;
		
	@OneToMany(targetEntity = ASUserAttrEntity.class, mappedBy="user")
	private Set<ASUserAttrEntity> userAttrs;
		
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	
	@Column(name = "last_update")
	private Date lastUpdate;
	
	@Column(name = "last_failed_update")
	private Date lastFailedUpdate;
	
	@Column(name = "theme", length = 128)
	private String theme;
	
	@Column(name = "locale", length = 64)
	private String locale;
	
	@ManyToOne(targetEntity = GroupEntity.class)
	private GroupEntity primaryGroup;

	@Column(name = "last_status_change")
	private Date lastStatusChange;
	
	public Set<UserRoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRoleEntity> roles) {
		this.roles = roles;
	}

	@Deprecated
	public String getPersistentId() {
		return persistentId;
	}

	@Deprecated
	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}

	@Deprecated
	public String getEppn() {
		return eppn;
	}

	@Deprecated
	public void setEppn(String eppn) {
		this.eppn = eppn;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public Map<String, String> getGenericStore() {
		return genericStore;
	}

	public void setGenericStore(Map<String, String> genericStore) {
		this.genericStore = genericStore;
	}

	public Map<String, String> getAttributeStore() {
		return attributeStore;
	}

	public void setAttributeStore(Map<String, String> attributeStore) {
		this.attributeStore = attributeStore;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	@Deprecated
	public String getPersistentSpId() {
		return persistentSpId;
	}

	@Deprecated
	public void setPersistentSpId(String persistentSpId) {
		this.persistentSpId = persistentSpId;
	}

	@Deprecated
	public String getPersistentIdpId() {
		return persistentIdpId;
	}

	@Deprecated
	public void setPersistentIdpId(String persistentIdpId) {
		this.persistentIdpId = persistentIdpId;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Deprecated
	public Date getLastUpdate() {
		return lastUpdate;
	}

	@Deprecated
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Integer getUidNumber() {
		return uidNumber;
	}

	public void setUidNumber(Integer uidNumber) {
		this.uidNumber = uidNumber;
	}

	public GroupEntity getPrimaryGroup() {
		return primaryGroup;
	}

	public void setPrimaryGroup(GroupEntity primaryGroup) {
		this.primaryGroup = primaryGroup;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Set<String> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(Set<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	@Deprecated
	public SamlIdpMetadataEntity getIdp() {
		return idp;
	}

	@Deprecated
	public void setIdp(SamlIdpMetadataEntity idp) {
		this.idp = idp;
	}

	public Set<UserGroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(Set<UserGroupEntity> groups) {
		this.groups = groups;
	}

	public Date getLastStatusChange() {
		return lastStatusChange;
	}

	public void setLastStatusChange(Date lastStatusChange) {
		this.lastStatusChange = lastStatusChange;
	}

	@Deprecated
	public Date getLastFailedUpdate() {
		return lastFailedUpdate;
	}

	@Deprecated
	public void setLastFailedUpdate(Date lastFailedUpdate) {
		this.lastFailedUpdate = lastFailedUpdate;
	}

	public Set<ASUserAttrEntity> getUserAttrs() {
		return userAttrs;
	}

	public void setUserAttrs(Set<ASUserAttrEntity> userAttrs) {
		this.userAttrs = userAttrs;
	}
}
