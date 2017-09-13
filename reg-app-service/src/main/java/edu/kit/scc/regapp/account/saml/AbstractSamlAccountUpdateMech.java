package edu.kit.scc.regapp.account.saml;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;

public abstract class AbstractSamlAccountUpdateMech implements SamlAccountUpdateMech {

	protected final Logger logger = LoggerFactory.getLogger(AbstractSamlAccountUpdateMech.class);

	protected AttributeMapHelper mapHelper;
		
	protected abstract Boolean update(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException;
	
	@Override
	public Boolean updateAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		return update(account, attributeMap, auditor);
	}

	@Override
	public void setMapHelper(AttributeMapHelper mapHelper) {
		this.mapHelper = mapHelper;
	}

}