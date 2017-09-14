package edu.kit.scc.regapp.account.saml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;

import edu.kit.scc.regapp.account.AccountUpdater;
import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.bpm.KnowledgeSessionWorker;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.exc.UserUpdateException;

public class SamlAccountUpdater extends AccountUpdater<SamlAccountEntity> {

	public static final String SAML_ACCOUNT_MECH_RULE = "saml_account_mech_rule";
	public static final String SAML_GROUP_MECH_RULE = "saml_group_mech_rule";
	
	public static final String SAML_ACCOUNT_STANDARD_MECH = "edu.kit.scc.regapp.account.saml.StandardSamlAccountUpdateMech";
	public static final String SAML_GROUP_STANDARD_MECH = "edu.kit.scc.regapp.account.saml.StandardSamlGroupUpdateMech";
	
	public static final String PERSISTENT_ID_KEY = "__persistentId__";
	public static final String EPPN_URN = "urn:oid:1.3.6.1.4.1.5923.1.1.1.6";
	
	@Inject
	private Logger logger;

	@Inject
	private SamlGroupUpdater samlGroupUpdater;
	
	@Inject
	private KnowledgeSessionWorker knowledgeSession;
	
	@Inject
	private ApplicationConfig appConfig;
	
	@Override
	protected Boolean updateAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		
		boolean changed = false;
		
		String className;
		
		/*
		 * determine which SamlAccountUpdateMech to use
		 */
		String accountMechRuleString = appConfig.getConfigValue(SAML_ACCOUNT_MECH_RULE);
		
		if (accountMechRuleString == null || accountMechRuleString.equals("")) {
			logger.debug("No specific update mech specified, using standard");
			className = SAML_ACCOUNT_STANDARD_MECH;
		}
		else {
			className = knowledgeSession.resolveSamlAccountUpdateMech(accountMechRuleString, account, attributeMap);
			logger.debug("Resolved update mech {}", className);
		}
		
		try {
			SamlAccountUpdateMech accountUpdateMech = (SamlAccountUpdateMech) Class.forName(className).newInstance();
			accountUpdateMech.setMapHelper(mapHelper);
			changed = accountUpdateMech.updateAccount(account, attributeMap, auditor);
			
			samlGroupUpdater.updateGroupsForAccount(account, attributeMap, auditor);
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute account update", e);
			throw new UserUpdateException(e);
		}
		
		return changed;
	}

}
