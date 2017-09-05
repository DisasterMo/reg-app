package edu.kit.scc.regapp.entity.account;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(LocalAccountEntity.class)
public abstract class LocalAccountEntity_ extends edu.kit.scc.regapp.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<LocalAccountEntity, String> localId;
	public static volatile SingularAttribute<LocalAccountEntity, String> password;
}
