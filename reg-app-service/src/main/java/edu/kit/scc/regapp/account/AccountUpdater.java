package edu.kit.scc.regapp.account;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.dao.audit.AuditDetailDao;
import edu.kit.scc.regapp.dao.audit.AuditEntryDao;
import edu.kit.scc.regapp.entity.EventType;
import edu.kit.scc.regapp.entity.account.AccountEntity;
import edu.kit.scc.regapp.entity.account.AccountStatus;
import edu.kit.scc.regapp.entity.audit.AuditStatus;
import edu.kit.scc.regapp.event.AccountEvent;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.exc.EventSubmitException;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;
import edu.kit.scc.regapp.user.UserUpdater;

public abstract class AccountUpdater<T extends AccountEntity> {
	
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
	
	@Inject
	protected AttributeMapHelper mapHelper;
	
	@Inject
	private UserUpdater userUpdater;
	
	protected abstract Boolean updateAccount(T account, Map<String, List<Object>> attributeMap, AccountUpdateAuditor auditor)
			throws UserUpdateException;
	
	public void update(T account, Map<String, List<Object>> attributeMap, String executor)
			throws UserUpdateException {
        logger.debug("Updating account {}", account.getGlobalId());

        AccountUpdateAuditor auditor = new AccountUpdateAuditor(auditDao, auditDetailDao, appConfig);
        auditor.startAuditTrail(executor);
        auditor.setName(getClass().getName() + "-UserUpdate-Audit");
        auditor.setDetail("Update account " + account.getGlobalId());
        auditor.setAccount(account);
        
        boolean changed = false;
        
        /**
         * put no_assertion_count in generic store if assertion is missing. Else
         * reset no assertion count and put last valid assertion date in
         */
        if (attributeMap == null) {
                if (account.getNoDataCount() == null)
                	account.setNoDataCount(0);
                
                account.setNoDataCount(account.getNoDataCount() + 1);
                
                logger.info("No attributes for account {}, setting status ON_HOLD and clearing attributes", account.getGlobalId());
                
                account.getAccountStore().clear();

                if (AccountStatus.ACTIVE.equals(account.getAccountStatus())) {
                        changeAccountStatus(account, AccountStatus.ON_HOLD, auditor);
                }
        }
        else {
        	account.setNoDataCount(0);
        	account.setLastValidData(new Date());

            if (AccountStatus.ON_HOLD.equals(account.getAccountStatus())) {
                    changeAccountStatus(account, AccountStatus.ACTIVE, auditor);

                    /*
                     * fire a user changed event to be sure, when the user is activated
                     */
                    changed = true;
            }

            Map<String, String> accountStore = account.getAccountStore();
            accountStore.clear();
            for (Entry<String, List<Object>> entry : attributeMap.entrySet()) {
            	accountStore.put(entry.getKey(), mapHelper.attributeListToString(entry.getValue()));
            }

            changed |= updateAccount(account, attributeMap, auditor);

        } 
        account.setLastUpdate(new Date());
        account.setLastFailedUpdate(null);

        userUpdater.updateUser(account, auditor, null);
        
        if (changed) {
        	fireAccountChangeEvent(account, auditor.getActualExecutor(), auditor);
        }

        auditor.finishAuditTrail();
        auditor.commitAuditTrail();

	}
	
    private void changeAccountStatus(T account, AccountStatus toStatus, Auditor auditor) {
        AccountStatus fromStatus = account.getAccountStatus();
        account.setAccountStatus(toStatus);
        account.setLastStatusChange(new Date());

        logger.debug("{}: change user status from {} to {}", account.getGlobalId(), fromStatus, toStatus);
        auditor.logAction(account.getGlobalId(), "CHANGE STATUS", fromStatus + " -> " + toStatus,
                        "Change status " + fromStatus + " -> " + toStatus, AuditStatus.SUCCESS);
    }
    
    private void fireAccountChangeEvent(T account, String executor, Auditor auditor) {

        AccountEvent event = new AccountEvent(account, auditor.getAudit());

        try {
                eventSubmitter.submit(event, EventType.ACCOUNT_UPDATE, executor);
        } catch (EventSubmitException e) {
                logger.warn("Could not submit event", e);
        }
    }
}
