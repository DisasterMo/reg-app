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
package edu.kit.scc.syncshare.reg;

import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.exc.RegisterException;

public interface PFAccountService {

	public PFAccount findById(String id, ServiceEntity serviceEntity) throws RegisterException;
	public PFAccount findByUsername(String username, ServiceEntity serviceEntity) throws RegisterException;
	PFAccount update(PFAccount account, ServiceEntity serviceEntity)
			throws RegisterException;
	
}
