package edu.kit.scc.regapp.rest.auth;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.dto.entity.SamlAuthFederationEntityDto;
import edu.kit.scc.regapp.dto.service.SamlAuthFederationDtoService;
import edu.kit.scc.regapp.exc.RestInterfaceException;

@Path("/auth/saml/federation")
public class AuthSamlFederationController {

	@Inject
	private SamlAuthFederationDtoService samlAuthFederationDtoService;
	
	@Path(value = "/list/{id}")
	@Produces({"application/json"})
	@GET
	public SamlAuthFederationEntityDto info(@PathParam("id") Long id, @Context HttpServletRequest request)
					throws IOException, RestInterfaceException, ServletException {
		return samlAuthFederationDtoService.findById(id);
	}
	
}