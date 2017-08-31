package edu.kit.scc.regapp.dto.mapper;

import javax.enterprise.context.ApplicationScoped;

import edu.kit.scc.regapp.dto.entity.UserEntityDto;
import edu.kit.scc.regapp.entity.UserEntity;

@ApplicationScoped
public class UserEntityMapper extends AbstractBaseEntityMapper<UserEntity, UserEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void copyAllProperties(UserEntity fromBaseEntity,
			UserEntityDto toDtoEntity) {

	}

	@Override
	public Class<UserEntityDto> getEntityDtoClass() {
		return UserEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] { };
	}

}
