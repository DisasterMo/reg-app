package edu.kit.scc.regapp.dto.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.RegistryDao;
import edu.kit.scc.regapp.dto.entity.RegistryEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.RegistryEntityMapper;
import edu.kit.scc.regapp.entity.RegistryEntity;

@Stateless
public class RegistryDtoServiceImpl extends BaseDtoServiceImpl<RegistryEntity, RegistryEntityDto, Long> implements RegistryDtoService {

	private static final long serialVersionUID = 1L;

	@Inject
	private RegistryEntityMapper mapper;
	
	@Inject
	private RegistryDao dao;

	@Override
	public List<RegistryEntityDto> findRegistriesForDepro(String serviceShortName) {
		List<RegistryEntity> regList = dao.findRegistriesForDepro(serviceShortName);
		List<RegistryEntityDto> dtoList = new ArrayList<RegistryEntityDto>(regList.size());
		for (RegistryEntity reg : regList) {
			RegistryEntityDto dto = createNewDto();
			mapper.copyProperties(reg, dto);
			dtoList.add(dto);
		}
		return dtoList;
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
