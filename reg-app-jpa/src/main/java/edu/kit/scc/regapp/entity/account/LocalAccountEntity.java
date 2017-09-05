package edu.kit.scc.regapp.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "LocalAccountEntity")
public class LocalAccountEntity extends AccountEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "local_id", length = 64)
	private String localId;

	@Column(name = "password", length = 512)
	private String password;

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
