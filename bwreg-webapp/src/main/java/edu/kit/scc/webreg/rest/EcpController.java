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
package edu.kit.scc.webreg.rest;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.soap.client.BasicSOAPMessageContext;
import org.opensaml.ws.soap.client.SOAPClientException;
import org.opensaml.ws.soap.client.http.HttpClientBuilder;
import org.opensaml.ws.soap.client.http.HttpSOAPClient;
import org.opensaml.ws.soap.common.SOAPException;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.security.SecurityException;
import org.slf4j.Logger;

import edu.kit.scc.webreg.dao.TextPropertyDao;
import edu.kit.scc.webreg.drools.KnowledgeSessionService;
import edu.kit.scc.webreg.drools.OverrideAccess;
import edu.kit.scc.webreg.drools.UnauthorizedUser;
import edu.kit.scc.webreg.entity.BusinessRulePackageEntity;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.RegistryStatus;
import edu.kit.scc.webreg.entity.SamlIdpMetadataEntity;
import edu.kit.scc.webreg.entity.SamlSpConfigurationEntity;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.TextPropertyEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.exc.MetadataException;
import edu.kit.scc.webreg.exc.SamlAuthenticationException;
import edu.kit.scc.webreg.exc.UserUpdateException;
import edu.kit.scc.webreg.rest.dto.ECPResponse;
import edu.kit.scc.webreg.rest.dto.RestError;
import edu.kit.scc.webreg.rest.exc.AssertionException;
import edu.kit.scc.webreg.rest.exc.GenericRestInterfaceException;
import edu.kit.scc.webreg.rest.exc.LoginFailedException;
import edu.kit.scc.webreg.rest.exc.NoDelegationConfiguredException;
import edu.kit.scc.webreg.rest.exc.NoEcpSupportException;
import edu.kit.scc.webreg.rest.exc.NoHostnameConfiguredException;
import edu.kit.scc.webreg.rest.exc.NoIdpForScopeException;
import edu.kit.scc.webreg.rest.exc.NoIdpFoundException;
import edu.kit.scc.webreg.rest.exc.NoItemFoundException;
import edu.kit.scc.webreg.rest.exc.NoRegistryFoundException;
import edu.kit.scc.webreg.rest.exc.NoScopedUsernameException;
import edu.kit.scc.webreg.rest.exc.NoServiceFoundException;
import edu.kit.scc.webreg.rest.exc.NoUserFoundException;
import edu.kit.scc.webreg.rest.exc.RestInterfaceException;
import edu.kit.scc.webreg.rest.exc.UnauthorizedException;
import edu.kit.scc.webreg.rest.exc.UserNotRegisteredException;
import edu.kit.scc.webreg.rest.exc.UserUpdateFailedException;
import edu.kit.scc.webreg.service.RegistryService;
import edu.kit.scc.webreg.service.SamlIdpMetadataService;
import edu.kit.scc.webreg.service.SamlSpConfigurationService;
import edu.kit.scc.webreg.service.ServiceService;
import edu.kit.scc.webreg.service.UserService;
import edu.kit.scc.webreg.service.UserUpdateService;
import edu.kit.scc.webreg.service.impl.AttributeMapHelper;
import edu.kit.scc.webreg.service.saml.AttributeQueryHelper;
import edu.kit.scc.webreg.service.saml.MetadataHelper;
import edu.kit.scc.webreg.service.saml.Saml2AssertionService;
import edu.kit.scc.webreg.service.saml.Saml2ResponseValidationService;
import edu.kit.scc.webreg.service.saml.SamlHelper;
import edu.kit.scc.webreg.service.saml.SsoHelper;

@Path("/ecp")
public class EcpController {

	@Inject
	private Logger logger;
	
	@Inject
	private SamlIdpMetadataService idpService;
	
	@Inject
	private SamlSpConfigurationService spService;
	
	@Inject
	private ServiceService serviceService;
	
