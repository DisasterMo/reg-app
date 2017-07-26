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
import edu.kit.scc.regapp.entity.as.ASUserAttrEntity;
import edu.kit.scc.regapp.entity.as.ASUserAttrValueDateEntity;
import edu.kit.scc.regapp.entity.as.ASUserAttrValueEntity;
import edu.kit.scc.regapp.entity.as.ASUserAttrValueLongEntity;
import edu.kit.scc.regapp.entity.as.ASUserAttrValueStringEntity;

public interface ASUserAttrValueDao extends BaseDao<ASUserAttrValueEntity, Long> {

	ASUserAttrValueDateEntity createNewDate();

	ASUserAttrValueLongEntity createNewLong();

	ASUserAttrValueStringEntity createNewString();

	ASUserAttrValueEntity findValueByKey(ASUserAttrEntity asUserAttr, String key);

	List<ASUserAttrValueEntity> findValues(ASUserAttrEntity asUserAttr);

}
