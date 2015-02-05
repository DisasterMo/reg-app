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
package edu.kit.scc.webreg.sec;

import java.io.IOException;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.Configuration;
import org.opensaml.saml2.core.AttributeQuery;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.security.SecurityException;
import org.slf4j.Logger;

import edu.kit.scc.webreg.bootstrap.ApplicationConfig;
import edu.kit.scc.webreg.entity.SamlAAConfigurationEntity;
import edu.kit.scc.webreg.entity.SamlSpMetadataEntity;
import edu.kit.scc.webreg.exc.SamlAuthenticationException;
import edu.kit.scc.webreg.service.SamlIdpMetadataService;
import edu.kit.scc.webreg.service.SamlSpMetadataService;
import edu.kit.scc.webreg.service.saml.Saml2DecoderService;
import edu.kit.scc.webreg.service.saml.Saml2ResponseValidationService;
import edu.kit.scc.webreg.service.saml.SamlHelper;

@ApplicationScoped
public class Saml2AttributeQueryServlet {

	@Inject
	private Logger logger;

	@Inject
	private Saml2DecoderService saml2DecoderService;
	
	@Inject
	private Saml2ResponseValidationService saml2ValidationService;
	
	@Inject
	private SamlHelper samlHelper;
		
	@Inject 
	private SamlIdpMetadataService idpService;
	
	@Inject 
	private SamlSpMetadataService spMetadataService;

	@Inject
	private ApplicationConfig appConfig;
	
	public void service(ServletRequest servletRequest, ServletResponse servletResponse, SamlAAConfigurationEntity aaConfig)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		logger.debug("Consuming SAML AttributeQuery");
		
		try {
			AttributeQuery query = saml2DecoderService.decodeAttributeQuery(request);
			logger.debug("SAML AttributeQuery decoded");

			Issuer issuer = query.getIssuer();
			if (issuer == null || issuer.getValue() == null) {
				throw new SamlAuthenticationException("Issuer not set");
			}

			String issuerString = issuer.getValue();
			SamlSpMetadataEntity spEntity = spMetadataService.findByEntityId(issuerString);
			if (spEntity == null)
				throw new SamlAuthenticationException("Issuer metadata not in database");
			
			EntityDescriptor spEntityDescriptor = samlHelper.unmarshal(
					spEntity.getEntityDescriptor(), EntityDescriptor.class);
			
			saml2ValidationService.verifyIssuer(spEntity, query);
			saml2ValidationService.validateSpSignature(query, issuer, spEntityDescriptor);
			
			Response samlResponse = buildSamlRespone(StatusCode.SUCCESS_URI, null);
			
			Envelope envelope = buildSoapEnvelope(samlResponse);
			response.getWriter().print(samlHelper.marshal(envelope));
			
		} catch (MessageDecodingException e) {
			logger.info("Could not execute AttributeQuery: {}", e.getMessage());
			sendErrorResponse(response, StatusCode.REQUEST_DENIED_URI, e.getMessage());
		} catch (SecurityException e) {
			logger.info("Could not execute AttributeQuery: {}", e.getMessage());
			sendErrorResponse(response, StatusCode.REQUEST_DENIED_URI, e.getMessage());
		} catch (SamlAuthenticationException e) {
			logger.info("Could not execute AttributeQuery: {}", e.getMessage());
			sendErrorResponse(response, StatusCode.REQUEST_DENIED_URI, e.getMessage());
		}
	}

	private void sendErrorResponse(HttpServletResponse response, String statusCodeString, String messageString) 
			throws IOException {
		Response samlResponse = buildSamlRespone(statusCodeString, messageString);
		
		Envelope envelope = buildSoapEnvelope(samlResponse);
		response.getWriter().print(samlHelper.marshal(envelope));
	}
	
	private Envelope buildSoapEnvelope(XMLObject xmlObject) {
		XMLObjectBuilderFactory bf = Configuration.getBuilderFactory();
		Envelope envelope = (Envelope) bf.getBuilder(
				Envelope.DEFAULT_ELEMENT_NAME).buildObject(
				Envelope.DEFAULT_ELEMENT_NAME);
		Body body = (Body) bf.getBuilder(Body.DEFAULT_ELEMENT_NAME)
				.buildObject(Body.DEFAULT_ELEMENT_NAME);

		body.getUnknownXMLObjects().add(xmlObject);
		envelope.setBody(body);		
		return envelope;
	}
	
	private Response buildSamlRespone(String statusCodeString, String messageString) {
		Response samlResponse = samlHelper.create(Response.class, Response.DEFAULT_ELEMENT_NAME);
		samlResponse.setStatus(buildSamlStatus(statusCodeString, messageString));
		return samlResponse;
	}
	
	private Status buildSamlStatus(String statusCodeString, String messageString) {
		StatusCode statusCode = samlHelper.create(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
		statusCode.setValue(statusCodeString);
		
		Status samlStatus = samlHelper.create(Status.class, Status.DEFAULT_ELEMENT_NAME);
		samlStatus.setStatusCode(statusCode);

		if (messageString != null) {
			StatusMessage statusMessage = samlHelper.create(StatusMessage.class, StatusMessage.DEFAULT_ELEMENT_NAME);
			statusMessage.setMessage(messageString);
			samlStatus.setStatusMessage(statusMessage);
		}
		return samlStatus;
	}
}
