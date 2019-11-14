package edu.kit.scc.webreg.service.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.saml.security.impl.SAMLMetadataSignatureSigningParametersResolver;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;

import edu.kit.scc.webreg.dao.RegistryDao;
import edu.kit.scc.webreg.dao.SamlAuthnRequestDao;
import edu.kit.scc.webreg.dao.SamlIdpConfigurationDao;
import edu.kit.scc.webreg.dao.SamlSpMetadataDao;
import edu.kit.scc.webreg.dao.ServiceSamlSpDao;
import edu.kit.scc.webreg.dao.UserDao;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.RegistryStatus;
import edu.kit.scc.webreg.entity.SamlAuthnRequestEntity;
import edu.kit.scc.webreg.entity.SamlIdpConfigurationEntity;
import edu.kit.scc.webreg.entity.SamlSpMetadataEntity;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.ServiceSamlSpEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.exc.SamlAuthenticationException;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

@Stateless
public class SamlIdpServiceImpl implements SamlIdpService {

	@Inject
	private Logger logger;

	@Inject
	private UserDao userDao;

	@Inject
	private RegistryDao registryDao;
	
	@Inject
	private SamlIdpConfigurationDao idpConfigDao;
	
	@Inject
	private SamlAuthnRequestDao samlAuthnRequestDao;

	@Inject
	private SamlSpMetadataDao spDao;
	
	@Inject
	private ServiceSamlSpDao serviceSamlSpDao;
	
	@Inject
	private SamlHelper samlHelper;
	
	@Inject
	private SsoHelper ssoHelper;
	
	@Inject
	private CryptoHelper cryptoHelper;
	
	@Override
	public long registerAuthnRequest(AuthnRequest authnRequest) {
		SamlAuthnRequestEntity authnRequestEntity = samlAuthnRequestDao.createNew();
		authnRequestEntity.setValidUntil(new Date(System.currentTimeMillis() + 30L * 60L * 1000L));
		authnRequestEntity.setAuthnrequestData(samlHelper.prettyPrint(authnRequest));
		authnRequestEntity = samlAuthnRequestDao.persist(authnRequestEntity);
		return authnRequestEntity.getId();
	}

