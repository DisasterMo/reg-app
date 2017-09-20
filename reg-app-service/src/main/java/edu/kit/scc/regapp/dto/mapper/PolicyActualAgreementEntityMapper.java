package edu.kit.scc.regapp.dto.mapper;

import javax.enterprise.context.ApplicationScoped;

import edu.kit.scc.regapp.dto.entity.PolicyActualAgreementEntityDto;
import edu.kit.scc.regapp.entity.PolicyEntity;

@ApplicationScoped
public class PolicyActualAgreementEntityMapper extends AbstractBaseEntityMapper<PolicyEntity, PolicyActualAgreementEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void copyAllProperties(PolicyEntity fromBaseEntity,
			PolicyActualAgreementEntityDto toDtoEntity) {

		if (fromBaseEntity.getActualAgreement() != null) {
			toDtoEntity.setAgreementText(fromBaseEntity.getActualAgreement().getAgreement());
			toDtoEntity.setAgreementName(fromBaseEntity.getActualAgreement().getName());
		}
		
	}

	@Override
	public Class<PolicyActualAgreementEntityDto> getEntityDtoClass() {
		return PolicyActualAgreementEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] { "name" };
	}

}
