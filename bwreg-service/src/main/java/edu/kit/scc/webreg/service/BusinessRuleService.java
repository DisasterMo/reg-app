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
package edu.kit.scc.webreg.service;

import java.util.Date;
import java.util.List;

import edu.kit.scc.webreg.entity.BusinessRuleEntity;

public interface BusinessRuleService extends BaseService<BusinessRuleEntity> {

	List<BusinessRuleEntity> findAllNewer(Date date);

	List<BusinessRuleEntity> findAllKnowledgeBaseNotNull();

	void replaceRegexSingle(Long ruleId, String regex, String replace);

	void replaceRegex(String regex, String replace);
}
