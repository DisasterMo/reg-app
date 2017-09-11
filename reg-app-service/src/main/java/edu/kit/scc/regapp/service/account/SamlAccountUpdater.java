package edu.kit.scc.regapp.service.account;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.exc.UserUpdateException;

public class SamlAccountUpdater extends AccountUpdater<SamlAccountEntity> {

	@Inject
	private Logger logger;

	@Override
	protected Boolean updateAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		
		boolean changed = false;
		
		logger.info("Updating SamlAccount {}", account.getGlobalId());
		
		return changed;
	}

}
