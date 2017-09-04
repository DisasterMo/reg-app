package edu.kit.scc.regapp.entity.auth;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.kit.scc.regapp.entity.FederationEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;

@Entity(name = "SamlAuthMechEntity")
public class SamlAuthMechEntity extends AuthMechEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "sp_config_id")
	private SamlSpConfigurationEntity spConfig;

	@ManyToOne
    @JoinColumn(name = "federation_id")
	private FederationEntity federation;

	public SamlSpConfigurationEntity getSpConfig() {
		return spConfig;
	}

	public void setSpConfig(SamlSpConfigurationEntity spConfig) {
		this.spConfig = spConfig;
	}

	public FederationEntity getFederation() {
		return federation;
	}

	public void setFederation(FederationEntity federation) {
		this.federation = federation;
	}
}
