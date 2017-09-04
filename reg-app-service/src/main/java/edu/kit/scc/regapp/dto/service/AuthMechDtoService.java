package edu.kit.scc.regapp.dto.service;

import java.util.List;

import edu.kit.scc.regapp.dto.entity.AuthMechEntityDto;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;

public interface AuthMechDtoService extends BaseDtoService<AuthMechEntity, AuthMechEntityDto, Long> {

	List<AuthMechEntityDto> findByHostname(String hostname);

}
