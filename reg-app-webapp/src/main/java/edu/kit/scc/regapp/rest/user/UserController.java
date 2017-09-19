package edu.kit.scc.regapp.rest.user;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.dto.entity.UserEntityDto;
import edu.kit.scc.regapp.dto.service.UserDtoService;
import edu.kit.scc.regapp.exc.RestInterfaceException;
import edu.kit.scc.regapp.session.SessionManager;

@Path("/user")
public class UserController {

	@Inject
	private UserDtoService service;
	
	@Inject
	private SessionManager session;
	
	@Path(value = "/detail")
	@Produces({"application/json"})
	@GET
	public UserEntityDto detail(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		
		return service.findById(session.getUserId());
	}
}