	@Inject
	private RegistryService registryService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private KnowledgeSessionService knowledgeSessionService;
	
	@Inject
	private SamlHelper samlHelper;
	
	@Inject
	private Saml2AssertionService saml2AssertionService;

	@Inject
	private AttributeQueryHelper attrQueryHelper;
	
	@Inject
	private SsoHelper ssoHelper;
	
	@Inject
	private Saml2ResponseValidationService saml2ResponseValidationService;
	
	@Inject
	private UserUpdateService userUpdateService;
	
	@Inject 
	private MetadataHelper metadataHelper;

	@Inject
	private AttributeMapHelper attrHelper;
	
	@Inject
	private TextPropertyDao textPropertyDao;
	
	@Path("/eppn/{service}/{eppn}")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> ecpLogin(@PathParam("eppn") String eppn,
			@PathParam("service") String serviceShortName,
			@FormParam("password") String password, @Context HttpServletRequest request)
			throws IOException, ServletException, RestInterfaceException {

		UserEntity user = findUser(eppn);
		if (user == null)
			throw new NoUserFoundException("no such user");
		
		ServiceEntity service = findService(serviceShortName);
		if (service == null)
			throw new NoServiceFoundException("no such service");
		
		RegistryEntity registry = findRegistry(user, service);
		if (registry == null)
			throw new NoRegistryFoundException("user not registered for service");
		
		if (password != null && (password.toLowerCase().startsWith("<?xml version") ||
				password.startsWith("<saml2:Assertion "))) {
			return delegateLogin(user, service, registry,
					password, request);
		}
		else {			
			return ecp(user, service, registry, password, request);
		}
	}

	@Path("/eppn-xml/{service}/{eppn}")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public ECPResponse ecpLoginXml(@PathParam("eppn") String eppn,
			@PathParam("service") String serviceShortName,
			@FormParam("password") String password, @Context HttpServletRequest request)
			throws IOException, ServletException, RestInterfaceException {

		ECPResponse response = new ECPResponse();
		
		UserEntity user = findUser(eppn);
		if (user == null) {
			generateFailXml(response, 400, "ecp login failed", "user-not-found");
			return response;
		}
		
		ServiceEntity service = findService(serviceShortName);
		if (service == null) {
			generateFailXml(response, 400, "ecp login failed", "no-such-service");
			return response;
		}
		
		RegistryEntity registry = findRegistry(user, service);
		if (registry == null) {
			generateFailXml(response, 400, "ecp login failed", "user-not-registered");
			return response;
		}
		
		if (password != null && (password.toLowerCase().startsWith("<?xml version") ||
				password.startsWith("<saml2:Assertion "))) {
			return delegateLoginXml(response, user, service, registry, password, request);
		}
		else {
			return ecpInternXml(response, user, service, registry, password, request);
		}
	}

	@Path("/regid/{regid}")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> ecpLogin(@PathParam("regid") Long regId,
			@FormParam("password") String password, @Context HttpServletRequest request)
			throws IOException, ServletException, RestInterfaceException {
		RegistryEntity registry = registryService.findById(regId);

		if (registry == null) {
			logger.info("No registry found for id {}", regId);
			throw new NoItemFoundException("registry unknown");
		}
		
		if (password != null && (password.toLowerCase().startsWith("<?xml version") ||
				password.startsWith("<saml2:Assertion "))) {
			return delegateLogin(registry.getUser(), registry.getService(), registry,
					password, request);
		}
		else {
			return ecp(registry.getUser(), registry.getService(), registry,
				password, request);
		}
	}

