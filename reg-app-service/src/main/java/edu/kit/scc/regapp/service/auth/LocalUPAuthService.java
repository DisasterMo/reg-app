package edu.kit.scc.regapp.service.auth;

import edu.kit.scc.regapp.entity.account.LocalAccountEntity;

public interface LocalUPAuthService {

	LocalAccountEntity checkUsernamePassword(String username, String password);
}
