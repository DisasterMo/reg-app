/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.scc.webreg.rwth.jobs;

import edu.kit.scc.webreg.entity.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job class for updating users with an ACTIVE status and/or deleting users
 * which cannot be updated.
 *
 * Required Parameters:
 *
 * max_acceptable_period_millis: Users which fail to update * for this long (in
 * milliseconds) are deleted. Set to -1 to prevent deletion.
 *
 * target_user_status: Only users with this status will be processed. Possible
 * values: blocked, deregistered, on_hold.
 *
 * Optional Parameters:
 *
 * registries_per_exec: This value determines how many registries are processed
 * at once. This can reduce load on the database and prevents too long running
 * database transaction, if a low value is chosen. Ie. only 1 registry (default
 * value), but every minute.
 *
 * last_user_update_millis: If the last user update is older than this value in
 * milliseconds (default: 1 day = 86400000), an update is attempted. The
 * deletion check only occurs, if this update fails.
 *
 * @author bum-admin
 */
public class UpdateOrDeleteOutOfDateUsers extends AbstractUpdateOrDeleteUserData {

    @Override
    public void execute() {
        Logger logger = LoggerFactory.getLogger(UpdateOrDeleteOutOfDateUsers.class);

        if (!getJobStore().containsKey("max_acceptable_period_millis")) {
            logger.warn("UpdateOrDeleteUserDataJob is not configured correctly. max_absence_period_millis parameter is missing in JobMap");
            return;
        }
        Long maxAbsencePeriod = Long.parseLong(getJobStore().get("max_absence_period_millis"));

        Long updateAfter = 1 * 24 * 60 * 60 * 1000L; // 1 day
        if (getJobStore().containsKey("last_user_update_millis")) {
            updateAfter = Long.parseLong(getJobStore().get("last_user_update_millis"));
        }

        Integer limit = 1;
        if (getJobStore().containsKey("registries_per_exec")) {
            limit = Integer.parseInt(getJobStore().get("registries_per_exec"));
        }

        switch (getJobStore().getOrDefault("target_user_status", null)) {
//            case "ACTIVE":
//            case "active":
//                executeUpdateOrDelete(UserStatus.ACTIVE, maxAbsencePeriod, updateAfter, limit);
//                break;
            case "BLOCKED":
            case "blocked":
                executeUpdateOrDelete(UserStatus.BLOCKED, maxAbsencePeriod, updateAfter, limit);
                break;
            case "DEREGISTERED":
            case "deregistered":
                executeUpdateOrDelete(UserStatus.DEREGISTERED, maxAbsencePeriod, updateAfter, limit);
                break;
            case "ON_HOLD":
            case "on_hold":
                executeUpdateOrDelete(UserStatus.ON_HOLD, maxAbsencePeriod, updateAfter, limit);
                break;
            default:
                logger.warn("UpdateOrDeleteUserDataJob is not configured correctly. target_user_status parameter in JobMap must be one of: blocked, deregistered or on_hold");
        }
    }
}