	@Path("/regid-xml/{regid}")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public ECPResponse ecpLoginXml(@PathParam("regid") Long regId,
			@FormParam("password") String password, @Context HttpServletRequest request)
			throws IOException, ServletException, RestInterfaceException {

		ECPResponse response = new ECPResponse();

		RegistryEntity registry = registryService.findById(regId);
		if (registry == null) {
			generateFailXml(response, 400, "ecp login failed", "user-not-registered");
			return response;
		}
		
		if (password != null && (password.toLowerCase().startsWith("<?xml version") ||
				password.startsWith("<saml2:Assertion "))) {
			return delegateLoginXml(response, registry.getUser(), registry.getService(), registry,
					password, request);
		}
		else {
			return ecpInternXml(response, registry.getUser(), registry.getService(), registry,
				password, request);
		}
	}

	private Map<String, String> ecp(UserEntity user, ServiceEntity service, RegistryEntity registry,
			String password, HttpServletRequest request) throws RestInterfaceException {

		logger.debug("Attempting ECP Authentication for {} and service {} (regId {})", user.getEppn(), service.getShortName(), registry.getId());

		String[] splits = user.getEppn().split("@");
		
		if (splits.length != 2) {
			throw new NoScopedUsernameException("username must be scoped");
		}
		String username = splits[0];
		String scope = splits[1];
		
		SamlIdpMetadataEntity idp = idpService.findByScope(scope);

		if (idp == null) {
			throw new NoIdpForScopeException("scope unknown");
		}

		try {
			EntityDescriptor idpEntityDesc = samlHelper.unmarshal(idp.getEntityDescriptor(), EntityDescriptor.class);
			if (idpEntityDesc == null) {
				logger.warn("EntityDescriptor for {} is not parsable", idp.getEntityId());
				throw new NoIdpFoundException("IDP metadata are not parseable");
			}
			
			SingleSignOnService sso = metadataHelper.getSSO(idpEntityDesc, SAMLConstants.SAML2_SOAP11_BINDING_URI);
			if (sso == null) {
				logger.warn("No SOAP Endpoint defined for {}", idp.getEntityId());
				throw new NoEcpSupportException("IDP is not compatible. SOAP ECP Endpoint is missing in metadata");
			}
			String bindingLocation = sso.getLocation();
			String bindingHost = (new URL(bindingLocation)).getHost();
			String hostname = request.getLocalName();
			logger.debug("hostname is {}", hostname);
			SamlSpConfigurationEntity sp = spService.findByHostname(hostname);
			
			if (sp == null) {
				logger.warn("No hostname configured for {}", hostname);
				throw new NoItemFoundException("No hostname configured");
			}
			
			AuthnRequest authnRequest = ssoHelper.buildAuthnRequest(sp.getEntityId(), sp.getEcp(),
					SAMLConstants.SAML2_PAOS_BINDING_URI);
			Envelope envelope = attrQueryHelper.buildSOAP11Envelope(authnRequest);
			BasicSOAPMessageContext soapContext = new BasicSOAPMessageContext();
			soapContext.setOutboundMessage(envelope);
			
			HttpClientBuilder clientBuilder = new HttpClientBuilder();
			HttpClient client = clientBuilder.buildClient();
			client.getState().setCredentials(
	                new AuthScope(bindingHost, 443),
	                new UsernamePasswordCredentials(username, password));
			HttpSOAPClient soapClient = new HttpSOAPClient(client, 
					samlHelper.getBasicParserPool());
		
			try {
				soapClient.send(bindingLocation, soapContext);
			} catch (SOAPClientException se) {
				logger.info("Login failed for user {} idp {}", username, idp.getEntityId());
				throw new LoginFailedException(se.getMessage());
			}
			Envelope returnEnvelope = (Envelope) soapContext.getInboundMessage();
			Response response = 
					attrQueryHelper.getResponseFromEnvelope(returnEnvelope);

			return processResponse(response, idpEntityDesc, service, idp, sp, "ecp");

		} catch (SOAPException e) {
			logger.info("exception at ecp query", e);
			throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
		} catch (SecurityException e) {
			logger.info("exception at ecp query", e);
			throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
		} catch (DecryptionException e) {
			logger.info("exception at ecp query", e);
			throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
		} catch (IOException e) {
			logger.info("exception at ecp query", e);
			throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
		} catch (SamlAuthenticationException e) {
			logger.info("exception at attribute query", e);
			throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
		}	
	}
	
