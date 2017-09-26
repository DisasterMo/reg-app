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

import edu.kit.scc.regapp.entity.JobClassEntity;
import edu.kit.scc.regapp.service.BaseService;

public interface JobClassService extends BaseService<JobClassEntity, Long> {

	void replaceAllInClasses(String regex, String replacement);

}
