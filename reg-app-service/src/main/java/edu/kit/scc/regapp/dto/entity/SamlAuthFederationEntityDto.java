package edu.kit.scc.regapp.dto.entity;

import java.util.List;

public class SamlAuthFederationEntityDto extends AbstractBaseEntityDto {

	private static final long serialVersionUID = 1L;

	private String entityId;
	private String name;
	
	private List<SamlAuthIdpEntityDto> idpList;
	
	public String getEntityId() {
		return entityId;
	}
	
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SamlAuthIdpEntityDto> getIdpList() {
		return idpList;
	}

	public void setIdpList(List<SamlAuthIdpEntityDto> idpList) {
		this.idpList = idpList;
	}
	
}
