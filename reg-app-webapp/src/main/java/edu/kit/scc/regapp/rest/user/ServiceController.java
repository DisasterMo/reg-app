package edu.kit.scc.regapp.rest.user;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.dto.service.ServicePolicyDtoService;
import edu.kit.scc.regapp.dto.service.ServiceShortDtoService;
import edu.kit.scc.regapp.exc.RestInterfaceException;
import edu.kit.scc.regapp.session.SessionManager;

@Path("/service")
public class ServiceController {

	@Inject
	private ServiceShortDtoService shortService;
	
	@Inject
	private ServicePolicyDtoService policyService;
	
	@Inject
	private SessionManager session;
	
	@Path(value = "/detail/{id}")
	@Produces({"application/json"})
	@GET
	public ServiceEntityDto detail(@PathParam("id") Long id, @Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		
		return policyService.findById(id);
	}
	
	@Path(value = "/available/list")
	@Produces({"application/json"})
	@GET
	public List<ServiceEntityDto> visible(@Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		
		return shortService.findServicesAvailableForUser(session.getUserId());
	}		
}
