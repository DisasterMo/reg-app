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
package edu.kit.scc.regapp.dao;

import java.util.List;

import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.ServiceEventEntity;

public interface ServiceEventDao extends BaseDao<ServiceEventEntity, Long> {

	List<ServiceEventEntity> findAllByService(ServiceEntity service);

}
