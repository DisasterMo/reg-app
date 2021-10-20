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
package edu.kit.scc.webreg.converter;

import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.oidc.OidcRpConfigurationEntity;
import edu.kit.scc.webreg.service.BaseService;
import edu.kit.scc.webreg.service.oidc.OidcRpConfigurationService;

@Named("oidcRpConfigurationConverter")
public class OidcRpConfigurationConverter extends AbstractConverter<OidcRpConfigurationEntity> {

	private static final long serialVersionUID = 1L;

	@Inject
	private OidcRpConfigurationService service;

	@Override
	protected BaseService<OidcRpConfigurationEntity> getService() {
		return service;
	}
	
}
