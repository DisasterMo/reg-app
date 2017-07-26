package edu.kit.scc.regapp.entity.audit;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;
import edu.kit.scc.regapp.entity.SamlSpConfigurationEntity;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AuditIdpCommunicationEntity.class)
public abstract class AuditIdpCommunicationEntity_ extends edu.kit.scc.regapp.entity.audit.AuditEntryEntity_ {

	public static volatile SingularAttribute<AuditIdpCommunicationEntity, SamlIdpMetadataEntity> idp;
	public static volatile SingularAttribute<AuditIdpCommunicationEntity, SamlSpConfigurationEntity> spConfig;

}

