/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.scc.webreg.rwth;

import edu.kit.scc.webreg.entity.SamlUserEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.UserStatus;
import edu.kit.scc.webreg.entity.identity.IdentityEntity;
import edu.kit.scc.webreg.exc.UserUpdateException;
import edu.kit.scc.webreg.job.AbstractExecutableJob;
import edu.kit.scc.webreg.model.GenericLazyDataModelImpl;
import edu.kit.scc.webreg.service.UserDeleteService;
import edu.kit.scc.webreg.service.UserService;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for updating out of date SAML users and deleting user data for
 * no longer present/reachable/updateable users.
 *
 * @author bum-admin
 */
public abstract class AbstractUpdateOrDeleteUserData extends AbstractExecutableJob {

    @Inject
    private UserDeleteService userDeleteService;

    /**
     * Attempts to update all users which are out of date by attemptUpdateAfter,
     * while deleting user data for those which fail to update for longer than
     * maxAbsencePeriod.
     *
     * @param userStatus Limits the evaluated users to those having this status.
     * @param maxAbsencePeriod [ms] If a user update fails for longer than this,
     * their user data is deleted. Set to -1 to prevent any and all deletions.
     * @param attemptUpdateAfter [ms] If the last successful update was longer
     * ago than this, attempt to update them.
     */
    public void executeUpdateOrDelete(UserStatus userStatus, Long maxAbsencePeriod, Long attemptUpdateAfter) {
        String auditName = userStatus.toString() + "-rem-job";
        Logger logger = LoggerFactory.getLogger(AbstractUpdateOrDeleteUserData.class);

        logger.info("Starting Update of {} Registries", userStatus);

        try {
            InitialContext ic = new InitialContext();

            UserService userService = (UserService) ic.lookup("global/bwreg/bwreg-service/UserServiceImpl!edu.kit.scc.webreg.service.UserService");

            LazyDataModel<UserEntity> allUserList = new GenericLazyDataModelImpl<>(userService);

            for (UserEntity user : allUserList) {
                // could be a long list, only print to debug level log
                logger.debug("Updating User {}", user.getEppn());

                if (user instanceof SamlUserEntity) {

                    SamlUserEntity samlUser = (SamlUserEntity) user;
                    IdentityEntity identity = samlUser.getIdentity();

                    Long absenceTime = System.currentTimeMillis() - user.getLastUpdate().getTime();
                    if (absenceTime > attemptUpdateAfter) {
                        logger.info("User {} lastUpdate is older than {}ms. Trying update", user.getEppn(), attemptUpdateAfter);
                        try {
                            userService.updateUserFromIdp(samlUser, auditName);
                        } catch (UserUpdateException e) {
                            logger.info("Exception while Querying IDP: {}\nUpdate fails since {}ms", e.getMessage(), absenceTime);
                            if (e.getCause() != null) {
                                logger.info("Cause is: {}", e.getCause().getMessage());
                                if (e.getCause().getCause() != null) {
                                    logger.info("Inner Cause is: {}", e.getCause().getCause().getMessage());
                                }
                            }

                            if (0 <= maxAbsencePeriod && absenceTime > maxAbsencePeriod) {
                                logger.warn("Unable to update user {} for over {}ms. User is considered to be deleted. Removing all personal data...", user.getEppn(), maxAbsencePeriod);
                                userDeleteService.deleteUserData(identity, "identity-" + identity.getId());
                            }
                        }
                    }
                }
            }
        } catch (NamingException e) {
            logger.warn("Could not Update Registries: {}", e);
        }
    }
}
