package edu.kit.scc.regapp.account.saml;

import java.util.List;
import java.util.Map;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.exc.UserUpdateException;

public class StandardSamlAccountUpdateMech extends AbstractSamlAccountUpdateMech implements SamlAccountUpdateMech {

	@Override
	protected Boolean update(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		boolean changed = false;
		
		logger.info("Updating SamlAccount using Standard Mech: {}", account.getGlobalId());
		
		/*
		 * Use EPPN as globalId. If missing, use the persistentId
		 */
		String str;
		if (attributeMap.containsKey(SamlAccountUpdater.EPPN_URN)) {
			str = mapHelper.getSingleStringFirst(attributeMap, SamlAccountUpdater.EPPN_URN);
		}
		else {
			str = mapHelper.getSingleStringFirst(attributeMap, SamlAccountUpdater.PERSISTENT_ID_KEY);
		}

		if (! account.getGlobalId().equals(str)) {
			account.setGlobalId(str);
			changed = true;
		}
				
		return changed;
	}

	

}