	@Override
	public void resumeAuthnRequest(Long authnRequestId, Long userId, 
			Long authnRequestIdpConfigId, HttpServletResponse response) throws SamlAuthenticationException {
		
		SamlIdpConfigurationEntity idpConfig = idpConfigDao.findById(authnRequestIdpConfigId);
		logger.debug("IDP Config loaded: {}", idpConfig.getEntityId());
		
		UserEntity user = userDao.findById(userId);
		logger.debug("User loaded: {}", user.getId());

		SamlAuthnRequestEntity authnRequestEntity = samlAuthnRequestDao.findById(authnRequestId);
		AuthnRequest authnRequest = samlHelper.unmarshal(authnRequestEntity.getAuthnrequestData(), AuthnRequest.class);
		
		logger.debug("Authn request reloaded: {}", samlHelper.prettyPrint(authnRequest));

		SamlSpMetadataEntity spMetadata = spDao.findByEntityId(authnRequest.getIssuer().getValue());
		logger.debug("Corresponding SP found in Metadata: {}", spMetadata.getEntityId());

		List<ServiceSamlSpEntity> serviceSamlSpEntityList = serviceSamlSpDao.findBySamlSp(spMetadata);
		for (ServiceSamlSpEntity serviceSamlSpEntity : serviceSamlSpEntityList) {
			ServiceEntity service = serviceSamlSpEntity.getService();
			logger.debug("Service for SP found: {}", service);
			RegistryEntity registry = registryDao.findByServiceAndUserAndStatus(service, user, RegistryStatus.ACTIVE);
			if (registry == null) {
				try {
					logger.info("No active registration for user {} and service {}, redirecting to register page", 
							user.getEppn(), service.getName());
					response.sendRedirect("/user/register-service.xhtml?serviceId=" + service.getId());
					return;
				} catch (IOException e) {
					logger.warn("Cannot send to register page", e);
					throw new SamlAuthenticationException("Cannot send to register page");
				}
			}
		}
		
		Response samlResponse = ssoHelper.buildAuthnResponse(authnRequest, idpConfig.getEntityId());

		Audience audience = samlHelper.create(Audience.class, Audience.DEFAULT_ELEMENT_NAME);
		audience.setAudienceURI(spMetadata.getEntityId());
		AudienceRestriction ar = samlHelper.create(AudienceRestriction.class, AudienceRestriction.DEFAULT_ELEMENT_NAME);
		ar.getAudiences().add(audience);
		
		Conditions conditions = samlHelper.create(Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
		conditions.setNotBefore(new DateTime());
		conditions.setNotOnOrAfter(new DateTime(System.currentTimeMillis() + (5L * 60L * 1000L)));
		conditions.getAudienceRestrictions().add(ar);
		
		AuthnContextClassRef accr = samlHelper.create(AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
		AuthnContext ac = samlHelper.create(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
		ac.setAuthnContextClassRef(accr);
		AuthnStatement as = samlHelper.create(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
		as.setAuthnContext(ac);
		as.setAuthnInstant(new DateTime());
		as.setSessionNotOnOrAfter(new DateTime(System.currentTimeMillis() + (5L * 60L * 1000L)));
		as.setSessionIndex(samlHelper.getRandomId());
		
		Assertion assertion = samlHelper.create(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
		assertion.setID(samlHelper.getRandomId());
		assertion.setIssueInstant(new DateTime());
		assertion.setIssuer(ssoHelper.buildIssuser(idpConfig.getEntityId()));
		assertion.setSubject(ssoHelper.buildSubject(samlHelper.getRandomId(), NameID.TRANSIENT, authnRequest.getID()));
		assertion.setConditions(conditions);
		assertion.getAttributeStatements().add(buildAttributeStatement(user));
		assertion.getAuthnStatements().add(as);
		
		SecurityParametersContext securityContext = buildSecurityContext(idpConfig);
		HTTPPostEncoder postEncoder = new HTTPPostEncoder();
		postEncoder.setHttpServletResponse(response);
		MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();

		/*
		 * encrypt assertion
		 */
		try {
			samlResponse.getEncryptedAssertions().add(encryptAssertion(assertion, spMetadata, messageContext));
		} catch (SamlAuthenticationException e) {
			throw new SamlAuthenticationException(e);
		}
		
		messageContext.setMessage(samlResponse);

		/*
		 * signing response
		 */
		messageContext.addSubcontext(securityContext);

		SAMLPeerEntityContext entityContext = new SAMLPeerEntityContext();
		SAMLEndpointContext endpointContext = new SAMLEndpointContext();
		
		AssertionConsumerService acs = samlHelper.create(AssertionConsumerService.class, AssertionConsumerService.DEFAULT_ELEMENT_NAME);
		acs.setLocation(authnRequest.getAssertionConsumerServiceURL());
		endpointContext.setEndpoint(acs);
		entityContext.addSubcontext(endpointContext);
		messageContext.addSubcontext(entityContext);
		
		try {
			SAMLMessageSecuritySupport.signMessage(messageContext);
		} catch (SecurityException | MarshallingException | SignatureException e) {
			throw new SamlAuthenticationException(e);
		}
		
		postEncoder.setMessageContext(messageContext);

		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.log4j.logger", "root");
		engine.setProperty("resource.loader", "class");
		engine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine.init();
		postEncoder.setVelocityEngine(engine);
		
		logger.debug(samlHelper.prettyPrint(samlResponse));
		
		try {
			postEncoder.initialize();
			postEncoder.encode();
		} catch (MessageEncodingException | ComponentInitializationException e) {
			logger.warn("Exception occured", e);
			throw new SamlAuthenticationException(e);
		}
	}
	
	private SecurityParametersContext buildSecurityContext(SamlIdpConfigurationEntity idpConfig) throws SamlAuthenticationException {
		PrivateKey privateKey;
		X509Certificate publicKey;
		try {
			privateKey = cryptoHelper.getPrivateKey(idpConfig.getPrivateKey());
			publicKey = cryptoHelper.getCertificate(idpConfig.getCertificate());
		} catch (IOException e) {
			throw new SamlAuthenticationException("Private key is not set up properly", e);
		}

		BasicX509Credential credential = new BasicX509Credential(publicKey, privateKey);
		List<Credential> credentialList = new ArrayList<Credential>();
		credentialList.add(credential);
		
		BasicSignatureSigningConfiguration ssConfig = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();
		ssConfig.setSigningCredentials(credentialList);
		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(new SignatureSigningConfigurationCriterion(ssConfig));
		SAMLMetadataSignatureSigningParametersResolver smsspr = new SAMLMetadataSignatureSigningParametersResolver();

		SignatureSigningParameters ssp;
		try {
			ssp = smsspr.resolveSingle(criteriaSet);
		} catch (ResolverException e) {
			throw new SamlAuthenticationException(e);
		}
		logger.debug("Resolved algo {} for signing", ssp.getSignatureAlgorithm());
		SecurityParametersContext securityContext = new SecurityParametersContext();
		securityContext.setSignatureSigningParameters(ssp);
		
		return securityContext;
	}
	
	private EncryptedAssertion encryptAssertion(Assertion assertion, SamlSpMetadataEntity spMetadata, MessageContext<?> messageContext) throws SamlAuthenticationException {

		EntityDescriptor ed = samlHelper.unmarshal(spMetadata.getEntityDescriptor(), EntityDescriptor.class);
		
		KeyDescriptor keyDescriptor = null;
		SPSSODescriptor spsso = ed.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
		for (KeyDescriptor kd : spsso.getKeyDescriptors()) {
			if (kd.getUse() == null || kd.getUse().equals(UsageType.ENCRYPTION)) {
				keyDescriptor = kd;
				break;
			}
		}
		
		if (keyDescriptor == null) {
			return null;
		}
		
		KeyInfo keyInfo = keyDescriptor.getKeyInfo();
		X509Data x509Data = keyInfo.getX509Datas().get(0);
		org.opensaml.xmlsec.signature.X509Certificate x509cert = x509Data.getX509Certificates().get(0);
		String cert = x509cert.getValue();
		Encrypter enc = buildEncrypter(cert, messageContext, spMetadata.getEntityId());
		try {
			return enc.encrypt(assertion);
		} catch (EncryptionException e) {
			throw new SamlAuthenticationException("exception", e);
		}
	}
	
	private Encrypter buildEncrypter(String cert, MessageContext<?> messageContext, String spEntityId) 
			throws SamlAuthenticationException {
		try {
			byte[] decodedCert = Base64.decodeBase64(cert);
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		    InputStream in = new ByteArrayInputStream(decodedCert);
		    X509Certificate certificate = (X509Certificate)certFactory.generateCertificate(in);

		    BasicCredential encryptCredential = new BasicCredential(certificate.getPublicKey());

		    final BasicKeyInfoGeneratorFactory generator = new BasicKeyInfoGeneratorFactory();
			generator.setEmitPublicKeyValue(true);

		    EncryptionParameters encParams = new EncryptionParameters();
			encParams.setDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
			encParams.setDataKeyInfoGenerator(generator.newInstance());
			encParams.setKeyTransportEncryptionAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
			encParams.setKeyTransportEncryptionCredential(encryptCredential);
			encParams.setKeyTransportKeyInfoGenerator(generator.newInstance());

			messageContext.getSubcontext(EncryptionContext.class, true)
				.setAssertionEncryptionParameters(encParams);

			Encrypter encrypter = new Encrypter(new DataEncryptionParameters(encParams), 
					new KeyEncryptionParameters(encParams, spEntityId));
			return encrypter;
		} catch (CertificateException e) {
			throw new SamlAuthenticationException("Certificate cannot be read", e);
		}
	}	

	
	private AttributeStatement buildAttributeStatement(UserEntity user) {
		AttributeStatement attributeStatement = samlHelper.create(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
		attributeStatement.getAttributes().add(buildAttribute(
				"urn:oid:1.3.6.1.4.1.5923.1.1.1.6", "eduPersonPrincipalName", Attribute.URI_REFERENCE, user.getEppn()));
		attributeStatement.getAttributes().add(buildAttribute(
				"urn:oid:0.9.2342.19200300.100.1.3", "mail", Attribute.URI_REFERENCE, user.getEmail()));
		
		return attributeStatement;
	}
	
	private Attribute buildAttribute(String name, String friendlyName, String nameFormat, String... values) {
		Attribute attribute = samlHelper.create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
		attribute.setName(name);
		attribute.setFriendlyName(friendlyName);
		attribute.setNameFormat(nameFormat);
		
		for (String value : values) {
			XSString xsany = samlHelper.create(XSString.class, XSString.TYPE_NAME, AttributeValue.DEFAULT_ELEMENT_NAME);
			xsany.setValue(value);
			attribute.getAttributeValues().add(xsany);
		}
		
		return attribute;
	}	
}
