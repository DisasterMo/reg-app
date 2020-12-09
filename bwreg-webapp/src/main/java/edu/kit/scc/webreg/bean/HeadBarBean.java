package edu.kit.scc.webreg.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import edu.kit.scc.webreg.bootstrap.ApplicationConfig;

@Named("headBarBean")
@RequestScoped
public class HeadBarBean {

	@Inject
	private ApplicationConfig appConfig;

	@Inject 
	private HttpServletRequest request;
	
	public ApplicationConfig getAppConfig() {
		return appConfig;
	}
	
	public String getStylesheet() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_css", "");
	}

	public String getStylesheetExtended() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_css_ext", "/resources/css/std-ext.css");
	}

	public String getOverrideStdStylesheet() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "override_std_css", "/resources/css/std.css");
	}
	
	public String getHeaderImage() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_header_icon", "/resources/img/logo.svg");
	}
	
	public String getHeaderLink() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_header_link", "https://");
	}
	
	public String getHeaderTitle() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_header_title", "FeLS - Federated Login Service");
	}
	
	public String getAppTitle() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_app_title", "FeLS - Federated Login Service");
	}
	
	public String getAppImage() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_app_image", "/resources/img/regapp.jpg");
	}
	
	public String getAppLink() {
		return getApplicationBase() + getOrDefault(request.getServerName() + "_app_link", "https://");
	}
	
	public String getApplicationBase() {
		return getOrDefault(request.getServerName() + "_app_base", "");
	}
	
	private String getOrDefault(String key, String defaultString) {
		if (appConfig.getConfigValue(key) != null) {
			return appConfig.getConfigValue(key);
		}
		else {
			return defaultString;
		}
		
	}
}