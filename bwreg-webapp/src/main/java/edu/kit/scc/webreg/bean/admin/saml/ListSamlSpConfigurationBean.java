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

import edu.kit.scc.webreg.entity.SamlSpConfigurationEntity;
import edu.kit.scc.webreg.service.SamlSpConfigurationService;

@Named("listSamlSpConfigurationBean")
@RequestScoped
public class ListSamlSpConfigurationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SamlSpConfigurationEntity> list;
    
    @Inject
    private SamlSpConfigurationService service;

    @PostConstruct
    public void init() {
		list = service.findAll();
	}
	
    public List<SamlSpConfigurationEntity> getEntityList() {
   		return list;
    }

}
