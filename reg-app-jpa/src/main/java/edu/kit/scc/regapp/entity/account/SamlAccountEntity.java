package edu.kit.scc.regapp.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;

@Entity(name = "SamlAccountEntity")
public class SamlAccountEntity extends AccountEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = SamlIdpMetadataEntity.class)
	private SamlIdpMetadataEntity idp;

	@ManyToOne(targetEntity = SamlSpConfigurationEntity.class)
	private SamlSpConfigurationEntity sp;

	@Column(name = "persistent_id", length = 1024)
	private String persistentId;
		
	public SamlIdpMetadataEntity getIdp() {
		return idp;
	}

	public void setIdp(SamlIdpMetadataEntity idp) {
		this.idp = idp;
	}

	public String getPersistentId() {
		return persistentId;
	}

	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}

	public SamlSpConfigurationEntity getSp() {
		return sp;
	}

	public void setSp(SamlSpConfigurationEntity sp) {
		this.sp = sp;
	}

	
}
