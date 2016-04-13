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
package edu.kit.scc.webreg.audit;

import edu.kit.scc.webreg.bootstrap.ApplicationConfig;
import edu.kit.scc.webreg.dao.audit.AuditDetailDao;
import edu.kit.scc.webreg.dao.audit.AuditEntryDao;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.audit.AuditEntryEntity;
import edu.kit.scc.webreg.entity.audit.AuditRegistryEntity;

public class RegistryAuditor extends AbstractAuditor {

	private AuditRegistryEntity audit;
	
	public RegistryAuditor(AuditEntryDao auditEntryDao,
			AuditDetailDao auditDetailDao, ApplicationConfig appConfig) {

		super(auditEntryDao, auditDetailDao, appConfig);
	}

	@Override
	public AuditEntryEntity getAudit() {
		if (audit == null)
			audit = new AuditRegistryEntity();
		
		return audit;
	}

	public void setRegistry(RegistryEntity entity) {
		audit.setRegistry(entity);
	}
}
