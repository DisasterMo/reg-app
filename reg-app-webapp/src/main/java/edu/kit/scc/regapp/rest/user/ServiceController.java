package edu.kit.scc.regapp.rest.user;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.dto.service.ServiceShortDtoService;
import edu.kit.scc.regapp.exc.RestInterfaceException;

@Path("/service")
public class ServiceController {

	@Inject
	private ServiceShortDtoService shortService;
	
	@Path(value = "/detail/{id}")
	@Produces({"application/json"})
	@GET
	public ServiceEntityDto detail(@PathParam("id") Long id, @Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		
		return shortService.findById(id);
	}	
}
