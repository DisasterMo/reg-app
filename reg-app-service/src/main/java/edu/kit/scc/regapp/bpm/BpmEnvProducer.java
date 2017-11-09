package edu.kit.scc.regapp.bpm;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.cdi.Selectable;
import org.jbpm.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.task.api.UserInfo;

public class BpmEnvProducer {

    @Selectable
    private UserGroupInfoProducer userGroupInfoProducer;

    @Inject
    @Kjar
    private DeploymentService deploymentService;

    @Produces
    public org.kie.api.task.UserGroupCallback produceSelectedUserGroupCalback() {
        return userGroupInfoProducer.produceCallback();
    }

    @Produces
    public UserInfo produceUserInfo() {
        return userGroupInfoProducer.produceUserInfo();
    }

    @Produces
    @Named("Logs")
    public TaskLifeCycleEventListener produceTaskAuditListener() {
        return new JPATaskLifeCycleEventListener(true);
    }

    @Produces
    public DeploymentService getDeploymentService() {
        return this.deploymentService;
    }

    @Produces
    public IdentityProvider produceIdentityProvider() {
        return new IdentityProvider() {

			@Override
			public String getName() {
				return null;
			}

			@Override
			public List<String> getRoles() {
				return null;
			}

			@Override
			public boolean hasRole(String role) {
				return false;
			}
        };
    }
}