	private ECPResponse ecpInternXml(ECPResponse response, UserEntity user, ServiceEntity service, RegistryEntity registry,
			String password, HttpServletRequest request)
			throws IOException, ServletException {

		try {
			ecp(user, service, registry, password, request);
		}
		catch (NoScopedUsernameException e) {
			generateFailXml(response, 400, "ecp login failed", "no-scoped-username");
			return response;
		}
		catch (NoIdpForScopeException e) {
			generateFailXml(response, 400, "ecp login failed", "no-idp-for-scope");
			return response;
		}
		catch (NoIdpFoundException e) {
			generateFailXml(response, 500, "ecp login failed", "idp-metadata-error");
			return response;
		}
		catch (NoEcpSupportException e) {
			generateFailXml(response, 400, "ecp login failed", "idp-ecp-not-supported");
			return response;
		}
		catch (NoHostnameConfiguredException e) {
			generateFailXml(response, 500, "ecp login failed", "no-hostname-configured");
			return response;
		}
		catch (LoginFailedException e) {
			generateFailXml(response, 500, "ecp login failed", "login-failed");
			return response;
		}
		catch (UserUpdateFailedException e) {
			generateFailXml(response, 400, "ecp login failed", "user-update-failed");
			return response;
		}
		catch (UnauthorizedException e) {
			response.setCode(405);
			response.setMessage("rules failed");
			
			for (UnauthorizedUser uu : e.getUnauthList()) {
				addXmlError(response, uu.getMessage(), resolveString(uu.getMessage()));
			}
		}
		catch (GenericRestInterfaceException e) {
			generateFailXml(response, 500, "ecp login failed", "generic-error");
			return response;
		}
		catch (RestInterfaceException e) {
			logger.warn("Unmapped RestInterfaceException!", e);
			generateFailXml(response, 400, "ecp login failed", "unknown-error");
			return response;
		}	
		
		response.setCode(200);
		response.setMessage("success");

		return response;
	}

	private ECPResponse delegateLoginXml(ECPResponse response, UserEntity user, ServiceEntity service, RegistryEntity registry,
			String password, HttpServletRequest request)
			throws IOException, ServletException {
		
		try {
			delegateLogin(user, service, registry, password, request);
		} 
		catch (NoDelegationConfiguredException e) {
			generateFailXml(response, 500, "ecp login failed", "delegation-not-configured");
			return response;
		}
		catch (AssertionException e) {
			generateFailXml(response, 400, "ecp login failed", e.getMessage());
			return response;
		}
		catch (LoginFailedException e) {
			generateFailXml(response, 500, "ecp login failed", "eppn-mismatch");
			return response;
		}
		catch (NoIdpFoundException e) {
			generateFailXml(response, 500, "ecp login failed", "idp-metadata-error");
			return response;
		}
		catch (NoHostnameConfiguredException e) {
			generateFailXml(response, 500, "ecp login failed", "no-hostname-configured");
			return response;
		}
		catch (UserUpdateFailedException e) {
			generateFailXml(response, 400, "ecp login failed", "user-update-failed");
			return response;
		}
		catch (UnauthorizedException e) {
			response.setCode(405);
			response.setMessage("rules failed");
			
			for (UnauthorizedUser uu : e.getUnauthList()) {
				addXmlError(response, uu.getMessage(), resolveString(uu.getMessage()));
			}
		}
		catch (GenericRestInterfaceException e) {
			generateFailXml(response, 500, "ecp login failed", "generic-error");
			return response;
		}
		catch (RestInterfaceException e) {
			logger.warn("Unmapped RestInterfaceException!", e);
			generateFailXml(response, 400, "ecp login failed", "unknown-error");
			return response;			
		}

		response.setCode(200);
		response.setMessage("success");

		return response;
	}

