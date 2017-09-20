package edu.kit.scc.regapp.dto.entity;

import java.util.Set;

public class ServiceEntityDto extends AbstractBaseEntityDto {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String shortName;
	
	private String description;
	
	private String shortDescription;

	private Long parentServiceId;
	
	private Set<PolicyActualAgreementEntityDto> policies;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Long getParentServiceId() {
		return parentServiceId;
	}

	public void setParentServiceId(Long parentServiceId) {
		this.parentServiceId = parentServiceId;
	}

	public Set<PolicyActualAgreementEntityDto> getPolicies() {
		return policies;
	}

	public void setPolicies(Set<PolicyActualAgreementEntityDto> policies) {
		this.policies = policies;
	}
}
