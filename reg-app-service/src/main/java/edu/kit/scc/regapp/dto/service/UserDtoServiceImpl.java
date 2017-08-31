package edu.kit.scc.regapp.dto.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.dto.entity.UserEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.UserEntityMapper;
import edu.kit.scc.regapp.entity.UserEntity;

@Stateless
public class UserDtoServiceImpl extends BaseDtoServiceImpl<UserEntity, UserEntityDto, Long> implements UserDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserEntityMapper mapper;
	
	@Inject
	private UserDao dao;
	
	@Override
	protected BaseEntityMapper<UserEntity, UserEntityDto, Long> getMapper() {
		return mapper;
	}

	@Override
	protected BaseDao<UserEntity, Long> getDao() {
		return dao;
	}

}
