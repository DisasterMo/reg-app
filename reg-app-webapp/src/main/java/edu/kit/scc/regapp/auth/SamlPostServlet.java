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

import org.slf4j.Logger;

import edu.kit.scc.regapp.exc.SamlAuthenticationException;
import edu.kit.scc.regapp.service.auth.SamlPostService;

@Named
@WebServlet(urlPatterns = {"/Shibboleth.sso/SAML2/POST", "/saml/post"})
public class SamlPostServlet implements Servlet {

	@Inject
	private Logger logger;

	@Inject
	private SamlPostService samlPostService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
	}

	@Override
	public void service(ServletRequest servletRequest, ServletResponse servletResponse)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		logger.debug("Handling saml post request");

		try {
			samlPostService.consumePost(request, response);
		} catch (SamlAuthenticationException e) {
			logger.info("Exception while handling post assertion", e);
			String errorString = e.getMessage();
			if (e.getCause() != null)
				errorString += "(" + e.getCause().getMessage() + ")";
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Handling post request failed: " + errorString);
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
