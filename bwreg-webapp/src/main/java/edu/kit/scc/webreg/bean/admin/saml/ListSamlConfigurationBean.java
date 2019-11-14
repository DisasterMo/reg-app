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
package edu.kit.scc.webreg.bean.admin.saml;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.SamlAAConfigurationEntity;
import edu.kit.scc.webreg.entity.SamlIdpConfigurationEntity;
import edu.kit.scc.webreg.entity.SamlSpConfigurationEntity;
import edu.kit.scc.webreg.service.SamlAAConfigurationService;
import edu.kit.scc.webreg.service.SamlIdpConfigurationService;
import edu.kit.scc.webreg.service.SamlSpConfigurationService;

@Named("listSamlConfigurationBean")
@RequestScoped
public class ListSamlConfigurationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SamlSpConfigurationEntity> spList;
	private List<SamlAAConfigurationEntity> aaList;
	private List<SamlIdpConfigurationEntity> idpList;
    
    @Inject
    private SamlSpConfigurationService spService;

    @Inject
    private SamlAAConfigurationService aaService;

    @Inject
    private SamlIdpConfigurationService idpService;

    @PostConstruct
    public void init() {
		spList = spService.findAll();
		aaList = aaService.findAll();
		idpList = idpService.findAll();
	}

	public List<SamlSpConfigurationEntity> getSpList() {
		return spList;
	}

	public List<SamlAAConfigurationEntity> getAaList() {
		return aaList;
	}

	public List<SamlIdpConfigurationEntity> getIdpList() {
		return idpList;
	}

}
