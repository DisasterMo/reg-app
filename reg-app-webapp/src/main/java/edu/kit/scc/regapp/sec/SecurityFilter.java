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
package edu.kit.scc.regapp.sec;

import java.io.IOException;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.entity.RoleEntity;
import edu.kit.scc.regapp.service.role.RoleService;
import edu.kit.scc.regapp.service.tools.PasswordUtil;
import edu.kit.scc.regapp.session.SessionManager;

@Named
@WebFilter(urlPatterns = {"/*"})
public class SecurityFilter implements Filter {

	public static final String ADMIN_USER_ID = "_admin_user_id";
	public static final String USER_ID = "_user_id";
	public static final String DIRECT_USER_ID = "_direct_user_id";
	public static final String DIRECT_USER_PW = "_direct_user_pw";
	
	@Inject 
	private Logger logger;
	
	@Inject
	private SessionManager session;
	
	@Inject
	private AccessChecker accessChecker;
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private ApplicationConfig appConfig;

	@Inject
	private PasswordUtil passwordUtil;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		if (request.getCharacterEncoding() == null) {
		    request.setCharacterEncoding("UTF-8");
		}
		
		String context = request.getServletContext().getContextPath();
		String path = request.getRequestURI().substring(
				context.length());
		
		HttpSession httpSession = request.getSession(false);
		
		if (logger.isTraceEnabled())
			logger.trace("Prechain Session is: {}", httpSession);

		/**
		 * No access check with these paths
		 */
		if (path.startsWith("/rest/auth/") ||
			path.startsWith("/openid/") ||
			path.startsWith("/Shibboleth.sso/")
				) {
			chain.doFilter(servletRequest, servletResponse);
		}
		/**
		 * Session is established and userId is set. This means user has logged in
		 */
		else if ((session != null) && (session.getUserId() != null)) {

			Set<RoleEntity> roles = new HashSet<RoleEntity>(roleService.findByUserId(session.getUserId()));
			session.addRoles(roles);
			
			if (accessChecker.check(path, roles)) {
    			request.setAttribute(USER_ID, session.getUserId());
				chain.doFilter(servletRequest, servletResponse);
			}
			else
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not allowed");
		}
		else {
			logger.debug("User from {} not logged in. Sending Unauthorized", request.getRemoteAddr());
			
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
		}
		
		if (logger.isTraceEnabled()) {
			httpSession = request.getSession(false);
			logger.trace("Postchain Session is: {}", httpSession);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (Security.getProvider("BC") == null) {
			logger.info("Register bouncy castle crypto provider");
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		}
	}

	private String getFullURL(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder(request.getRequestURI());
		String query = request.getQueryString();
		
		if (query != null) {
			sb.append("?");
			sb.append(query);
		}
		
		return sb.toString();
	}
}
