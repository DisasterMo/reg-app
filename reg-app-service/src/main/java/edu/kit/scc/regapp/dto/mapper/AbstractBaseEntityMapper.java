package edu.kit.scc.regapp.dto.mapper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import edu.kit.scc.regapp.dto.entity.AbstractBaseEntityDto;
import edu.kit.scc.regapp.dto.entity.BaseEntityDto;
import edu.kit.scc.regapp.entity.AbstractBaseEntity;
import edu.kit.scc.regapp.entity.BaseEntity;

public abstract class AbstractBaseEntityMapper<T extends BaseEntity<PK>,E extends BaseEntityDto<PK>, PK extends Serializable>
		implements BaseEntityMapper<T, E, PK>, Serializable {

	private static final long serialVersionUID = 1L;

    protected abstract void copyAllProperties(T fromBaseEntity, E toDtoEntity); 

    public abstract Class<E> getEntityDtoClass();

	@Override
	public void copyProperties(T fromBaseEntity, E toDtoEntity) {
		toDtoEntity.setId(fromBaseEntity.getId());
		if ((fromBaseEntity instanceof AbstractBaseEntity) 
				&& (toDtoEntity instanceof AbstractBaseEntityDto)) {
			copy(fromBaseEntity, toDtoEntity, "createdAt", "updatedAt", "version");
		}
		
		copy(fromBaseEntity, toDtoEntity, getPropertiesToCopy());
		copyAllProperties(fromBaseEntity, toDtoEntity);
	}
	
	protected String[] getPropertiesToCopy() {
		return new String[]{};
	}
	
	protected void copy(T fromBaseEntity, E toDtoEntity, String... props) {
		for (String prop : props) {
			try {
				Object o = PropertyUtils.getProperty(fromBaseEntity, prop);
				PropertyUtils.setProperty(toDtoEntity, prop, o);
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			}
		}
	}
}
