package edu.kit.scc.webreg.entity.account;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import edu.kit.scc.webreg.entity.UserEntity;

@StaticMetamodel(AccountEntity.class)
public abstract class AccountEntity_ extends edu.kit.scc.webreg.entity.AbstractBaseEntity_ {

	public static volatile MapAttribute<AccountEntity, String, String> accountStore;
	public static volatile SingularAttribute<AccountEntity, String> globalId;
	public static volatile SingularAttribute<AccountEntity, UserEntity> user;

}
