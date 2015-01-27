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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import edu.kit.scc.webreg.dao.AuditEntryDao;
import edu.kit.scc.webreg.entity.AuditEntryEntity;
import edu.kit.scc.webreg.entity.AuditServiceRegisterEntity;
import edu.kit.scc.webreg.entity.RegistryEntity;

@Named
@ApplicationScoped
public class JpaAuditEntryDao extends JpaBaseDao<AuditEntryEntity, Long> implements AuditEntryDao {

    @Override
    @SuppressWarnings({"unchecked"})
	public List<AuditEntryEntity> findAllOlderThan(Date date, int limit) {
		return em.createQuery("select e from AuditEntryEntity e where endTime < :date order by endTime asc")
				.setParameter("date", date).setMaxResults(limit).getResultList();
	}

    @Override
    @SuppressWarnings({"unchecked"})
	public List<AuditServiceRegisterEntity> findAllServiceRegister(RegistryEntity registry) {
		return em.createQuery("select e from AuditServiceRegisterEntity e where e.registry = :registry")
				.setParameter("registry", registry).getResultList();
	}

	@Override
	public Class<AuditEntryEntity> getEntityClass() {
		return AuditEntryEntity.class;
	}
}
