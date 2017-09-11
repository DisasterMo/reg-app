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
package edu.kit.scc.regapp.event;

import edu.kit.scc.regapp.entity.account.AccountEntity;
import edu.kit.scc.regapp.entity.audit.AuditEntryEntity;

public class AccountEvent extends AbstractEvent<AccountEntity> {


	private static final long serialVersionUID = 1L;

	public AccountEvent(AccountEntity entity) {
		super(entity);
	}

	public AccountEvent(AccountEntity entity, AuditEntryEntity audit) {
		super(entity, audit);
	}
}
