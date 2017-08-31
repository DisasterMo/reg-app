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
package edu.kit.scc.regapp.dao.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import edu.kit.scc.regapp.dao.JobClassDao;
import edu.kit.scc.regapp.entity.JobClassEntity;

@Named
@ApplicationScoped
public class JpaJobClassDao extends JpaBaseDao<JobClassEntity, Long> implements JobClassDao {

    @Override
	public Class<JobClassEntity> getEntityClass() {
		return JobClassEntity.class;
	}
}
