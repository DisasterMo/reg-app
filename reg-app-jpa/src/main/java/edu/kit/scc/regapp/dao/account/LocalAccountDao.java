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
package edu.kit.scc.regapp.dao.account;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.entity.account.LocalAccountEntity;

public interface LocalAccountDao extends BaseDao<LocalAccountEntity, Long> {

	LocalAccountEntity findByLocalIdWithRoles(String localId);
	LocalAccountEntity findByLocalId(String localId);

}