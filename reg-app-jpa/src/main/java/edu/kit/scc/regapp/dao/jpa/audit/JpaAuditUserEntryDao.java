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
package edu.kit.scc.regapp.dao.jpa.audit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import edu.kit.scc.regapp.dao.audit.AuditUserEntryDao;
import edu.kit.scc.regapp.dao.jpa.JpaBaseDao;
import edu.kit.scc.regapp.entity.audit.AuditUserEntity;

@Named
@ApplicationScoped
public class JpaAuditUserEntryDao extends JpaBaseDao<AuditUserEntity, Long> implements AuditUserEntryDao {

	@Override
	public Class<AuditUserEntity> getEntityClass() {
		return AuditUserEntity.class;
	}
}
