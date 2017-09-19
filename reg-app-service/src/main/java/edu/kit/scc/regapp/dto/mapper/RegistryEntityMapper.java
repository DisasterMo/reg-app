package edu.kit.scc.regapp.dto.mapper;

import javax.enterprise.context.ApplicationScoped;

import edu.kit.scc.regapp.dto.entity.RegistryEntityDto;
import edu.kit.scc.regapp.entity.RegistryEntity;

@ApplicationScoped
public class RegistryEntityMapper extends AbstractBaseEntityMapper<RegistryEntity, RegistryEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void copyAllProperties(RegistryEntity fromBaseEntity,
			RegistryEntityDto toDtoEntity) {

		toDtoEntity.setUserId(fromBaseEntity.getUser().getId());
		toDtoEntity.setServiceId(fromBaseEntity.getService().getId());
		toDtoEntity.setServiceName(fromBaseEntity.getService().getName());
		toDtoEntity.setServiceShortDescription(fromBaseEntity.getService().getShortDescription());
		
		if (toDtoEntity.getRegistryValues().containsKey("userPassword")) {
			toDtoEntity.getRegistryValues().remove("userPassword");
			toDtoEntity.getRegistryValues().put("userPassword", "set");
		}
	}

	@Override
	public Class<RegistryEntityDto> getEntityDtoClass() {
		return RegistryEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] {"registryStatus", "agreedTime", "lastAccessCheck", "lastFullReconcile",
				"lastReconcile", "lastStatusChange", "registryValues"};
	}

}
