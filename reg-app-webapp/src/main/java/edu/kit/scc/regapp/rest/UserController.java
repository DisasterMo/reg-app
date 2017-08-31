package edu.kit.scc.regapp.rest;

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

@Path("/user")
public class UserController {

	@Inject
	private UserDtoService service;
	
	@Path(value = "/detail")
	@Produces({"application/json"})
	@GET
	public UserEntityDto updateUserAsync(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		
		return service.findById(1009L);
	}
}
