package edu.kit.scc.regapp.dto.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.ServiceEntityShortMapper;
import edu.kit.scc.regapp.entity.ServiceEntity;

@Stateless
public class ServiceShortDtoServiceImpl extends BaseDtoServiceImpl<ServiceEntity, ServiceEntityDto, Long> implements ServiceShortDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private ServiceEntityShortMapper mapper;
	
	@Inject
	private ServiceDao dao;

	@Override
	protected BaseEntityMapper<ServiceEntity, ServiceEntityDto, Long> getMapper() {
		return mapper;
	}

	@Override
	protected BaseDao<ServiceEntity, Long> getDao() {
		return dao;
	}

}
