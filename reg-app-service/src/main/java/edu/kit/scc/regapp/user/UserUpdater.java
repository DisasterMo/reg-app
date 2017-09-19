package edu.kit.scc.regapp.user;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.audit.UserUpdateAuditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.dao.audit.AuditDetailDao;
import edu.kit.scc.regapp.dao.audit.AuditEntryDao;
import edu.kit.scc.regapp.entity.EventType;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.account.AccountEntity;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.event.UserEvent;
import edu.kit.scc.regapp.exc.EventSubmitException;
import edu.kit.scc.regapp.exc.UserUpdateException;

@ApplicationScoped
public class UserUpdater implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Logger logger;
	
	@Inject
	private AuditEntryDao auditDao;
	
	@Inject
	private AuditDetailDao auditDetailDao;
	
	@Inject
	protected ApplicationConfig appConfig;
	
	@Inject
	private EventSubmitter eventSubmitter;
	
	public void updateUser(AccountEntity account, Auditor parentAuditor, ServiceEntity service)
			throws UserUpdateException {
		logger.debug("Updating user for account {}", account.getGlobalId());

		UserEntity user = account.getUser();
		
		boolean changed = false;
		
		UserUpdateAuditor auditor = new UserUpdateAuditor(auditDao, auditDetailDao, appConfig);
		auditor.setParent(parentAuditor);
		auditor.startAuditTrail(parentAuditor.getActualExecutor());
		auditor.setName(getClass().getName() + "-UserUpdate-Audit");
		auditor.setDetail("Update user " + user.getId());

		logger.info("User {} has {} associated accounts", user.getId(), user.getAccounts().size());
		
		if (user.getPreferredAccount() == null) {
			user.setPreferredAccount(account);
		}
		
        if (changed) {
        	fireUserChangeEvent(user, auditor.getActualExecutor(), auditor);
        }
		
		auditor.setUser(user);
		auditor.finishAuditTrail();
		auditor.commitAuditTrail();
	}
	
	protected void fireUserChangeEvent(UserEntity user, String executor, Auditor auditor) {
		
		UserEvent userEvent = new UserEvent(user, auditor.getAudit());
		
		try {
			eventSubmitter.submit(userEvent, EventType.USER_UPDATE, executor);
		} catch (EventSubmitException e) {
			logger.warn("Could not submit event", e);
		}
	}
	
}
