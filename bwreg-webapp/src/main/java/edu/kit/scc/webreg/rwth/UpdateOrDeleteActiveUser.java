/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.scc.webreg.rwth;

import edu.kit.scc.webreg.entity.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job class for updating users with an ACTIVE status and/or deleting users
 * which cannot be updated.
 *
 * Required Parameters: max_absence_period_millis: Users which fail to update
 * for this long (in milliseconds) are deleted. Set to -1 to prevent deletion.
 *
 * Optional Parameters: last_user_update_millis: If the user's last update is
 * older than this value in milliseconds, an update is attempted. The deletion
 * check only occurs if this update fails. Default is 1 day (86400000).
 *
 * @author bum-admin
 */
public class UpdateOrDeleteActiveUser extends AbstractUpdateOrDeleteUserData {

    @Override
    public void execute() {
        Logger logger = LoggerFactory.getLogger(UpdateOrDeleteActiveUser.class);

        if (!getJobStore().containsKey("max_absence_period_millis")) {
            logger.warn("UpdateOrDeleteUserDataJob is not configured correctly. max_absence_period_millis Parameter is missing in JobMap");
            return;
        }

        Long maxAbsencePeriod = Long.parseLong(getJobStore().get("max_absence_period_millis"));

        Long attemptUpdateAfter = 1 * 24 * 60 * 60 * 1000L; // 1 day standard value
        if (getJobStore().containsKey("last_user_update_millis")) {
            attemptUpdateAfter = Long.parseLong(getJobStore().get("last_user_update_millis"));
        }

//        UserStatus targetStatus = UserStatus.ACTIVE;
//        if (getJobStore().containsKey("last_user_update_millis")) {
//            attemptUpdateAfter = Long.parseLong(getJobStore().get("last_user_update_millis"));
//        }

        executeUpdateOrDelete(UserStatus.ACTIVE, maxAbsencePeriod, attemptUpdateAfter);
    }
}
