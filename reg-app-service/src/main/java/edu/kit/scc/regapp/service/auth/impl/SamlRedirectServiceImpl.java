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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPRedirectDeflateEncoder;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.slf4j.Logger;

import edu.kit.scc.regapp.dao.SamlIdpMetadataDao;
import edu.kit.scc.regapp.dao.auth.AuthMechDao;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;
import edu.kit.scc.regapp.entity.auth.SamlAuthMechEntity;
import edu.kit.scc.regapp.exc.LoginFailedException;
import edu.kit.scc.regapp.service.auth.SamlHelper;
import edu.kit.scc.regapp.service.auth.SamlMetadataHelper;
import edu.kit.scc.regapp.service.auth.SamlRedirectService;
import edu.kit.scc.regapp.service.auth.SamlSsoHelper;
import edu.kit.scc.regapp.session.SessionManager;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

@Stateless
public class SamlRedirectServiceImpl implements SamlRedirectService {

	public static final String AUTH_MECH_ID_KEY = "authMechId";
	public static final String IDP_ID_KEY = "idpId";
	
	@Inject
	private Logger logger;

	@Inject
	private AuthMechDao authMechDao;
	
	@Inject
	private SamlIdpMetadataDao idpDao;
	
	@Inject
	private SamlHelper samlHelper;

	@Inject
	private SamlMetadataHelper metadataHelper;
	
	@Inject 
	private SamlSsoHelper ssoHelper;
	
	@Inject
	private SessionManager sessionManager;
	
	public void redirectClient(Long authMechId, Long idpId, HttpServletResponse response) 
					throws MessageEncodingException, ComponentInitializationException, LoginFailedException {

		AuthMechEntity authMechEntity = authMechDao.findById(authMechId);
		SamlAuthMechEntity samlAuthMechEntity;
		if (authMechEntity instanceof SamlAuthMechEntity) {
			samlAuthMechEntity = (SamlAuthMechEntity) authMechEntity;
		}
		else {
			throw new LoginFailedException("authentication mechanism is not saml type");
		}
		
		SamlIdpMetadataEntity idpEntity = idpDao.findById(idpId);
		SamlSpConfigurationEntity spEntity = samlAuthMechEntity.getSpConfig();
		
		EntityDescriptor entityDesc = samlHelper.unmarshal(
				idpEntity.getEntityDescriptor(), EntityDescriptor.class);
		if (entityDesc == null) {
			throw new LoginFailedException("No entity descriptor for idp");
		}
		
		SingleSignOnService sso = metadataHelper.getSSO(entityDesc, SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		if (sso == null) {
			throw new LoginFailedException("No sso endpoint for idp");
		}

		AuthnRequest authnRequest = ssoHelper.buildAuthnRequest(
				spEntity.getEntityId(), spEntity.getAcs(), SAMLConstants.SAML2_POST_BINDING_URI);

		sessionManager.addAuthMechData(SamlRedirectServiceImpl.AUTH_MECH_ID_KEY, authMechEntity.getId());
		sessionManager.addAuthMechData(SamlRedirectServiceImpl.IDP_ID_KEY, idpEntity.getId());
		logger.debug("Sending client to idp {} endpoint {}", idpEntity.getEntityId(), sso.getLocation());

		MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
		messageContext.setMessage(authnRequest);
		SAMLPeerEntityContext entityContext = new SAMLPeerEntityContext();
		entityContext.setEntityId(idpEntity.getEntityId());
		SAMLEndpointContext endpointContext = new SAMLEndpointContext();
		endpointContext.setEndpoint(sso);
		entityContext.addSubcontext(endpointContext);
		messageContext.addSubcontext(entityContext);
		
		HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
		encoder.setHttpServletResponse(response);
		encoder.setMessageContext(messageContext);
		encoder.initialize();
		encoder.prepareContext();
		encoder.encode();
		
	}

}
