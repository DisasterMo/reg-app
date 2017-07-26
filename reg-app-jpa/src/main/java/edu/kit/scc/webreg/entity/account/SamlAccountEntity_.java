package edu.kit.scc.webreg.entity.account;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import edu.kit.scc.webreg.entity.SamlIdpMetadataEntity;
import edu.kit.scc.webreg.entity.SamlSpConfigurationEntity;

@StaticMetamodel(SamlAccountEntity.class)
public abstract class SamlAccountEntity_ extends edu.kit.scc.webreg.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<SamlAccountEntity, String> persistentId;
	public static volatile SingularAttribute<SamlAccountEntity, String> globalId;
	public static volatile SingularAttribute<SamlAccountEntity, SamlIdpMetadataEntity> idp;
	public static volatile SingularAttribute<SamlAccountEntity, SamlSpConfigurationEntity> sp;

}
