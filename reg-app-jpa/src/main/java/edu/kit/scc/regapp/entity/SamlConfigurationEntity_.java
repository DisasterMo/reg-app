package edu.kit.scc.regapp.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SamlConfigurationEntity.class)
public abstract class SamlConfigurationEntity_ extends edu.kit.scc.regapp.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<SamlConfigurationEntity, String> privateKey;
	public static volatile SingularAttribute<SamlConfigurationEntity, String> standbyCertificate;
	public static volatile SingularAttribute<SamlConfigurationEntity, String> standbyPrivateKey;
	public static volatile SingularAttribute<SamlConfigurationEntity, String> certificate;
	public static volatile SingularAttribute<SamlConfigurationEntity, String> entityId;
	public static volatile SingularAttribute<SamlConfigurationEntity, SamlMetadataEntityStatus> status;

}

