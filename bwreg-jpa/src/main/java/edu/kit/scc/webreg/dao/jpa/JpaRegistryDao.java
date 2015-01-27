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
package edu.kit.scc.webreg.dao.jpa;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import edu.kit.scc.webreg.dao.GenericSortOrder;
import edu.kit.scc.webreg.dao.RegistryDao;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.RegistryStatus;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.UserEntity;

@Named
@ApplicationScoped
public class JpaRegistryDao extends JpaBaseDao<RegistryEntity, Long> implements RegistryDao {

	@Override
	public RegistryEntity findByIdWithAgreements(Long id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> registry = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(registry.get("id"), id)
				));
		criteria.select(registry);
		criteria.distinct(true);
		registry.fetch("agreedTexts", JoinType.LEFT);

		try {
			return em.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}	
	
	@Override
	public List<RegistryEntity> findAllByStatus(RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(
				builder.equal(root.get("registryStatus"), status));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}	
	
	@Override
	public List<RegistryEntity> findByService(ServiceEntity service) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.equal(root.get("service"), service));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}	

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findByServiceAndStatus(String serviceShortName, RegistryStatus status, Date date, int limit) {
		return em.createQuery("select r from RegistryEntity r where r.service.shortName = :ssn and r.registryStatus = :status"
				+ " and lastStatusChange < :is order by lastStatusChange asc")
				.setParameter("ssn", serviceShortName).setParameter("status", status).setParameter("is", date)
				.setMaxResults(limit).getResultList();
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> findUserListByServiceAndStatus(ServiceEntity service, RegistryStatus status) {
		return em.createQuery("select r.user from RegistryEntity r where r.service = :service and r.registryStatus = :status")
				.setParameter("service", service).setParameter("status", status).getResultList();
	}	
		
	@Override
	public List<RegistryEntity> findByServiceAndStatus(ServiceEntity service, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get("service"), service),
				builder.equal(root.get("registryStatus"), status)));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}	
	
	@Override
	public List<RegistryEntity> findByServiceAndStatusPaging(ServiceEntity service, RegistryStatus status,
			int first, int pageSize, String sortField,
			GenericSortOrder sortOrder, Map<String, Object> filterMap) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		
		List<Predicate> predicateList = predicatesFromFilterMap(builder, root, filterMap);
		predicateList.add(builder.equal(root.get("service"), service));
		predicateList.add(builder.equal(root.get("registryStatus"), status));
		
		criteria.where(builder.and(predicateList.toArray(new Predicate[predicateList.size()])));

		if (sortField != null && sortOrder != null && sortOrder != GenericSortOrder.NONE) {
			criteria.orderBy(getSortOrder(builder, root, sortField, sortOrder));
		}

		criteria.select(root);

		TypedQuery<RegistryEntity> q = em.createQuery(criteria);
		q.setFirstResult(first).setMaxResults(pageSize);
		
		return q.getResultList();
	}	
		
	@Override
	public Number countServiceAndStatus(ServiceEntity service, RegistryStatus status,
			Map<String, Object> filterMap) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		
		criteria.select(builder.count(root));
		
		List<Predicate> predicateList = predicatesFromFilterMap(builder, root, filterMap);
		predicateList.add(builder.equal(root.get("service"), service));
		predicateList.add(builder.equal(root.get("registryStatus"), status));
		
		criteria.where(builder.and(predicateList.toArray(new Predicate[predicateList.size()])));
		
		TypedQuery<Long> q = em.createQuery(criteria);
		return q.getSingleResult();
	}	
		
	@Override
	public List<RegistryEntity> findByServiceAndUser(ServiceEntity service, UserEntity user) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get("service"), service),
				builder.equal(root.get("user"), user)));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}	
	
	@Override
	public RegistryEntity findByServiceAndUserAndStatus(ServiceEntity service, UserEntity user, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get("service"), service),
				builder.equal(root.get("user"), user),
				builder.equal(root.get("registryStatus"), status)));
		criteria.select(root);

		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}	
	
	@Override
	public List<RegistryEntity> findByServiceAndUserAndNotStatus(ServiceEntity service, UserEntity user, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get("service"), service),
				builder.equal(root.get("user"), user),
				builder.notEqual(root.get("registryStatus"), status)));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}	
	
	@Override
	public List<RegistryEntity> findByUserAndStatus(UserEntity user, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get("user"), user)),
				builder.equal(root.get("registryStatus"), status));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}	
		
	@Override
	public List<RegistryEntity> findByUserAndNotStatus(UserEntity user, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get("user"), user)),
				builder.notEqual(root.get("registryStatus"), status));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}	
		
	@Override
	public List<RegistryEntity> findByUser(UserEntity user) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(
				builder.equal(root.get("user"), user));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}	
		
	@Override
	public Class<RegistryEntity> getEntityClass() {
		return RegistryEntity.class;
	}
}
