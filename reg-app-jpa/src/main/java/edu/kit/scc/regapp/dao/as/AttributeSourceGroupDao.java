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
package edu.kit.scc.regapp.dao.as;

import java.util.List;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.as.AttributeSourceEntity;
import edu.kit.scc.regapp.entity.as.AttributeSourceGroupEntity;

public interface AttributeSourceGroupDao extends BaseDao<AttributeSourceGroupEntity, Long> {

	List<AttributeSourceGroupEntity> findByUserAndAS(UserEntity user,
			AttributeSourceEntity attributeSource);

	AttributeSourceGroupEntity findByNameAndAS(String name,
			AttributeSourceEntity attributeSource);


}
