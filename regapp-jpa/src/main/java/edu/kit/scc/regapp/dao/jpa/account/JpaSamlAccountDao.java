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

import edu.kit.scc.regapp.dao.account.SamlAccountDao;
import edu.kit.scc.regapp.dao.jpa.JpaBaseDao;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity_;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity_;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity_;

@Named
@ApplicationScoped
public class JpaSamlAccountDao extends JpaBaseDao<SamlAccountEntity, Long> implements SamlAccountDao {

	@Override
	public SamlAccountEntity findByPersistentWithRoles(String spId, String idpId, String persistentId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SamlAccountEntity> criteria = builder.createQuery(SamlAccountEntity.class);
		Root<SamlAccountEntity> root = criteria.from(SamlAccountEntity.class);
		criteria.where(builder.and(
				builder.equal(root.get(SamlAccountEntity_.sp).get(SamlSpConfigurationEntity_.entityId), spId),
				builder.equal(root.get(SamlAccountEntity_.idp).get(SamlIdpMetadataEntity_.entityId), idpId),
				builder.equal(root.get(SamlAccountEntity_.persistentId), persistentId)
				));
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
	public SamlAccountEntity findByEppn(String eppn) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SamlAccountEntity> criteria = builder.createQuery(SamlAccountEntity.class);
		Root<SamlAccountEntity> root = criteria.from(SamlAccountEntity.class);
		criteria.where(builder.equal(root.get(SamlAccountEntity_.globalId), eppn));
		criteria.select(root);
		
		try {
			return em.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}	

	@Override
	public Class<SamlAccountEntity> getEntityClass() {
		return SamlAccountEntity.class;
	}
}
