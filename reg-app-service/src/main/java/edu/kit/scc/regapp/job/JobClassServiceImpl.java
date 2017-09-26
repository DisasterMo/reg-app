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
package edu.kit.scc.regapp.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.JobClassDao;
import edu.kit.scc.regapp.entity.JobClassEntity;
import edu.kit.scc.regapp.service.impl.BaseServiceImpl;

@Stateless
public class JobClassServiceImpl extends BaseServiceImpl<JobClassEntity, Long> implements JobClassService {

	private static final long serialVersionUID = 1L;

	@Inject
	private JobClassDao dao;
	
	
    @Override
	public void replaceAllInClasses(String regex, String replacement) {
		List<JobClassEntity> jobList = dao.findAll();
		
		for (JobClassEntity job : jobList) {
			job.setJobClassName(
					job.getJobClassName().replaceAll(regex, replacement));
		}
    }

	@Override
	protected BaseDao<JobClassEntity, Long> getDao() {
		return dao;
	}
}
