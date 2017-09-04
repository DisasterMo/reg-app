package edu.kit.scc.regapp.rest.auth;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.dto.entity.AuthMechEntityDto;
import edu.kit.scc.regapp.dto.service.AuthMechDtoService;
import edu.kit.scc.regapp.exc.RestInterfaceException;
import edu.kit.scc.regapp.sec.SessionManager;

@Path("/auth/")
public class AuthController {

	@Inject
	private SessionManager sessionManager;
	
	@Inject
	private AuthMechDtoService authMechDtoService;
	
	@Path(value = "/info")
	@Produces({"application/json"})
	@GET
	public AuthInfo info(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		AuthInfo authInfo = new AuthInfo();
		if (request.getSession(false) == null) {
			/**
			 * No session established yet
			 */
			authInfo.setLoggedIn(false);
		}
		else {
			/**
			 * Session is established. SessionManager should contain
			 * sessionData.
			 */
			if (sessionManager.getUserId() != null) {
				authInfo.setLoggedIn(true);
				authInfo.setUserId(sessionManager.getUserId());
			}
			else {
				authInfo.setLoggedIn(false);
			}
		}

		return authInfo;
	}
	
	@Path(value = "/mech/list")
	@Produces({"application/json"})
	@GET
	public List<AuthMechEntityDto> mechList(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		String hostname = request.getLocalName();
		return authMechDtoService.findByHostname(hostname);
	}
}
