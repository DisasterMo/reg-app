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
package edu.kit.scc.regapp.dao.jpa.auth;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;

import edu.kit.scc.regapp.dao.auth.AuthMechDao;
import edu.kit.scc.regapp.dao.jpa.JpaBaseDao;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;

@Named
@ApplicationScoped
public class JpaAuthMechDao extends JpaBaseDao<AuthMechEntity, Long> implements AuthMechDao {

	@Override
	public List<AuthMechEntity> findByHostname(String hostname) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<AuthMechEntity> criteria = builder.createQuery(AuthMechEntity.class);
		Root<AuthMechEntity> root = criteria.from(AuthMechEntity.class);
		ListJoin<AuthMechEntity, String> elementJoin = root.joinList("hostNameList");
		
		criteria.select(root);
		criteria.where(
				builder.equal(elementJoin.as(String.class), hostname));

		return em.createQuery(criteria).getResultList();
	}

	@Override
	public Class<AuthMechEntity> getEntityClass() {
		return AuthMechEntity.class;
	}
}
