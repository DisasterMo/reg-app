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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import edu.kit.scc.regapp.entity.account.AccountGroupEntity;

@Entity(name = "GroupEntity")
@Table(name = "group_store")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class GroupEntity extends AbstractBaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "gid_number", unique = true, nullable = false)
	private Integer gidNumber;
	
	@Column(name = "group_name", length = 512, nullable = false)
	@Pattern(regexp = "^[a-z]{1}[a-z0-9-_]{0,511}$")	
	private String name;

	@Enumerated(EnumType.STRING)
	private GroupStatus groupStatus;
	
	@OneToMany(targetEntity = UserGroupEntity.class, mappedBy="group")
	private Set<UserGroupEntity> users;		

	@OneToMany(targetEntity = AccountGroupEntity.class, mappedBy="group")
	private Set<AccountGroupEntity> accounts;		
	
	@OneToMany(targetEntity = RoleGroupEntity.class, mappedBy="group")
	private Set<RoleGroupEntity> roles;		
	
	@ManyToMany(targetEntity=RoleEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "group_role",
			joinColumns = @JoinColumn(name="role_id"),
			inverseJoinColumns = @JoinColumn(name="group_id")
	)
	private Set<RoleEntity> adminRoles;
    
	@ManyToMany(targetEntity = GroupEntity.class, mappedBy = "children")
	private Set<GroupEntity> parents;
	
	@ManyToMany(targetEntity=GroupEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "group_children",
			joinColumns = @JoinColumn(name="parent_group_id"),
			inverseJoinColumns = @JoinColumn(name="child_group_id")
	)
	private Set<GroupEntity> children;
	
	public Integer getGidNumber() {
		return gidNumber;
	}

	public void setGidNumber(Integer gidNumber) {
		this.gidNumber = gidNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<RoleEntity> getAdminRoles() {
		return adminRoles;
	}

	public void setAdminRoles(Set<RoleEntity> adminRoles) {
		this.adminRoles = adminRoles;
	}

	public Set<GroupEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<GroupEntity> children) {
		this.children = children;
	}

	public Set<GroupEntity> getParents() {
		return parents;
	}

	public void setParents(Set<GroupEntity> parents) {
		this.parents = parents;
	}

	public GroupStatus getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(GroupStatus groupStatus) {
		this.groupStatus = groupStatus;
	}

	public Set<UserGroupEntity> getUsers() {
		return users;
	}

	public void setUsers(Set<UserGroupEntity> users) {
		this.users = users;
	}

	public Set<AccountGroupEntity> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<AccountGroupEntity> accounts) {
		this.accounts = accounts;
	}

}
