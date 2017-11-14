package edu.kit.scc.regapp.bpm;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;

@ApplicationScoped
public class BpmProcessWorker {

	@Inject
	private Logger logger;
	
	@Inject
    private KnowledgeSessionWorker knowledgeSessionWorker;
	
    public void startProcess(String deploymentId, String processId) {
    	KieSession session = knowledgeSessionWorker.getStatefulSession(deploymentId);
    	ProcessInstance pin = session.startProcess(processId, new HashMap<String, Object>());
    	logger.info("Process {} created: id {}", deploymentId, pin.getId());
    }

}
