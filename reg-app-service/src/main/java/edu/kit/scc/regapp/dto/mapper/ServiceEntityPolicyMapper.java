package edu.kit.scc.regapp.dto.mapper;

import java.util.HashSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import edu.kit.scc.regapp.dto.entity.PolicyActualAgreementEntityDto;
import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.entity.PolicyEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;

@ApplicationScoped
public class ServiceEntityPolicyMapper extends AbstractBaseEntityMapper<ServiceEntity, ServiceEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private PolicyActualAgreementEntityMapper policyMapper;	

	@Override
	protected void copyAllProperties(ServiceEntity fromBaseEntity,
			ServiceEntityDto toDtoEntity) {

		if (fromBaseEntity.getParentService() != null) 
			toDtoEntity.setParentServiceId(fromBaseEntity.getParentService().getId());

		toDtoEntity.setPolicies(new HashSet<PolicyActualAgreementEntityDto>());
		for (PolicyEntity policy : fromBaseEntity.getPolicies()) {
			PolicyActualAgreementEntityDto dto = new PolicyActualAgreementEntityDto();
			policyMapper.copyProperties(policy, dto);
			toDtoEntity.getPolicies().add(dto);
		}
	}

	@Override
	public Class<ServiceEntityDto> getEntityDtoClass() {
		return ServiceEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] { "name", "shortName", "description", "shortDescription" };
	}

}
