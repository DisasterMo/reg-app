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
package edu.kit.scc.regapp.dao.jpa.as;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import edu.kit.scc.regapp.dao.as.AttributeSourceDao;
import edu.kit.scc.regapp.dao.jpa.JpaBaseDao;
import edu.kit.scc.regapp.entity.as.AttributeSourceEntity;

@Named
@ApplicationScoped
public class JpaAttributeSourceDao extends JpaBaseDao<AttributeSourceEntity, Long> implements AttributeSourceDao {

	@Override
	public Class<AttributeSourceEntity> getEntityClass() {
		return AttributeSourceEntity.class;
	}
}
