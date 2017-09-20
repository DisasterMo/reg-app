package edu.kit.scc.regapp.dto.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.ServiceEntityPolicyMapper;
import edu.kit.scc.regapp.entity.ServiceEntity;

@Stateless
public class ServicePolicyDtoServiceImpl extends BaseDtoServiceImpl<ServiceEntity, ServiceEntityDto, Long> implements ServicePolicyDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private ServiceEntityPolicyMapper mapper;
	
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
