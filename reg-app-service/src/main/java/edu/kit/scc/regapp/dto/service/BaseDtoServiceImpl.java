package edu.kit.scc.regapp.dto.service;

import java.io.Serializable;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dto.entity.BaseEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.entity.BaseEntity;

public abstract class BaseDtoServiceImpl<T extends BaseEntity<PK>, E extends BaseEntityDto<PK>, PK extends Serializable> 
	implements BaseDtoService<T, E, PK> {

	private static final long serialVersionUID = 1L;

	protected abstract BaseEntityMapper<T, E, PK> getMapper();
	protected abstract BaseDao<T, PK> getDao();

	@Override
	public E createNewDto() {
		try {
			return getMapper().getEntityDtoClass().newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	@Override
	public E findById(PK pk) {
		E dto = createNewDto();
		getMapper().copyProperties(getDao().findById(pk), dto);
		return dto;
	}
}
