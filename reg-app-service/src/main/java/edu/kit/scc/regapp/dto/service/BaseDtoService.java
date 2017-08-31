package edu.kit.scc.regapp.dto.service;

import java.io.Serializable;

import edu.kit.scc.regapp.dto.entity.BaseEntityDto;
import edu.kit.scc.regapp.entity.BaseEntity;

public interface BaseDtoService<T extends BaseEntity<PK>, E extends BaseEntityDto<PK>, PK extends Serializable> extends Serializable {

	E createNewDto();

	E findById(PK pk);
}
