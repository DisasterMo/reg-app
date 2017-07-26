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
package edu.kit.scc.webreg.dao.jpa.account;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.kit.scc.webreg.dao.account.AccountDao;
import edu.kit.scc.webreg.dao.jpa.JpaBaseDao;
import edu.kit.scc.webreg.entity.account.AccountEntity;
import edu.kit.scc.webreg.entity.account.AccountEntity_;

@Named
@ApplicationScoped
public class JpaAccountDao extends JpaBaseDao<AccountEntity, Long> implements AccountDao {

	@Override
	public AccountEntity findByGlobalId(String globalId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<AccountEntity> criteria = builder.createQuery(AccountEntity.class);
		Root<AccountEntity> root = criteria.from(AccountEntity.class);
		criteria.where(
				builder.equal(root.get(AccountEntity_.globalId), globalId));
		criteria.select(root);
		
		try {
			return em.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}			
	}	
	
	@Override
	public Class<AccountEntity> getEntityClass() {
		return AccountEntity.class;
	}
}
