package edu.kit.scc.regapp.dto.entity;

public class PolicyActualAgreementEntityDto extends AbstractBaseEntityDto {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String agreementText;
	
	private String agreementName;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAgreementText() {
		return agreementText;
	}

	public void setAgreementText(String agreementText) {
		this.agreementText = agreementText;
	}

	public String getAgreementName() {
		return agreementName;
	}

	public void setAgreementName(String agreementName) {
		this.agreementName = agreementName;
	}
}
