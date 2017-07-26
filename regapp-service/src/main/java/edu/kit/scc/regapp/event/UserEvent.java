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

import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.audit.AuditEntryEntity;

public class UserEvent extends AbstractEvent<UserEntity> {


	private static final long serialVersionUID = 1L;

	public UserEvent(UserEntity entity) {
		super(entity);
	}

	public UserEvent(UserEntity entity, AuditEntryEntity audit) {
		super(entity, audit);
	}
}
