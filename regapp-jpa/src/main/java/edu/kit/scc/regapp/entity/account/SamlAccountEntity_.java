package edu.kit.scc.regapp.entity.account;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;

@StaticMetamodel(SamlAccountEntity.class)
public abstract class SamlAccountEntity_ extends edu.kit.scc.regapp.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<SamlAccountEntity, String> persistentId;
	public static volatile SingularAttribute<SamlAccountEntity, String> globalId;
	public static volatile SingularAttribute<SamlAccountEntity, SamlIdpMetadataEntity> idp;
	public static volatile SingularAttribute<SamlAccountEntity, SamlSpConfigurationEntity> sp;

}
