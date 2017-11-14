package edu.kit.scc.regapp.rest.bpm;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import edu.kit.scc.regapp.bpm.BpmProcessWorker;
import edu.kit.scc.regapp.exc.RestInterfaceException;

@Path("/bpm/")
public class BpmStartController {

	@Inject
	private BpmProcessWorker bpmWorker;
	
	@Path(value = "/start/{id}/{pid}")
	@Produces({"application/json"})
	@GET
	public String info(@Context HttpServletRequest request, @PathParam("id") String deploymentId, 
			@PathParam("pid") String processId)
					throws IOException, RestInterfaceException, ServletException {
		bpmWorker.startProcess(deploymentId, processId);
		return "bimm";
	}
	

}
