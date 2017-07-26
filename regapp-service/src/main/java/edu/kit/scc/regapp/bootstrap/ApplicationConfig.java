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
package edu.kit.scc.regapp.bootstrap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;

import edu.kit.scc.webreg.dao.ApplicationConfigDao;
import edu.kit.scc.webreg.entity.ApplicationConfigEntity;

@Singleton
public class ApplicationConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String actualConfigFormatVersion = "3.0.0";
	
	@Inject
	private Logger logger;
	
	@Inject
	private ApplicationConfigDao dao;
	
	private ApplicationConfigEntity appConfig; 
	
	private Date lastLoad;
	
	private List<VersionConverter> converterList;
	
	public void init() {
		logger.debug("Checking for Active Configuration");
		appConfig = dao.findActive();
		
		if (appConfig == null) {
			logger.info("No active configuration found. Creating new config");
			appConfig = dao.createNew();
			appConfig.setConfigFormatVersion(actualConfigFormatVersion);
			appConfig.setSubVersion("1");
			appConfig.setActiveConfig(true);
			appConfig.setConfigOptions(new HashMap<String, String>());
			appConfig = dao.persist(appConfig);
		}
		else if (! actualConfigFormatVersion.equals(appConfig.getConfigFormatVersion())) {
			// Config format changed. Upgrade is needed
			logger.debug("Populating converterList");
			converterList = new ArrayList<>();
			converterList.add(new VersionConverter2__3());
			
			try {
				for (VersionConverter vc : converterList) {
					if (vc.fromVersion().equals(appConfig.getConfigFormatVersion()) 
							&& vc.toVersion().equals(actualConfigFormatVersion)) {
						logger.info("Found configuration upgrader from version {} to version {}", vc.fromVersion(), vc.toVersion());
						vc.convert(appConfig);
					}
				}
				
				appConfig = dao.persist(appConfig);
			} catch (ConversionException e) {
				logger.warn("Version conversion failed", e);
			}
		}
		
		lastLoad = new Date();
	}
	
	public boolean reload() {
		ApplicationConfigEntity newAppConfig = dao.findReloadActive(lastLoad);
		
		if (newAppConfig != null) {
			logger.info("Reloading Application Configuration");
			appConfig = newAppConfig;
			
			lastLoad = new Date();
			return true;
		}
		else {
			return false;
		}
	}
	
	public void scheduleReload() {
		appConfig.setDirtyStamp(new Date());
		appConfig = dao.persist(appConfig);
	}
	
	public String getConfigValue(String key) {
		return appConfig.getConfigOptions().get(key);
	}
	
	public String deleteConfigValue(String key) {
		String value = appConfig.getConfigOptions().remove(key);
		appConfig = dao.persist(appConfig);
		return value;
	}
	
	public void storeConfigValue(String key, String value) {
		appConfig.getConfigOptions().put(key, value);
		appConfig = dao.persist(appConfig);
	}
	
	public Map<String, String> getConfigOptions() {
		return appConfig.getConfigOptions();
	}

	public Date getLastLoad() {
		return lastLoad;
	}
	
	public Date getNextScheduledReload() {
		return appConfig.getDirtyStamp();
	}
}
