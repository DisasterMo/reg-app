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
package edu.kit.scc.webreg.bean.admin.brule;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.kit.scc.webreg.entity.BusinessRulePackageEntity;
import edu.kit.scc.webreg.service.BusinessRulePackageService;
import edu.kit.scc.webreg.service.BusinessRuleService;

@Named("listBusinessRuleBean")
@RequestScoped
public class ListBusinessRuleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<BusinessRulePackageEntity> list;
    
	private String regex;
	private String replace;
	
    @Inject
    private BusinessRulePackageService service;

    @Inject
    private BusinessRuleService businessRuleService;

    @PostConstruct
    public void init() {
		list = service.findAllWithRules();
	}
	
    public List<BusinessRulePackageEntity> getList() {
   		return list;
    }

    public void regexReplace() {
    	businessRuleService.replaceRegex(regex, replace);
    }

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getReplace() {
		return replace;
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}
}
