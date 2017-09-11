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
package edu.kit.scc.regapp.service.auth.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;

import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.dao.SamlIdpMetadataDao;
import edu.kit.scc.regapp.dao.account.SamlAccountDao;
import edu.kit.scc.regapp.dao.auth.AuthMechDao;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntityStatus;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;
import edu.kit.scc.regapp.entity.auth.SamlAuthMechEntity;
import edu.kit.scc.regapp.exc.SamlAuthenticationException;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.account.SamlAccountUpdater;
import edu.kit.scc.regapp.service.auth.SamlAssertionProcessor;
import edu.kit.scc.regapp.service.auth.SamlDecoder;
import edu.kit.scc.regapp.service.auth.SamlHelper;
import edu.kit.scc.regapp.service.auth.SamlPostService;
import edu.kit.scc.regapp.session.SessionManager;

@ApplicationScoped
public class SamlPostServiceImpl implements SamlPostService {

	@Inject
	private Logger logger;

	@Inject
	private SessionManager sessionManager;

	@Inject
	private SamlAccountDao samlAccountDao;
	
	@Inject
	private SamlDecoder samlDecoder;
	
	@Inject
	private SamlAssertionProcessor samlAssertionProcessor;
	
	@Inject
	private SamlHelper samlHelper;
		
	@Inject 
	private SamlIdpMetadataDao idpDao;

	@Inject
	private AuthMechDao authMechDao;
	
	@Inject
	private SamlAccountUpdater accountUpdater;
	
	@Inject
	private ApplicationConfig appConfig;
	
	@Override
	public void consumePost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SamlAuthenticationException {

		if (sessionManager.getAuthMechData(SamlRedirectServiceImpl.AUTH_MECH_ID_KEY) == null || 
				sessionManager.getAuthMechData(SamlRedirectServiceImpl.IDP_ID_KEY) == null) {
			logger.info("Client session from {} not established. Sending client back to welcome page",
					request.getRemoteAddr());
			response.sendRedirect("/");
			return;
		}
		
		logger.debug("attempAuthentication, Consuming SAML Assertion");
		Long authMechId = (Long) sessionManager.getAuthMechData(SamlRedirectServiceImpl.AUTH_MECH_ID_KEY);
		Long idpId = (Long) sessionManager.getAuthMechData(SamlRedirectServiceImpl.IDP_ID_KEY);
		
			AuthMechEntity authMechEntity = authMechDao.findById(authMechId);
			SamlAuthMechEntity samlAuthMechEntity;
			if (authMechEntity instanceof SamlAuthMechEntity) {
				samlAuthMechEntity = (SamlAuthMechEntity) authMechEntity;
			}
			else {
				throw new SamlAuthenticationException("authentication mechanism is not saml type");
			}

			SamlIdpMetadataEntity idpEntity = idpDao.findById(idpId);
			EntityDescriptor idpEntityDescriptor = samlHelper.unmarshal(
					idpEntity.getEntityDescriptor(), EntityDescriptor.class);
		
			Assertion assertion;
			String persistentId;
			try {
				Response samlResponse = samlDecoder.decodePostMessage(request);

				assertion = samlAssertionProcessor.processSamlResponse(samlResponse, idpEntity, 
						idpEntityDescriptor, samlAuthMechEntity.getSpConfig());
				
				persistentId = samlAssertionProcessor.extractPersistentId(assertion,  samlAuthMechEntity.getSpConfig());
			} catch (Exception e1) {
				/*
				 * Catch Exception here for a probably faulty IDP. Register Exception and rethrow.
				 */
				if (! SamlIdpMetadataEntityStatus.FAULTY.equals(idpEntity.getIdIdpStatus())) {
                    idpEntity.setIdIdpStatus(SamlIdpMetadataEntityStatus.FAULTY);
                    idpEntity.setLastIdStatusChange(new Date());
				}
				throw new SamlAuthenticationException("Saml auth exception", e1);
			}
			
			if (! SamlIdpMetadataEntityStatus.GOOD.equals(idpEntity.getIdIdpStatus())) {
                idpEntity.setIdIdpStatus(SamlIdpMetadataEntityStatus.GOOD);
                idpEntity.setLastIdStatusChange(new Date());
			}

			Map<String, List<Object>> attributeMap = samlAssertionProcessor.extractAttributes(assertion);
			
			logger.debug("Done for now, attributeMap is {} entries", attributeMap.size());
			
			SamlAccountEntity samlAccount = samlAccountDao.findByPersistentWithRoles(
					samlAuthMechEntity.getSpConfig().getEntityId(),	idpEntity.getEntityId(), persistentId);
			
			if (samlAccount == null) {
				logger.info("New account detected with persistendId {} from {}", persistentId, idpEntity.getEntityId());
			}
			else {
		    	logger.debug("Updating account {} ({}) from {}", samlAccount.getGlobalId(), persistentId, idpEntity.getEntityId());
				try {
					accountUpdater.update(samlAccount, attributeMap, "user-login");
				} catch (UserUpdateException e) {
					logger.warn("User Update failed", e);
					throw new SamlAuthenticationException("user update failed", e);
				}
			}
/*
			UserEntity user = userDao.findByPersistentWithRoles(samlAuthMechEntity.getSpConfig().getEntityId(), 
						idpEntity.getEntityId(), persistentId);

			String userLoginRule = appConfig.getConfigValue("user_login_rule");
			
			if (userLoginRule != null && (! "".equals(userLoginRule))) {
				logger.debug("Checking User login rule {}", userLoginRule);
		    	long start = System.currentTimeMillis();

				knowledgeSessionService.checkRule(userLoginRule, user, attributeMap, assertion, 
						idpEntity, idpEntityDescriptor, spConfig);
				
		    	long end = System.currentTimeMillis();
		    	logger.debug("Rule processing took {} ms", end - start);
			}
			
			if (user == null) {
				logger.info("New User detected, sending to register Page");

				// Store SAML Data temporarily in Session
				logger.debug("Storing relevant SAML data in session");
				session.setPersistentId(persistentId);
				session.setAttributeMap(attributeMap);				
				
				response.sendRedirect("/register/register.xhtml");
				return;
			}
			
	    	logger.debug("Updating user {}", persistentId);
			
			try {
				user = userService.updateUserFromAttribute(user, attributeMap, "web-sso");
			} catch (UserUpdateException e) {
				logger.warn("Could not update user {}: {}", e.getMessage(), user.getEppn());
				throw new SamlAuthenticationException(e.getMessage());
			}
			
			session.setUserId(user.getId());
			session.setTheme(user.getTheme());
			session.setLocale(user.getLocale());
			
			if (session.getOriginalRequestPath() != null)
				response.sendRedirect(session.getOriginalRequestPath());
			else
				response.sendRedirect("/index.xhtml");

			return;
*/
	}
}
