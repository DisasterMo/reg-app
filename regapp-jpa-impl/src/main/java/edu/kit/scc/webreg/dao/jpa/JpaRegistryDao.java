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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import edu.kit.scc.webreg.dao.RegistryDao;
import edu.kit.scc.webreg.entity.ExternalUserEntity;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.RegistryEntity_;
import edu.kit.scc.webreg.entity.RegistryStatus;
import edu.kit.scc.webreg.entity.SamlIdpMetadataEntityStatus;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.ServiceEntity_;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.identity.IdentityEntity;

@Named
@ApplicationScoped
public class JpaRegistryDao extends JpaBaseDao<RegistryEntity> implements RegistryDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findAllByRegValueAndStatus(ServiceEntity service, String key, String value,
			RegistryStatus status) {
		return em
				.createQuery("select r from RegistryEntity r join r.registryValues rv "
						+ "where (key(rv) = :key and rv = :val) "
						+ "and r.service = :service and r.registryStatus = :status")
				.setParameter("key", key).setParameter("val", value).setParameter("service", service)
				.setParameter("status", status).getResultList();
	}

	@Override
	public RegistryEntity findByIdWithAgreements(Long id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> registry = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(builder.equal(registry.get("id"), id)));
		criteria.select(registry);
		criteria.distinct(true);
		registry.fetch("agreedTexts", JoinType.LEFT);

		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<RegistryEntity> findAllByStatus(RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.equal(root.get("registryStatus"), status));
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

	@Override
	public List<RegistryEntity> findByServiceOrderByRecon(ServiceEntity service, int limit) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.equal(root.get(RegistryEntity_.service), service));
		criteria.orderBy(builder.asc(root.get(RegistryEntity_.lastReconcile)));
		criteria.select(root);

		return em.createQuery(criteria).setMaxResults(limit).getResultList();
	}

	@Override
	public List<RegistryEntity> findByServiceAndStatusOrderByRecon(ServiceEntity service, RegistryStatus status,
			int limit) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(builder.equal(root.get("service"), service),
				builder.equal(root.get("registryStatus"), status)));
		criteria.orderBy(builder.asc(root.get(RegistryEntity_.lastReconcile)));
		criteria.select(root);

		return em.createQuery(criteria).setMaxResults(limit).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findByServiceAndStatus(String serviceShortName, RegistryStatus status, Date date,
			int limit) {
		return em
				.createQuery(
						"select r from RegistryEntity r where r.service.shortName = :ssn and r.registryStatus = :status"
								+ " and lastStatusChange < :is order by lastStatusChange asc")
				.setParameter("ssn", serviceShortName).setParameter("status", status).setParameter("is", date)
				.setMaxResults(limit).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findAllExternalBySsn(String serviceShortName) {
		return em
				.createQuery("select r from RegistryEntity r, UserEntity u where r.service.shortName = :ssn and "
						+ "r.user = u and TYPE(u) = :class")
				.setParameter("ssn", serviceShortName).setParameter("class", ExternalUserEntity.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findByServiceAndStatusAndIDPGood(String serviceShortName, RegistryStatus status,
			Date date, int limit) {
		return em.createQuery(
				"select r from RegistryEntity r where r.service.shortName = :ssn and r.registryStatus = :status"
						+ " and lastStatusChange < :is and r.user.idp.aqIdpStatus = :aqStatus order by lastStatusChange asc")
				.setParameter("ssn", serviceShortName).setParameter("status", status).setParameter("is", date)
				.setParameter("aqStatus", SamlIdpMetadataEntityStatus.GOOD).setMaxResults(limit).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findRegistriesForDepro(String serviceShortName) {
		List<RegistryEntity> resultList = em.createQuery(
				"select r from RegistryEntity r where r.service.shortName = :ssn and r.registryStatus = :status and "
						+ "r.agreedTime = (select max(r1.agreedTime) from RegistryEntity r1 where r1.user = r.user and r1.service = r.service) and not exists "
						+ "(select r2 from RegistryEntity r2 where r2.user = r.user and r2.agreedTime > r.agreedTime and r2.service = r.service)")
				.setParameter("ssn", serviceShortName).setParameter("status", RegistryStatus.DELETED).getResultList();

		return resultList;
	}

	@Override
	public RegistryEntity findRegistryForDepro(String serviceShortName, String key, String value) {
		try {
			RegistryEntity registry = em.createQuery("select r from RegistryEntity r join r.registryValues rv "
					+ "where (key(rv) = :key and rv = :val) and r.service.shortName = :ssn and r.registryStatus = :status and "
					+ "r.agreedTime = (select max(r1.agreedTime) from RegistryEntity r1 where r1.user = r.user and r1.service = r.service) and not exists "
					+ "(select r2 from RegistryEntity r2 where r2.user = r.user and r2.agreedTime > r.agreedTime and r2.service = r.service)",
					RegistryEntity.class).setParameter("key", key).setParameter("val", value)
					.setParameter("ssn", serviceShortName).setParameter("status", RegistryStatus.DELETED)
					.getSingleResult();

			return registry;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> findUserListByServiceAndStatus(ServiceEntity service, RegistryStatus status) {
		return em
				.createQuery(
						"select r.user from RegistryEntity r where r.service = :service and r.registryStatus = :status")
				.setParameter("service", service).setParameter("status", status).getResultList();
	}

	@Override
	public List<RegistryEntity> findByServiceAndStatus(ServiceEntity service, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(builder.equal(root.get("service"), service),
				builder.equal(root.get("registryStatus"), status)));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByServiceAndAttribute(String key, String value, ServiceEntity service) {
		return em
				.createQuery(
						"select r from RegistryEntity r join r.registryValues rv "
								+ "where (key(rv) = :key and rv = :val) " + "and r.service = :service ",
						RegistryEntity.class)
				.setParameter("key", key).setParameter("val", value).setParameter("service", service).getResultList();
	}

	@Override
	public List<RegistryEntity> findByServiceAndNotStatus(ServiceEntity service, RegistryStatus... status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);

		List<Predicate> predList = new ArrayList<Predicate>();
		predList.add(builder.equal(root.get("service"), service));
		for (RegistryStatus s : status)
			predList.add(builder.notEqual(root.get("registryStatus"), s));

		criteria.where(builder.and(predList.toArray(new Predicate[] {})));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByServiceAndUser(ServiceEntity service, UserEntity user) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(builder.equal(root.get("service"), service), builder.equal(root.get("user"), user)));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public RegistryEntity findByServiceAndUserAndStatus(ServiceEntity service, UserEntity user, RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(builder.equal(root.get("service"), service), builder.equal(root.get("user"), user),
				builder.equal(root.get("registryStatus"), status)));
		criteria.select(root);

		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public RegistryEntity findByServiceAndIdentityAndStatus(ServiceEntity service, IdentityEntity identity,
			RegistryStatus status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.and(builder.equal(root.get("service"), service),
				builder.equal(root.get("identity"), identity), builder.equal(root.get("registryStatus"), status)));
		criteria.select(root);

		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<RegistryEntity> findByServiceAndIdentityAndNotStatus(ServiceEntity service, IdentityEntity identity,
			RegistryStatus... status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);

		List<Predicate> predList = new ArrayList<Predicate>();
		predList.add(builder.equal(root.get("service"), service));
		predList.add(builder.equal(root.get("identity"), identity));
		for (RegistryStatus s : status)
			predList.add(builder.notEqual(root.get("registryStatus"), s));

		criteria.where(builder.and(predList.toArray(new Predicate[] {})));
		criteria.select(root);

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByIdentityAndStatus(IdentityEntity identity, RegistryStatus... status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);

		List<Predicate> predList = new ArrayList<Predicate>();

		for (RegistryStatus s : status)
			predList.add(builder.equal(root.get("registryStatus"), s));

		criteria.where(builder.and(builder.equal(root.get(RegistryEntity_.identity), identity),
				builder.or(predList.toArray(new Predicate[] {}))));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByUserAndStatus(UserEntity user, RegistryStatus... status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);

		List<Predicate> predList = new ArrayList<Predicate>();

		for (RegistryStatus s : status)
			predList.add(builder.equal(root.get("registryStatus"), s));

		criteria.where(builder.and(builder.equal(root.get(RegistryEntity_.user), user),
				builder.or(predList.toArray(new Predicate[] {}))));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByIdentityAndNotStatusAndNotHidden(IdentityEntity identity,
			RegistryStatus... status) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		Join<RegistryEntity, ServiceEntity> serviceJoin = root.join(RegistryEntity_.service);

		List<Predicate> predList = new ArrayList<Predicate>();

		predList.add(builder.or(builder.isNull(serviceJoin.get(ServiceEntity_.hidden)),
				builder.equal(serviceJoin.get(ServiceEntity_.hidden), false)));

		predList.add(builder.equal(root.get(RegistryEntity_.identity), identity));
		for (RegistryStatus s : status)
			predList.add(builder.notEqual(root.get("registryStatus"), s));

		criteria.where(builder.and(predList.toArray(new Predicate[] {})));
		criteria.select(root);
		criteria.distinct(true);

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByUser(UserEntity user) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.equal(root.get("user"), user));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<RegistryEntity> findByIdentity(IdentityEntity identity) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RegistryEntity> criteria = builder.createQuery(RegistryEntity.class);
		Root<RegistryEntity> root = criteria.from(RegistryEntity.class);
		criteria.where(builder.equal(root.get("identity"), identity));
		criteria.select(root);
		criteria.distinct(true);
		criteria.orderBy(builder.asc(root.get("id")));

		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistryEntity> findMissingIdentity() {
		return em.createQuery("select r from RegistryEntity r where r.identity is null").getResultList();
	}

	@Override
	public Class<RegistryEntity> getEntityClass() {
		return RegistryEntity.class;
	}
}
