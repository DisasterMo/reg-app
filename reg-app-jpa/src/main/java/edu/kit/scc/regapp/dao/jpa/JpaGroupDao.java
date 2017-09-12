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
package edu.kit.scc.regapp.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.HomeOrgGroupDao;
import edu.kit.scc.regapp.dao.LocalGroupDao;
import edu.kit.scc.regapp.dao.SerialDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dao.ServiceGroupFlagDao;
import edu.kit.scc.regapp.dao.as.AttributeSourceGroupDao;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.LocalGroupEntity;
import edu.kit.scc.regapp.entity.ServiceBasedGroupEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.ServiceGroupFlagEntity;
import edu.kit.scc.regapp.entity.ServiceGroupStatus;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.UserGroupEntity;
import edu.kit.scc.regapp.entity.account.AccountEntity;
import edu.kit.scc.regapp.entity.account.AccountGroupEntity;

@Named
@ApplicationScoped
public class JpaGroupDao extends JpaBaseDao<GroupEntity, Long> implements GroupDao {

	@Inject
	private LocalGroupDao localGroupDao;
	
	@Inject
	private HomeOrgGroupDao homeOrgGroupDao;
	
	@Inject
	private AttributeSourceGroupDao attributeSourceGroupDao;
	
	@Inject
	private SerialDao serialDao;
	
	@Inject
	private ServiceGroupFlagDao groupFlagDao;
	
	@Inject
	private ServiceDao serviceDao;
	
	@Override
	public ServiceBasedGroupEntity persistWithServiceFlags(ServiceBasedGroupEntity entity) {
		entity = (ServiceBasedGroupEntity) persist(entity);
		List<ServiceEntity> serviceList = serviceDao.findByGroupCapability(true);
		for (ServiceEntity service : serviceList) {
			List<ServiceGroupFlagEntity> flagList = groupFlagDao.findByGroupAndService(entity, service);
			if (flagList.size() == 0) {
				ServiceGroupFlagEntity groupFlag = groupFlagDao.createNew();
				groupFlag.setGroup(entity);
				groupFlag.setService(service);
				groupFlag.setStatus(ServiceGroupStatus.DIRTY);
				groupFlagDao.persist(groupFlag);
			}
		}
		return entity;
	}
	
	@Override
	public ServiceBasedGroupEntity persistWithServiceFlags(ServiceBasedGroupEntity entity, Set<ServiceEntity> services) {
		entity = (ServiceBasedGroupEntity) persist(entity);
		for (ServiceEntity service : services) {
			List<ServiceGroupFlagEntity> flagList = groupFlagDao.findByGroupAndService(entity, service);
			if (flagList.size() == 0) {
				ServiceGroupFlagEntity groupFlag = groupFlagDao.createNew();
				groupFlag.setGroup(entity);
				groupFlag.setService(service);
				groupFlag.setStatus(ServiceGroupStatus.DIRTY);
				groupFlagDao.persist(groupFlag);
			}
		}
		return entity;
	}
	
	@Override
	public void setServiceFlags(ServiceBasedGroupEntity entity, ServiceGroupStatus status) {
		List<ServiceGroupFlagEntity> flagList = groupFlagDao.findByGroup(entity);
		for (ServiceGroupFlagEntity groupFlag : flagList) {
			groupFlag.setStatus(status);
		}
	}
	
	@Override
	public void addUserToGroup(UserEntity user, GroupEntity group, Boolean quick) {
		UserGroupEntity userGroup = createNewUserGroup();
		userGroup.setUser(user);
		userGroup.setGroup(group);
		
		if ((quick != null) && (! quick)) {
			if (user.getGroups() != null)
				user.getGroups().add(userGroup);
			
			if (group.getUsers() != null)
				group.getUsers().add(userGroup);
		}
		
		em.persist(userGroup);
	}
	
	@Override
	public void removeUserFromGroup(UserEntity user, GroupEntity group, Boolean quick) {
		UserGroupEntity userGroup = findUserGroupEntity(user, group);
		if (userGroup != null) {
			if ((quick != null) && (! quick)) {
				if (user.getGroups() != null)
					user.getGroups().remove(userGroup);
				
				if (group.getUsers() != null)
					group.getUsers().remove(userGroup);
			}
			em.remove(userGroup);
		}
	}

