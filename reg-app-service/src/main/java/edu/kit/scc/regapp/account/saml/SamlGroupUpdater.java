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
package edu.kit.scc.regapp.account.saml;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.bpm.KnowledgeSessionWorker;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.HomeOrgGroupDao;
import edu.kit.scc.regapp.dao.SerialDao;
import edu.kit.scc.regapp.dao.ServiceGroupFlagDao;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;

@ApplicationScoped
public class SamlGroupUpdater implements Serializable {

	public static final String SECONDARY_GROUP_ID = "http://bwidm.de/bwidmMemberOf";
	public static final String PRIMARY_GROUP_ID = "http://bwidm.de/bwidmCC";
	public static final String HOME_ID = "http://bwidm.de/bwidmOrgId";

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private HomeOrgGroupDao dao;
	
	@Inject
	private GroupDao groupDao;

	@Inject
	private ServiceGroupFlagDao groupFlagDao;
	
	@Inject
	private AttributeMapHelper mapHelper;

	@Inject 
	private SerialDao serialDao;

	@Inject 
	private EventSubmitter eventSubmitter;

	@Inject
	private KnowledgeSessionWorker knowledgeSession;

	@Inject
	private ApplicationConfig appConfig;
	
	public Set<GroupEntity> updateGroupsForAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap, AccountUpdateAuditor auditor)
			throws UserUpdateException {
		String className;
		
		/*
		 * determine which SamlAccountUpdateMech to use
		 */
		String groupMechRuleString = appConfig.getConfigValue(SamlAccountUpdater.SAML_GROUP_MECH_RULE);
		
		if (groupMechRuleString == null || groupMechRuleString.equals("")) {
			logger.debug("No specific group update mech specified, using standard");
			className = SamlAccountUpdater.SAML_GROUP_STANDARD_MECH;
		}
		else {
			className = knowledgeSession.resolveSamlAccountUpdateMech(groupMechRuleString, account, attributeMap);
			logger.debug("Resolved group update mech {}", className);
		}
		
		try {
			SamlGroupUpdateMech groupUpdateMech = (SamlGroupUpdateMech) Class.forName(className).newInstance();
			groupUpdateMech.setEnv(mapHelper, dao, groupDao, groupFlagDao, serialDao, eventSubmitter);
			return groupUpdateMech.updateGroups(account, attributeMap, auditor);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute account update", e);
			throw new UserUpdateException(e);
		}

	}
}
