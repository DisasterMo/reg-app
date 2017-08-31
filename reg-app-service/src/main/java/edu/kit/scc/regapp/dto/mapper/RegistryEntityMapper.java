package edu.kit.scc.regapp.dto.mapper;

import java.util.HashSet;

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
		toDtoEntity.setUserUidNumber(fromBaseEntity.getUser().getUidNumber());
		toDtoEntity.setUserEppn(fromBaseEntity.getUser().getEppn());
		toDtoEntity.setUserEmailAddress(fromBaseEntity.getUser().getEmail());
		toDtoEntity.setUserEmailAddresses(new HashSet<String>());
		toDtoEntity.getUserEmailAddresses().add(fromBaseEntity.getUser().getEmail());
		toDtoEntity.getUserEmailAddresses().addAll(fromBaseEntity.getUser().getEmailAddresses());
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
