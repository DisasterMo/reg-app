/*******************************************************************************
 * Copyright (c) 2014 Michael Simon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Michael Simon - initial
 ******************************************************************************/
package edu.kit.scc.regapp.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.regapp.entity.RegistryStatus;

public class DeregisterOnHoldRegistries extends AbstractDeregisterRegistries {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute() {
		Logger logger = LoggerFactory.getLogger(DeregisterOnHoldRegistries.class);
		
		if (! getJobStore().containsKey("on_hold_since_millis")) {
			logger.warn("DeregisterOnHoldRegistries Job is not configured correctly. on_hold_since_millis Parameter is missing in JobMap");
			return;
		}
	
		Long lastUpdate = Long.parseLong(getJobStore().get("on_hold_since_millis"));
		
		Long lastUserUpdate =  14 * 24 * 60 * 60 * 1000L; // 14 days standard value
		if (getJobStore().containsKey("last_user_update_millis")) 
			lastUserUpdate = Long.parseLong(getJobStore().get("last_user_update_millis"));
		
		executeDeregister(RegistryStatus.LOST_ACCESS, lastUpdate, lastUserUpdate);
	}
}
