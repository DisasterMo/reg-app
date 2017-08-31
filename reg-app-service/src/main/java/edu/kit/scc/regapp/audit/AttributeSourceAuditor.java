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
package edu.kit.scc.regapp.audit;

import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.dao.audit.AuditDetailDao;
import edu.kit.scc.regapp.dao.audit.AuditEntryDao;
import edu.kit.scc.regapp.entity.as.ASUserAttrEntity;
import edu.kit.scc.regapp.entity.audit.AuditAttributeSourceEntity;

public class AttributeSourceAuditor extends AbstractAuditor<AuditAttributeSourceEntity> {

	private static final long serialVersionUID = 1L;

	public AttributeSourceAuditor(AuditEntryDao auditEntryDao,
			AuditDetailDao auditDetailDao, ApplicationConfig appConfig) {

		super(auditEntryDao, auditDetailDao, appConfig);
	}

	public void setAsUserAttr(ASUserAttrEntity entity) {
		audit.setAsUserAttr(entity);
	}

	@Override
	protected AuditAttributeSourceEntity newInstance() {
		return new AuditAttributeSourceEntity();
	}
}
