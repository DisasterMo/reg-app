package edu.kit.scc.regapp.rest.auth;

import java.io.Serializable;

public class AuthInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean loggedIn;

	public Boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

}
