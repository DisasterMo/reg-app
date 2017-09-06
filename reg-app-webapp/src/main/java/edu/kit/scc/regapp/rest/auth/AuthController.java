package edu.kit.scc.regapp.rest.auth;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import edu.kit.scc.regapp.dto.entity.AuthMechEntityDto;
import edu.kit.scc.regapp.dto.service.AuthMechDtoService;
import edu.kit.scc.regapp.entity.account.LocalAccountEntity;
import edu.kit.scc.regapp.exc.LoginFailedException;
import edu.kit.scc.regapp.exc.RestInterfaceException;
import edu.kit.scc.regapp.sec.SessionManager;
import edu.kit.scc.regapp.service.auth.LocalUPAuthService;

@Path("/auth/")
public class AuthController {

	@Inject
	private Logger logger;
	
	@Inject
	private SessionManager sessionManager;
	
	@Inject
	private AuthMechDtoService authMechDtoService;
	
	@Inject
	private LocalUPAuthService localUPAuthService;
	
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
	
	@Path(value = "/locallogin/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"application/json"})
	@POST
	public void localLogin(AuthMechEntityDto authMechDto, @PathParam("id") Long id, @Context HttpServletRequest request)
			throws IOException, RestInterfaceException, ServletException {
		logger.info("processing local login request for auth mech {} with username {}", id, authMechDto.getUsername());
		LocalAccountEntity entity = localUPAuthService.checkUsernamePassword(authMechDto.getUsername(), authMechDto.getPassword());
		if (entity == null) {
			logger.info("Login failed for {}", authMechDto.getUsername());
			throw new LoginFailedException("login failed");
		}
		else {
			logger.info("Login succeded for {}. Setting userId.", authMechDto.getUsername());
			sessionManager.setUserId(entity.getUser().getId());
		}
	}
}