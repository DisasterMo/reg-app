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
import javax.persistence.Query;

import edu.kit.scc.webreg.dao.AgreementTextDao;
import edu.kit.scc.webreg.dao.ops.PaginateBy;
import edu.kit.scc.webreg.entity.AgreementTextEntity;

@Named
@ApplicationScoped
public class JpaAgreementTextDao extends JpaBaseDao<AgreementTextEntity> implements AgreementTextDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<AgreementTextEntity> findByRegistryId(PaginateBy paginateBy, Long registryId) {
		Query query = em.createNativeQuery(
				"SELECT DISTINCT a.* FROM agreement_text a JOIN registry_agreementtext r ON a.id = r.agreementtext_id WHERE r.registry_id = :registryId",
				getEntityClass());
		if (paginateBy != null) {
			applyPaging(query, paginateBy);
		}
		return query.getResultList();
	}

	@Override
	public Class<AgreementTextEntity> getEntityClass() {
		return AgreementTextEntity.class;
	}
}
