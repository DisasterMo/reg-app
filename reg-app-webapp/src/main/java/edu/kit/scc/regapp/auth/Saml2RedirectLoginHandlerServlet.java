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
package edu.kit.scc.regapp.auth;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.encoder.MessageEncodingException;
import org.slf4j.Logger;

import edu.kit.scc.regapp.exc.LoginFailedException;
import edu.kit.scc.regapp.sec.SessionManager;
import edu.kit.scc.regapp.service.auth.SamlRedirectService;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

@Named
@WebServlet(urlPatterns = {"/Shibboleth.sso/Login", "/saml/login"})
public class Saml2RedirectLoginHandlerServlet implements Servlet {

	@Inject
	private Logger logger;

	@Inject
	private SessionManager session;

	@Inject
	private SamlRedirectService samlRedirectService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
	}

	@Override
	public void service(ServletRequest servletRequest, ServletResponse servletResponse)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String authMechString = request.getParameter("mech");
		String idpString = request.getParameter("idp");
		
		if ((session == null) || (authMechString == null) || (idpString == null)) {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "parameters are wrong");
			return;
		}
		
		try {
			Long authMethId = Long.parseLong(authMechString);
			Long idpId = Long.parseLong(idpString);
		
			samlRedirectService.redirectClient(authMethId, idpId, response);

		} catch (MessageEncodingException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cannot encode saml message");
        } catch (ComponentInitializationException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cannot initialize saml component");
	    } catch (LoginFailedException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
	    } catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
	}	
}
