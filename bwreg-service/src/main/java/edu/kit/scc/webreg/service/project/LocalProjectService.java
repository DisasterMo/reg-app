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
package edu.kit.scc.webreg.service.project;

import java.util.List;

import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.project.LocalProjectEntity;
import edu.kit.scc.webreg.entity.project.ProjectServiceEntity;
import edu.kit.scc.webreg.service.BaseService;

public interface LocalProjectService extends BaseService<LocalProjectEntity> {

	List<LocalProjectEntity> findByService(ServiceEntity service);

	LocalProjectEntity save(LocalProjectEntity project, Long identityId);

	void approve(ProjectServiceEntity pse, String executor);

	void deny(ProjectServiceEntity pse, String denyMessage, String executor);

}
