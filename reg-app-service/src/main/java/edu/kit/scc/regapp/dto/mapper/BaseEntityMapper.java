package edu.kit.scc.regapp.dto.mapper;

import java.io.Serializable;

import edu.kit.scc.regapp.dto.entity.BaseEntityDto;
import edu.kit.scc.regapp.entity.BaseEntity;

public interface BaseEntityMapper<T extends BaseEntity<PK>, E extends BaseEntityDto<PK>, PK extends Serializable> {

	void copyProperties(T fromBaseEntity, E toDtoEntity);
	Class<E> getEntityDtoClass();
}
