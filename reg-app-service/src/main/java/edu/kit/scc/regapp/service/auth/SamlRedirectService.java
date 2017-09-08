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
package edu.kit.scc.regapp.service.auth;

import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.encoder.MessageEncodingException;

import edu.kit.scc.regapp.exc.LoginFailedException;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public interface SamlRedirectService {

	void redirectClient(Long authMechId, Long idpId, HttpServletResponse response) 
					throws MessageEncodingException, ComponentInitializationException, LoginFailedException;
}
