package edu.kit.scc.regapp.service.auth.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.account.LocalAccountDao;
import edu.kit.scc.regapp.entity.account.LocalAccountEntity;
import edu.kit.scc.regapp.service.auth.LocalUPAuthService;

@Stateless
public class LocalUPAuthServiceImpl implements LocalUPAuthService {

	@Inject
	LocalAccountDao localAccountDao;
	
	@Override
	public LocalAccountEntity checkUsernamePassword(String username, String password) {
		LocalAccountEntity entity = localAccountDao.findByLocalId(username);
		
		if ((entity != null) && (password != null) 
				&& (password.equals(entity.getPassword()))) {
			return entity;
		}
		else {
			return null;
		}
	}

}
