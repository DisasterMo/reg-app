package edu.kit.scc.regapp.dto.entity;

public class AuthMechEntityDto extends AbstractBaseEntityDto {

	private static final long serialVersionUID = 1L;

	private String name;
	private String displayName;
	private String type;
	
	private Long federationId;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getFederationId() {
		return federationId;
	}

	public void setFederationId(Long federationId) {
		this.federationId = federationId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
