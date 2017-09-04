package edu.kit.scc.regapp.dto.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.auth.AuthMechDao;
import edu.kit.scc.regapp.dto.entity.AuthMechEntityDto;
import edu.kit.scc.regapp.dto.mapper.AuthMechEntityMapper;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;

@Stateless
public class AuthMechDtoServiceImpl extends BaseDtoServiceImpl<AuthMechEntity, AuthMechEntityDto, Long> implements AuthMechDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private AuthMechEntityMapper mapper;
	
	@Inject
	private AuthMechDao dao;
	
	@Override
	public List<AuthMechEntityDto> findByHostname(String hostname) {
		List<AuthMechEntity> mechList = dao.findByHostname(hostname);
		return createListDto(mechList);

	}
	
	@Override
	protected BaseEntityMapper<AuthMechEntity, AuthMechEntityDto, Long> getMapper() {
		return mapper;
	}

	@Override
	protected BaseDao<AuthMechEntity, Long> getDao() {
		return dao;
	}

}
