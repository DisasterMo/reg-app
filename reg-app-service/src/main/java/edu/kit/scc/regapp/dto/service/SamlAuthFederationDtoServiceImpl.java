package edu.kit.scc.regapp.dto.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.FederationDao;
import edu.kit.scc.regapp.dto.entity.SamlAuthFederationEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.SamlAuthFederationEntityMapper;
import edu.kit.scc.regapp.entity.FederationEntity;

@Stateless
public class SamlAuthFederationDtoServiceImpl extends BaseDtoServiceImpl<FederationEntity, SamlAuthFederationEntityDto, Long> implements SamlAuthFederationDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private SamlAuthFederationEntityMapper mapper;
	
	@Inject
	private FederationDao dao;
	
	@Override
	protected BaseEntityMapper<FederationEntity, SamlAuthFederationEntityDto, Long> getMapper() {
		return mapper;
	}

	@Override
	protected BaseDao<FederationEntity, Long> getDao() {
		return dao;
	}

}
