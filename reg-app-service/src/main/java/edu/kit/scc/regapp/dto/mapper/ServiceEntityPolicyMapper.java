package edu.kit.scc.regapp.dto.mapper;

import javax.enterprise.context.ApplicationScoped;

import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.entity.ServiceEntity;

@ApplicationScoped
public class ServiceEntityPolicyMapper extends AbstractBaseEntityMapper<ServiceEntity, ServiceEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void copyAllProperties(ServiceEntity fromBaseEntity,
			ServiceEntityDto toDtoEntity) {

		if (fromBaseEntity.getParentService() != null) 
			toDtoEntity.setParentServiceId(fromBaseEntity.getParentService().getId());
		
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
