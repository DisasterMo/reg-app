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

import java.io.Serializable;
import java.util.Map;

public interface ExecutableJob extends Serializable {

	void setJobStore(Map<String, String> jobStore);
	void execute();
	
}
