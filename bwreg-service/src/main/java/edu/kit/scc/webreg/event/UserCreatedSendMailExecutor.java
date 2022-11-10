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
package edu.kit.scc.webreg.event;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.regapp.mail.api.TemplateMailService;
import edu.kit.scc.webreg.entity.UserEntity;

public class UserCreatedSendMailExecutor extends
		AbstractEventExecutor<UserEvent, UserEntity> {

	private static final long serialVersionUID = 1L;

	public UserCreatedSendMailExecutor() {
		super();
	}

	@Override
	public void execute() {
		Logger logger = LoggerFactory.getLogger(UserCreatedSendMailExecutor.class);
		logger.debug("Executing");
		
		String templateName = getJobStore().get("mail_template");

		if (templateName == null) {
			logger.warn("No template configured for UserCreatedSendMailExecutor");
			return;
		}
		
		try {
			InitialContext ic = new InitialContext();
			
			TemplateMailService templateMailService = (TemplateMailService) ic.lookup("global/bwreg/bwreg-service/TemplateMailServiceImpl!edu.kit.scc.regapp.mail.api.TemplateMailService");
			
			UserEntity user = getEvent().getEntity();
			Map<String, Object> context = new HashMap<String, Object>(1);
			context.put("user", user);
			
			templateMailService.sendMail(templateName, context, true);
			
		} catch (NamingException e) {
			logger.warn("Could not send email: {}", e);
		}
		
	}

}
