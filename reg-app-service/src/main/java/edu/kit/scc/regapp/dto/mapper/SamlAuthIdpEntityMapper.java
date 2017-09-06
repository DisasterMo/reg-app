package edu.kit.scc.regapp.dto.mapper;

import javax.enterprise.context.ApplicationScoped;

import edu.kit.scc.regapp.dto.entity.SamlAuthIdpEntityDto;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;

@ApplicationScoped
public class SamlAuthIdpEntityMapper extends AbstractBaseEntityMapper<SamlIdpMetadataEntity, SamlAuthIdpEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void copyAllProperties(SamlIdpMetadataEntity fromBaseEntity,
			SamlAuthIdpEntityDto toDtoEntity) {
		
	}

	@Override
	public Class<SamlAuthIdpEntityDto> getEntityDtoClass() {
		return SamlAuthIdpEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] { "name", "entityId" };
	}

}
