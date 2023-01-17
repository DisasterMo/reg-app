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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Root;

import edu.kit.scc.webreg.dao.ExternalUserDao;
import edu.kit.scc.webreg.entity.ExternalUserAdminRoleEntity;
import edu.kit.scc.webreg.entity.ExternalUserEntity;
import edu.kit.scc.webreg.entity.ExternalUserEntity_;

@Named
@ApplicationScoped
public class JpaExternalUserDao extends JpaBaseDao<ExternalUserEntity> implements ExternalUserDao {

	@Override
	public ExternalUserEntity findByExternalId(String externalId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ExternalUserEntity> criteria = builder.createQuery(ExternalUserEntity.class);
		Root<ExternalUserEntity> user = criteria.from(ExternalUserEntity.class);
		criteria.where(builder.equal(user.get(ExternalUserEntity_.externalId), externalId));
		criteria.select(user);

		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<ExternalUserEntity> findByAttribute(String key, String value, ExternalUserAdminRoleEntity adminRole) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ExternalUserEntity> criteria = builder.createQuery(ExternalUserEntity.class);
		Root<ExternalUserEntity> root = criteria.from(ExternalUserEntity.class);
		criteria.select(root);
		MapJoin<ExternalUserEntity, String, String> mapJoin = root.joinMap("attributeStore");
		criteria.where(builder.and(builder.equal(root.get(ExternalUserEntity_.admin), adminRole),
				builder.equal(mapJoin.key(), key), builder.equal(mapJoin.value(), value)));

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<ExternalUserEntity> findByGeneric(String key, String value, ExternalUserAdminRoleEntity adminRole) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ExternalUserEntity> criteria = builder.createQuery(ExternalUserEntity.class);
		Root<ExternalUserEntity> root = criteria.from(ExternalUserEntity.class);
		criteria.select(root);
		MapJoin<ExternalUserEntity, String, String> mapJoin = root.joinMap("genericStore");
		criteria.where(builder.and(builder.equal(root.get(ExternalUserEntity_.admin), adminRole),
				builder.equal(mapJoin.key(), key), builder.equal(mapJoin.value(), value)));

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<ExternalUserEntity> findAll(ExternalUserAdminRoleEntity adminRole) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ExternalUserEntity> criteria = builder.createQuery(ExternalUserEntity.class);
		Root<ExternalUserEntity> root = criteria.from(ExternalUserEntity.class);
		criteria.select(root);
		criteria.where(builder.and(builder.equal(root.get(ExternalUserEntity_.admin), adminRole)));

		return em.createQuery(criteria).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalUserEntity> findByAdmin(ExternalUserAdminRoleEntity adminRole) {
		return em.createQuery("select e from ExternalUserEntity e where e.admin = :admin")
				.setParameter("admin", adminRole).getResultList();
	}

	@Override
	public Class<ExternalUserEntity> getEntityClass() {
		return ExternalUserEntity.class;
	}
}
