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
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;
import edu.kit.scc.regapp.entity.audit.AuditIdpCommunicationEntity;

public class IdpCommunicationAuditor extends AbstractAuditor<AuditIdpCommunicationEntity> {

	private static final long serialVersionUID = 1L;

	public IdpCommunicationAuditor(AuditEntryDao auditEntryDao,
			AuditDetailDao auditDetailDao, ApplicationConfig appConfig) {

		super(auditEntryDao, auditDetailDao, appConfig);
	}

	public void setIdp(SamlIdpMetadataEntity idp) {
		audit.setIdp(idp);
	}

	public void setSpConfig(SamlSpConfigurationEntity spConfig) {
		audit.setSpConfig(spConfig);
	}

	@Override
	protected AuditIdpCommunicationEntity newInstance() {
		return new AuditIdpCommunicationEntity();
	}
}
