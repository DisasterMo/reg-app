package edu.kit.scc.regapp.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import edu.kit.scc.regapp.entity.as.AttributeSourceServiceEntity;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ServiceEntity.class)
public abstract class ServiceEntity_ extends edu.kit.scc.regapp.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<ServiceEntity, BusinessRulePackageEntity> mandatoryValueRulePackage;
	public static volatile SingularAttribute<ServiceEntity, ImageEntity> image;
	public static volatile SingularAttribute<ServiceEntity, BusinessRulePackageEntity> groupFilterRulePackage;
	public static volatile SingularAttribute<ServiceEntity, Boolean> hidden;
	public static volatile SingularAttribute<ServiceEntity, GroupAdminRoleEntity> groupAdminRole;
	public static volatile SetAttribute<ServiceEntity, PolicyEntity> policies;
	public static volatile SingularAttribute<ServiceEntity, AdminRoleEntity> adminRole;
	public static volatile SingularAttribute<ServiceEntity, String> description;
	public static volatile SingularAttribute<ServiceEntity, ServiceEntity> parentService;
	public static volatile SingularAttribute<ServiceEntity, String> shortDescription;
	public static volatile SingularAttribute<ServiceEntity, Boolean> published;
	public static volatile SingularAttribute<ServiceEntity, AdminRoleEntity> hotlineRole;
	public static volatile SingularAttribute<ServiceEntity, BusinessRuleEntity> accessRule;
	public static volatile SetAttribute<ServiceEntity, AttributeSourceServiceEntity> attributeSourceService;
	public static volatile SingularAttribute<ServiceEntity, Boolean> passwordCapable;
	public static volatile SingularAttribute<ServiceEntity, String> name;
	public static volatile SingularAttribute<ServiceEntity, ApproverRoleEntity> approverRole;
	public static volatile MapAttribute<ServiceEntity, String, String> serviceProps;
	public static volatile SingularAttribute<ServiceEntity, String> shortName;
	public static volatile SingularAttribute<ServiceEntity, String> registerBean;
	public static volatile SingularAttribute<ServiceEntity, Boolean> groupCapable;

}

