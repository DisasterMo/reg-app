package edu.kit.scc.regapp.rest.user;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.dto.entity.RegistryEntityDto;
import edu.kit.scc.regapp.dto.service.RegistryDtoService;
import edu.kit.scc.regapp.exc.RestInterfaceException;
import edu.kit.scc.regapp.session.SessionManager;

@Path("/user/registry")
public class UserRegistryController {

	@Inject
	private RegistryDtoService service;
	
	@Inject
	private SessionManager session;
	
	@Path(value = "/list")
	@Produces({"application/json"})
	@GET
	public List<RegistryEntityDto> list(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		
		return service.findRegistriesForUserDisplay(session.getUserId());
	}
}
