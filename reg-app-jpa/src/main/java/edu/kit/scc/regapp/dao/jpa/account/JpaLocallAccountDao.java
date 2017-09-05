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
package edu.kit.scc.regapp.dao.jpa.account;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import edu.kit.scc.regapp.dao.account.LocalAccountDao;
import edu.kit.scc.regapp.dao.jpa.JpaBaseDao;
import edu.kit.scc.regapp.entity.account.LocalAccountEntity;
import edu.kit.scc.regapp.entity.account.LocalAccountEntity_;

@Named
@ApplicationScoped
public class JpaLocallAccountDao extends JpaBaseDao<LocalAccountEntity, Long> implements LocalAccountDao {

	@Override
	public LocalAccountEntity findByLocalIdWithRoles(String localId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<LocalAccountEntity> criteria = builder.createQuery(LocalAccountEntity.class);
		Root<LocalAccountEntity> root = criteria.from(LocalAccountEntity.class);
		criteria.where(builder.equal(root.get(LocalAccountEntity_.localId), localId));
		criteria.select(root);
		criteria.distinct(true);
		root.fetch("accountStore", JoinType.LEFT);
		
		try {
			return em.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}			
	}	

	@Override
	public LocalAccountEntity findByLocalId(String localId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<LocalAccountEntity> criteria = builder.createQuery(LocalAccountEntity.class);
		Root<LocalAccountEntity> root = criteria.from(LocalAccountEntity.class);
		criteria.where(builder.equal(root.get(LocalAccountEntity_.localId), localId));
		criteria.select(root);
		
		try {
			return em.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}	

	@Override
	public Class<LocalAccountEntity> getEntityClass() {
		return LocalAccountEntity.class;
	}
}
