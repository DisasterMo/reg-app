package edu.kit.scc.regapp.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RoleEntity.class)
public abstract class RoleEntity_ extends edu.kit.scc.regapp.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<RoleEntity, String> name;
	public static volatile SetAttribute<RoleEntity, UserRoleEntity> users;

}

