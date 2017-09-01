/*******************************************************************************
 * Copyright (c) 2017 Michael Simon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Michael Simon - initial
 ******************************************************************************/
package edu.kit.scc.regapp.dao.auth;

import java.util.List;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;

public interface AuthMechDao extends BaseDao<AuthMechEntity, Long> {

	List<AuthMechEntity> findByHostname(String hostname);

}