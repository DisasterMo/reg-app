package edu.kit.scc.regapp.entity.as;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import edu.kit.scc.regapp.entity.UserEntity;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ASUserAttrEntity.class)
public abstract class ASUserAttrEntity_ extends edu.kit.scc.regapp.entity.AbstractBaseEntity_ {

	public static volatile SingularAttribute<ASUserAttrEntity, Date> lastSuccessfulQuery;
	public static volatile SingularAttribute<ASUserAttrEntity, AttributeSourceQueryStatus> queryStatus;
	public static volatile SingularAttribute<ASUserAttrEntity, AttributeSourceEntity> attributeSource;
	public static volatile SetAttribute<ASUserAttrEntity, ASUserAttrValueEntity> values;
	public static volatile SingularAttribute<ASUserAttrEntity, Date> lastQuery;
	public static volatile SingularAttribute<ASUserAttrEntity, String> queryMessage;
	public static volatile SingularAttribute<ASUserAttrEntity, UserEntity> user;

}

