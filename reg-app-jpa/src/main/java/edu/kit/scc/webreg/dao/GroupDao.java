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
package edu.kit.scc.webreg.dao;

import java.util.List;
import java.util.Set;

import edu.kit.scc.webreg.dao.as.AttributeSourceGroupDao;
import edu.kit.scc.webreg.entity.GroupEntity;
import edu.kit.scc.webreg.entity.LocalGroupEntity;
import edu.kit.scc.webreg.entity.ServiceBasedGroupEntity;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.ServiceGroupStatus;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.UserGroupEntity;
import edu.kit.scc.webreg.entity.account.AccountEntity;
import edu.kit.scc.webreg.entity.account.AccountGroupEntity;

public interface GroupDao extends BaseDao<GroupEntity, Long> {

	GroupEntity findByGidNumber(Integer gid);

	GroupEntity findByName(String name);

	GroupEntity findByNameAndPrefix(String name, String prefix);

	List<GroupEntity> findByUser(UserEntity user);

	GroupEntity findWithUsers(Long id);

	void addUserToGroup(UserEntity user, GroupEntity group, Boolean quick);

	void removeUserFromGroup(UserEntity user, GroupEntity group, Boolean quick);

	UserGroupEntity createNewUserGroup();

	boolean isUserInGroup(UserEntity user, GroupEntity group);

	UserGroupEntity findUserGroupEntity(UserEntity user, GroupEntity group);

	LocalGroupEntity findLocalGroupByName(String name);

	LocalGroupDao getLocalGroupDao();

	HomeOrgGroupDao getHomeOrgGroupDao();

	AttributeSourceGroupDao getAttributeSourceGroupDao();

	Long getNextGID();

	ServiceBasedGroupEntity persistWithServiceFlags(
			ServiceBasedGroupEntity entity);

	ServiceBasedGroupEntity persistWithServiceFlags(
			ServiceBasedGroupEntity entity, Set<ServiceEntity> services);

	void setServiceFlags(ServiceBasedGroupEntity entity,
			ServiceGroupStatus status);

	void addAccountToGroup(AccountEntity account, GroupEntity group, Boolean quick);

	AccountGroupEntity findAccountGroupEntity(AccountEntity account, GroupEntity group);

	AccountGroupEntity createNewAccountGroup();

	void removeAccountFromGroup(AccountEntity account, GroupEntity group, Boolean quick);

}