	private Map<String, String> delegateLogin(UserEntity user, ServiceEntity service, RegistryEntity registry,
			String password, HttpServletRequest request)
			throws IOException, ServletException, RestInterfaceException {
		
		if (! service.getServiceProps().containsKey("delegate_entities")) {
			throw new NoDelegationConfiguredException("delegation not possible");
		}
		
		List<String> delegateEntityList = Arrays.asList(service.getServiceProps().get("delegate_entities").split(" "));
		
		Long delegateAssertionTimeout;
		
		/* if not configured, use 4 hours */
		if (! service.getServiceProps().containsKey("delegate_assertion_timeout"))
			delegateAssertionTimeout = 4 * 60 * 60 * 1000L;
		else
			delegateAssertionTimeout = Long.parseLong(service.getServiceProps().get("delegate_assertion_timeout"));
			
		logger.info("Attempting delgated Authentication for {} and service {}", user.getEppn(), service.getShortName());

		Assertion assertion = samlHelper.unmarshal(password, Assertion.class);
		
		if (assertion == null)
			throw new AssertionException("assertion-invalid");

		if (assertion.getConditions() == null ||
				assertion.getConditions().getAudienceRestrictions() == null ||
				assertion.getConditions().getAudienceRestrictions().size() == 0)
			throw new AssertionException("audience-restriction-missing");
			
		List<AudienceRestriction> audienceRestrictionList = assertion.getConditions().getAudienceRestrictions();
		
		if (System.currentTimeMillis() - assertion.getConditions().getNotBefore().getMillis() > delegateAssertionTimeout) {
			logger.info("assertion is older than {} ms ({}, {})", delegateAssertionTimeout, user.getEppn(), service.getShortName());
			throw new AssertionException("assertion-too-old");
		}
		
		boolean audienceOk = false;
		
		for (AudienceRestriction audienceRestriction : audienceRestrictionList) {
			for (Audience audience : audienceRestriction.getAudiences()) {
				if (delegateEntityList.contains(audience.getAudienceURI())) {
					logger.debug("Audience hit: {}", audience.getAudienceURI());
					audienceOk = true;
					break;
				}
			}
		}
		
		if (! audienceOk) { 
			logger.info("assertion does not match a configured audience (delegate_entities) ({}, {})", user.getEppn(), service.getShortName());
			throw new AssertionException("assertion-not-from-configured-audience");
		}
		
		String hostname = request.getLocalName();
		logger.debug("hostname is {}", hostname);
		SamlSpConfigurationEntity sp = spService.findByHostname(hostname);
		
		if (sp == null) {
			logger.warn("No hostname configured for {}", hostname);
			throw new NoHostnameConfiguredException("No hostname configured");
		}
		
		SamlIdpMetadataEntity idp = idpService.findByEntityId(user.getIdp().getEntityId());

		if (idp == null) {
			logger.info("No IDP found for user {} by entityId {}", user.getEppn(), user.getIdp().getEntityId());
			throw new NoIdpFoundException("idp unknown");
		}

		if (assertion.getIssuer() != null && 
				(! assertion.getIssuer().getValue().equals(idp.getEntityId()))) {
			logger.info("User {} is from idp {}, but assertion from idp {}", user.getEppn(), idp.getEntityId(),
					assertion.getIssuer().getValue());
			throw new AssertionException("wrong-idp-for-user");
		}
		
		EntityDescriptor idpEntityDesc = samlHelper.unmarshal(idp.getEntityDescriptor(), EntityDescriptor.class);
		
		if (idpEntityDesc == null) {
			logger.warn("EntityDescriptor for {} is not parsable", idp.getEntityId());
			throw new NoIdpFoundException("IDP metadata are not parseable");
		}

		try {
			logger.debug("Validating Signature for " + assertion.getID());					
			saml2ResponseValidationService.validateIdpSignature(assertion, assertion.getIssuer(), idpEntityDesc);
			logger.debug("Validating Signature success for " + assertion.getID());
		} catch (SamlAuthenticationException e) {
			logger.info("Could not validate signature for user {}", user.getEppn());
			throw new AssertionException("assertion-signature-invalid");
		}					

		Map<String, List<Object>> attributeMap = saml2AssertionService.extractAttributes(assertion);
		
		String assertionEppn = attrHelper.getSingleStringFirst(attributeMap, "urn:oid:1.3.6.1.4.1.5923.1.1.1.6");
		
		if (assertionEppn != null &&
				assertionEppn.equals(user.getEppn())) {
			
			logger.debug("EPPN from assertion and login match");
			
			try {

				Response response = attrQueryHelper.query(user, idp, idpEntityDesc, sp);
				
				return processResponse(response, idpEntityDesc, service, idp, sp, "ecp-delegate");
			
			} catch (SOAPException e) {
				logger.info("exception at attribute query", e);
				throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
			} catch (MetadataException e) {
				logger.info("exception at attribute query", e);
				throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
			} catch (SecurityException e) {
				logger.info("exception at attribute query", e);
				throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
			} catch (DecryptionException e) {
				logger.info("exception at attribute query", e);
				throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
			} catch (SamlAuthenticationException e) {
				logger.info("exception at attribute query", e);
				throw new GenericRestInterfaceException("an error occured: " + e.getMessage());
			}
		}
		else {
			if (attributeMap.containsKey("urn:oid:1.3.6.1.4.1.5923.1.1.1.6"))
				logger.warn("User tried to login with wrong Eppn from Assertion {} <-> {}", user.getEppn(), attributeMap.get("urn:oid:1.3.6.1.4.1.5923.1.1.1.6"));
			else
				logger.warn("User {} tried to login with missing Eppn in Assertion", user.getEppn());
			throw new LoginFailedException("Assertion is for wrong EPPN");
		}
	}
	
