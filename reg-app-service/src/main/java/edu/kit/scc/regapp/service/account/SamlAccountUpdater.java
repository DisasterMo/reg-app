package edu.kit.scc.regapp.service.account;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.exc.UserUpdateException;

public class SamlAccountUpdater extends AccountUpdater<SamlAccountEntity> {

	public static final String PERSISTENT_ID_KEY = "__persistentId__";
	public static final String EPPN_URN = "urn:oid:1.3.6.1.4.1.5923.1.1.1.6";
	
	@Inject
	private Logger logger;

	@Override
	protected Boolean updateAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		
		boolean changed = false;
		
		logger.info("Updating SamlAccount {}", account.getGlobalId());
		
		/*
		 * Use EPPN as globalId. If missing, use the persistentId
		 */
		String str;
		if (attributeMap.containsKey(EPPN_URN)) {
			str = mapHelper.getSingleStringFirst(attributeMap, EPPN_URN);
		}
		else {
			str = mapHelper.getSingleStringFirst(attributeMap, PERSISTENT_ID_KEY);
		}

		if (! account.getGlobalId().equals(str)) {
			account.setGlobalId(str);
			changed = true;
		}
		
		return changed;
	}

}
