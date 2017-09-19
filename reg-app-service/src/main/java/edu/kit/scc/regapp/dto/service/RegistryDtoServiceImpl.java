package edu.kit.scc.regapp.dto.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.RegistryDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.dto.entity.RegistryEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.RegistryEntityMapper;
import edu.kit.scc.regapp.entity.RegistryEntity;
import edu.kit.scc.regapp.entity.RegistryStatus;
import edu.kit.scc.regapp.entity.UserEntity;

@Stateless
public class RegistryDtoServiceImpl extends BaseDtoServiceImpl<RegistryEntity, RegistryEntityDto, Long> implements RegistryDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private RegistryEntityMapper mapper;
	
	@Inject
	private RegistryDao dao;

	@Inject
	private UserDao userDao;
	
	@Override
	public List<RegistryEntityDto> findRegistriesForDepro(String serviceShortName) {
		List<RegistryEntity> regList = dao.findRegistriesForDepro(serviceShortName);
		return createListDto(regList);
	}
	
	@Override
	public List<RegistryEntityDto> findRegistriesForUserDisplay(Long userId) {
		UserEntity user = userDao.findById(userId); 
		List<RegistryEntity> regList = dao.findByUserAndStatus(user, RegistryStatus.ACTIVE, 
				RegistryStatus.LOST_ACCESS, RegistryStatus.PENDING);
		return createListDto(regList);
	}
	
	@Override
	protected BaseEntityMapper<RegistryEntity, RegistryEntityDto, Long> getMapper() {
		return mapper;
	}

	@Override
	protected BaseDao<RegistryEntity, Long> getDao() {
		return dao;
	}

}
