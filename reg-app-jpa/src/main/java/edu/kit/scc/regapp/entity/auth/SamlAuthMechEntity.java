package edu.kit.scc.regapp.entity.auth;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;

@Entity(name = "SamlAuthMechEntity")
public class SamlAuthMechEntity extends AuthMechEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "sp_config_id", nullable = false)
	private SamlSpConfigurationEntity spConfig;

	public SamlSpConfigurationEntity getSpConfig() {
		return spConfig;
	}

	public void setSpConfig(SamlSpConfigurationEntity spConfig) {
		this.spConfig = spConfig;
	}
}
