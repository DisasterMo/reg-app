package edu.kit.scc.regapp.account.saml;

import java.util.List;
import java.util.Map;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;

public interface SamlAccountUpdateMech {

	Boolean updateAccount(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException;

	void setMapHelper(AttributeMapHelper mapHelper);

}