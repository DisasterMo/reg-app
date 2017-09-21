package edu.kit.scc.regapp.dto.service;

import java.util.List;

import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.entity.ServiceEntity;

public interface ServiceShortDtoService extends BaseDtoService<ServiceEntity, ServiceEntityDto, Long> {

	List<ServiceEntityDto> findServicesAvailableForUser(Long userId);


}
