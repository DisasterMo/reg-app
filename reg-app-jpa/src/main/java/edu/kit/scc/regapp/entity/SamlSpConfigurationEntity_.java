package edu.kit.scc.regapp.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SamlSpConfigurationEntity.class)
public abstract class SamlSpConfigurationEntity_ extends edu.kit.scc.regapp.entity.SamlConfigurationEntity_ {

	public static volatile SingularAttribute<SamlSpConfigurationEntity, String> acs;
	public static volatile ListAttribute<SamlSpConfigurationEntity, String> hostNameList;
	public static volatile SingularAttribute<SamlSpConfigurationEntity, String> ecp;

}

