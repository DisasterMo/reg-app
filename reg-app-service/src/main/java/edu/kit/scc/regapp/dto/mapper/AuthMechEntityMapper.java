package edu.kit.scc.regapp.dto.mapper;

import javax.enterprise.context.ApplicationScoped;

import edu.kit.scc.regapp.dto.entity.AuthMechEntityDto;
import edu.kit.scc.regapp.entity.auth.AuthMechEntity;
import edu.kit.scc.regapp.entity.auth.LocalUPAuthMechEntity;
import edu.kit.scc.regapp.entity.auth.SamlAuthMechEntity;

@ApplicationScoped
public class AuthMechEntityMapper extends AbstractBaseEntityMapper<AuthMechEntity, AuthMechEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void copyAllProperties(AuthMechEntity fromBaseEntity,
			AuthMechEntityDto toDtoEntity) {
		if (fromBaseEntity instanceof SamlAuthMechEntity) {
			toDtoEntity.setType("SamlAuthMechEntity");
			toDtoEntity.setFederationId(((SamlAuthMechEntity) fromBaseEntity).getFederation().getId());
		}
		else if (fromBaseEntity instanceof LocalUPAuthMechEntity) {
			toDtoEntity.setType("LocalUPAuthMechEntity");
		}
	}

	@Override
	public Class<AuthMechEntityDto> getEntityDtoClass() {
		return AuthMechEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] { "name" };
	}

}
