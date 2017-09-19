package edu.kit.scc.regapp.dto.service;

import java.util.List;

import edu.kit.scc.regapp.dto.entity.RegistryEntityDto;
import edu.kit.scc.regapp.entity.RegistryEntity;

public interface RegistryDtoService extends BaseDtoService<RegistryEntity, RegistryEntityDto, Long> {

	List<RegistryEntityDto> findRegistriesForDepro(String serviceShortName);

	List<RegistryEntityDto> findRegistriesForUserDisplay(Long userId);

}