	private Map<String, String> processResponse(Response samlResponse, EntityDescriptor idpEntityDescriptor,
			ServiceEntity service, SamlIdpMetadataEntity idp, SamlSpConfigurationEntity spEntity, String caller) 
					throws RestInterfaceException, IOException, DecryptionException, SamlAuthenticationException {
		
		Assertion assertion = saml2AssertionService.processSamlResponse(samlResponse, idp, idpEntityDescriptor, spEntity);

		String persistentId = saml2AssertionService.extractPersistentId(assertion, spEntity);
		
		UserEntity user = userService.findByPersistentWithRoles(spEntity.getEntityId(), 
				idp.getEntityId(), persistentId);
	
		if (user == null) {
			throw new UserNotRegisteredException("user not registered in webapp");
		}
		
		try {
			user = userUpdateService.updateUser(user, assertion, caller, service);
		} catch (UserUpdateException e) {
			logger.warn("Could not update user {}: {}", e.getMessage(), user.getEppn());
			throw new UserUpdateFailedException("user update failed: " + e.getMessage());
		}

		RegistryEntity registry = findRegistry(user, service);
		
		if (registry == null) {
			throw new NoRegistryFoundException("No such registry");
		}
			
		List<Object> objectList = checkRules(user, service, registry);
		List<OverrideAccess> overrideAccessList = extractOverideAccess(objectList);
		List<UnauthorizedUser> unauthorizedUserList = extractUnauthorizedUser(objectList);
		
		if (unauthorizedUserList.size() > 0 || overrideAccessList.size() == 0) {
			throw new UnauthorizedException(unauthorizedUserList);
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Map<String, String> map = new HashMap<String, String>();
		map.put("eppn", user.getEppn());
		map.put("email", user.getEmail());
		map.put("last_update",  df.format(user.getLastUpdate()));
		
		return map;
	}
	
	private ServiceEntity findService(String serviceShortName) {
		ServiceEntity service = serviceService.findByShortName(serviceShortName);
		
		if (service != null) {
			service = serviceService.findByIdWithServiceProps(service.getId());
		}
		
		return service;
	}

	private UserEntity findUser(String eppn) {
		UserEntity user = userService.findByEppn(eppn);

		if (user != null) {
			user = userService.findByIdWithStore(user.getId());
		}

		return user;
	}

	private RegistryEntity findRegistry(UserEntity user, ServiceEntity service) {
		RegistryEntity registry = registryService.findByServiceAndUserAndStatus(service, user, RegistryStatus.ACTIVE);
		
		if (registry == null) {
			/*
			 * Also check for Lost_access registries. They should also be allowed to be rechecked.
			 */
			registry = registryService.findByServiceAndUserAndStatus(service, user, RegistryStatus.LOST_ACCESS);
		}
		
		return registry;
	}

	private void generateFailXml(ECPResponse response, int code, String message, String error) {
		response.setCode(code);
		response.setMessage(message);
		addXmlError(response, error, resolveString(error));
	}
	
	private void addXmlError(ECPResponse response, String error, String errorDetail) {
		if (response.getErrorList() == null)
			response.setErrorList(new ArrayList<RestError>());
		RestError restError = new RestError();
		restError.setErrorShort(error);
		if (errorDetail != null)
			restError.setErrorDetail(errorDetail);
		response.getErrorList().add(restError);
	}
	
	private String resolveString(String key) {
		String enString = resolveString(key, Locale.ENGLISH);
		String deString = resolveString(key, Locale.GERMAN);
		
		StringBuffer sb = new StringBuffer();
		
		if (deString != null)
			sb.append(deString);
		
		if (deString != null && enString != null)
			sb.append(" / ");
		
		if (enString != null)
			sb.append(enString);
		
		if (sb.length() == 0)
			return null;
		else
			return sb.toString();
	}
	
	private String resolveString(String key, Locale locale) {
		try {
			TextPropertyEntity tpe = textPropertyDao.findByKeyAndLang(key, locale.getLanguage());

			if (tpe != null)
				return tpe.getValue();
			else {
				ResourceBundle bundle = ResourceBundle.getBundle("msg.messages", locale);		
				return bundle.getString(key);
			}
		}
		catch (Exception e) {
			return null;
		}
	}

	private List<Object> checkRules(UserEntity user, ServiceEntity service, RegistryEntity registry) {
		List<Object> objectList;
		
		if (service.getAccessRule() == null) {
			objectList = knowledgeSessionService.checkRule("default", "permitAllRule", "1.0.0", user, service, registry, "user-self", false);
		}
		else {
			BusinessRulePackageEntity rulePackage = service.getAccessRule().getRulePackage();
			if (rulePackage != null) {
				objectList = knowledgeSessionService.checkRule(rulePackage.getPackageName(), rulePackage.getKnowledgeBaseName(), 
					rulePackage.getKnowledgeBaseVersion(), user, service, registry, "user-self", false);
			}
			else {
				throw new IllegalStateException("checkServiceAccess called with a rule (" +
							service.getAccessRule().getName() + ") that has no rulePackage");
			}
		}

		return objectList;
	}

	private List<OverrideAccess> extractOverideAccess(List<Object> objectList) {
		List<OverrideAccess> returnList = new ArrayList<OverrideAccess>();
		
		for (Object o : objectList) {
			if (o instanceof OverrideAccess) {
				returnList.add((OverrideAccess) o);
			}
		}
		
		return returnList;
	}

	private List<UnauthorizedUser> extractUnauthorizedUser(List<Object> objectList) {
		List<UnauthorizedUser> returnList = new ArrayList<UnauthorizedUser>();
		
		for (Object o : objectList) {
			if (o instanceof UnauthorizedUser) {
				returnList.add((UnauthorizedUser) o);
			}
		}

		return returnList;
	}
}