	@Override
	public void addAccountToGroup(AccountEntity account, GroupEntity group, Boolean quick) {
		AccountGroupEntity accountGroup = createNewAccountGroup();
		accountGroup.setAccount(account);
		accountGroup.setGroup(group);
		
		if ((quick != null) && (! quick)) {
			if (account.getGroups() != null)
				account.getGroups().add(accountGroup);
			
			if (group.getAccounts() != null)
				group.getAccounts().add(accountGroup);
		}
		
		em.persist(accountGroup);
	}
	
	@Override
	public void removeAccountFromGroup(AccountEntity account, GroupEntity group, Boolean quick) {
		AccountGroupEntity accountGroup = findAccountGroupEntity(account, group);
		if (accountGroup != null) {
			if ((quick != null) && (! quick)) {
				if (account.getGroups() != null)
					account.getGroups().remove(accountGroup);
				
				if (group.getAccounts() != null)
					group.getAccounts().remove(accountGroup);
			}
			em.remove(accountGroup);
		}
	}
		
	@Override
	public boolean isUserInGroup(UserEntity user, GroupEntity group) {
		if (findUserGroupEntity(user, group) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public UserGroupEntity findUserGroupEntity(UserEntity user, GroupEntity group) {
		try {
			return (UserGroupEntity) em.createQuery("select r from UserGroupEntity r where r.user = :user "
					+ "and r.group = :group")
				.setParameter("user", user).setParameter("group", group).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public boolean isAccountInGroup(AccountEntity account, GroupEntity group) {
		if (findAccountGroupEntity(account, group) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public AccountGroupEntity findAccountGroupEntity(AccountEntity account, GroupEntity group) {
		try {
			return (AccountGroupEntity) em.createQuery("select r from AccountGroupEntity r where r.account = :account "
					+ "and r.group = :group")
				.setParameter("account", account).setParameter("group", group).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public UserGroupEntity createNewUserGroup() {
		return new UserGroupEntity();
	}
	
	@Override
	public AccountGroupEntity createNewAccountGroup() {
		return new AccountGroupEntity();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupEntity> findByUser(UserEntity user) {
		return em.createQuery("select r.group from UserGroupEntity r where r.user = :user")
			.setParameter("user", user).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupEntity> findByAccount(AccountEntity account) {
		return em.createQuery("select r.group from AccountGroupEntity r where r.account = :account")
			.setParameter("account", account).getResultList();
	}

	@Override
	public GroupEntity findWithUsers(Long id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<GroupEntity> criteria = builder.createQuery(GroupEntity.class);
		Root<GroupEntity> root = criteria.from(GroupEntity.class);
		criteria.where(
				builder.equal(root.get("id"), id));
		criteria.select(root);
		root.fetch("users", JoinType.LEFT);
		
		return em.createQuery(criteria).getSingleResult();
	}

	@Override
	public GroupEntity findByGidNumber(Integer gid) {
		try {
			return (GroupEntity) em.createQuery("select e from GroupEntity e where e.gidNumber = :gidNumber")
				.setParameter("gidNumber", gid).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public GroupEntity findByName(String name) {
		try {
			return (GroupEntity) em.createQuery("select e from GroupEntity e where e.name = :name")
				.setParameter("name", name).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public LocalGroupEntity findLocalGroupByName(String name) {
		try {
			return (LocalGroupEntity) em.createQuery("select e from LocalGroupEntity e where e.name = :name")
				.setParameter("name", name).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public GroupEntity findByNameAndPrefix(String name, String prefix) {
		try {
			return (GroupEntity) em.createQuery("select e from GroupEntity e where e.name = :name and e.prefix = :prefix")
				.setParameter("name", name).setParameter("prefix", prefix).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Long getNextGID() {
		return serialDao.next("gid-number-serial");
	}
	
	@Override
	public Class<GroupEntity> getEntityClass() {
		return GroupEntity.class;
	}

	@Override
	public LocalGroupDao getLocalGroupDao() {
		return localGroupDao;
	}

	@Override
	public HomeOrgGroupDao getHomeOrgGroupDao() {
		return homeOrgGroupDao;
	}
	
	@Override
	public AttributeSourceGroupDao getAttributeSourceGroupDao() {
		return attributeSourceGroupDao;
	}
}
