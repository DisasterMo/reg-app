/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.scc.webreg.rwth.jobs;

import edu.kit.scc.webreg.entity.SamlUserEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.UserStatus;
import edu.kit.scc.webreg.entity.identity.IdentityEntity;
import edu.kit.scc.webreg.exc.RegisterException;
import edu.kit.scc.webreg.exc.UserUpdateException;
import edu.kit.scc.webreg.job.AbstractExecutableJob;
import edu.kit.scc.webreg.service.UserDeleteService;
import edu.kit.scc.webreg.service.UserService;
import java.util.List;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for deleting non-active users.
 *
 * @author bum-admin
 */
public abstract class AbstractUpdateOrDeleteUserData extends AbstractExecutableJob {

    @Inject
    private UserDeleteService userDeleteService;

    /**
     * Attempts to update all users which are out of date by maxAbsencePeriod,
     * and deletes user data for those which fail to update.TODO not every
     * unsuccessful update "fails" (user status is set ON_HOLD, though); figure
     * out how to still get rid of those users
     *
     * @param targetStatus Process users with this status only.
     * @param maxAcceptablePeriod [ms] If a user has targetStatus for longer
     * than this, their user data is deleted. Set to -1 to prevent any and all
     * deletions.
     * @param updateAfter [ms] If a user was last updated longer than this ago,
     * attempt to update them before performing the deletion check.
     * @param limit This value determines how many registries are processed at
     * once.
     */
    public void executeUpdateOrDelete(UserStatus targetStatus, Long maxAcceptablePeriod, Long updateAfter, Integer limit) {

        String auditName = targetStatus + "-rem-job";
        Logger logger = LoggerFactory.getLogger(AbstractUpdateOrDeleteUserData.class);

        try {
            InitialContext ic = new InitialContext();
            UserService userService = (UserService) ic.lookup("global/bwreg/bwreg-service/UserServiceImpl!edu.kit.scc.webreg.service.UserService");

            List<UserEntity> userList;
            logger.info("Starting update of {} users", targetStatus);
            userList = userService.findByStatus(targetStatus);

            for (UserEntity user : userList) {
                try {
                    logger.debug("Updating user {}", user.getEppn());

                    if (user instanceof SamlUserEntity) {
                        SamlUserEntity samlUser = (SamlUserEntity) user;
                        IdentityEntity identity = samlUser.getIdentity();

                        Long sinceLastChange = System.currentTimeMillis() - samlUser.getLastStatusChange().getTime();
                        Long sinceLastUpdate = System.currentTimeMillis() - samlUser.getLastUpdate().getTime();
                        if (sinceLastUpdate > updateAfter) {
                            logger.info("User {} lastUpdate is older than {}ms, trying to update", user.getEppn(), updateAfter);
                            try {
                                userService.updateUserFromIdp(samlUser, auditName);
                                logger.info("Update performed");
                            } catch (UserUpdateException e) {
                                logger.info("Exception while querying IDP: {}\nUpdate fails since {}ms ago", e.getMessage(), sinceLastUpdate);
                                if (e.getCause() != null) {
                                    logger.info("Cause is: {}", e.getCause().getMessage());
                                    if (e.getCause().getCause() != null) {
                                        logger.info("Inner cause is: {}", e.getCause().getCause().getMessage());
                                    }
                                }
                                // resume without deregistering user
                                throw new RegisterException("IDP failed");
                            }
                        }

                        if (0 < maxAcceptablePeriod && sinceLastChange > maxAcceptablePeriod && targetStatus.equals(samlUser.getUserStatus())) {
                            userDeleteService.deleteUserData(identity, "identity-" + identity.getId());
                            logger.info("User {} with status {} deleted after {}ms", samlUser.getEppn(), targetStatus, sinceLastChange);
                        }
                    }
                } catch (RegisterException e) {
                    logger.info("Could not update, postponing deletion", e);
                }
            }
        } catch (NamingException e) {
            logger.warn("Could not update users: {}", e);
        }
    }
}
