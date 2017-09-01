package edu.kit.scc.regapp.rest.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.exc.RestInterfaceException;

@Path("/auth/")
public class AuthInfoController {

	@Path(value = "/info")
	@Produces({"application/json"})
	@GET
	public AuthInfo info(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		AuthInfo authInfo = new AuthInfo();
		authInfo.setLoggedIn(true);
		return authInfo;
	}
}
