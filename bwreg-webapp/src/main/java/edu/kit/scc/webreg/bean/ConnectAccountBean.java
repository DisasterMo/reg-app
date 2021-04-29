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
package edu.kit.scc.webreg.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;

import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.entity.identity.IdentityEntity;
import edu.kit.scc.webreg.entity.oidc.OidcRpConfigurationEntity;
import edu.kit.scc.webreg.service.UserService;
import edu.kit.scc.webreg.service.identity.IdentityService;
import edu.kit.scc.webreg.service.oidc.OidcRpConfigurationService;
import edu.kit.scc.webreg.session.SessionManager;
import edu.kit.scc.webreg.util.FacesMessageGenerator;

@Named
@ViewScoped
public class ConnectAccountBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserService userService;
    
	@Inject
	private IdentityService identityService;

    @Inject 
    private SessionManager sessionManager;

	@Inject
	private OidcRpConfigurationService oidcRpService;

	@Inject
	private FacesMessageGenerator messageGenerator;

	private IdentityEntity identity;
	private List<UserEntity> userList;
	
	private List<OidcRpConfigurationEntity> oidcRpList;
	private OidcRpConfigurationEntity selectedOidcRp;

	private String pin;
	
	public void preRenderView(ComponentSystemEvent ev) {
		if (identity == null) {
			identity = identityService.findById(sessionManager.getIdentityId());
			userList = userService.findByIdentity(identity);
		}
	}
	
	public void startConnect() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

		if (selectedOidcRp != null && pin != null && pin.matches("^[a-zA-Z0-9]{4,32}$")) {
			sessionManager.setOidcRelyingPartyId(selectedOidcRp.getId());
			sessionManager.setAccountLinkingPin(pin);
			try {
				externalContext.redirect("/rpoidc/login");
			} catch (IOException e) {
				messageGenerator.addErrorMessage("Ein Fehler ist aufgetreten", 
								e.toString());
			}
		}
		else {
				messageGenerator.addWarningMessage("Keine Auswahl getroffen", 
							"Bitte wählen Sie Ihre Heimatorganisation");
		}

	}

	public IdentityEntity getIdentity() {
		return identity;
	}

	public List<UserEntity> getUserList() {
		return userList;
	}

	public List<OidcRpConfigurationEntity> getOidcRpList() {
		if (oidcRpList == null) {
			oidcRpList = oidcRpService.findAll();
		}
		return oidcRpList;
	}

	public OidcRpConfigurationEntity getSelectedOidcRp() {
		return selectedOidcRp;
	}

	public void setSelectedOidcRp(OidcRpConfigurationEntity selectedOidcRp) {
		this.selectedOidcRp = selectedOidcRp;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
