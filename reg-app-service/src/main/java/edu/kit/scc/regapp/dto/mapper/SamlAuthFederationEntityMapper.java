package edu.kit.scc.regapp.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.SamlIdpMetadataDao;
import edu.kit.scc.regapp.dto.entity.SamlAuthFederationEntityDto;
import edu.kit.scc.regapp.dto.entity.SamlAuthIdpEntityDto;
import edu.kit.scc.regapp.entity.FederationEntity;
import edu.kit.scc.regapp.entity.SamlIdpMetadataEntity;

@ApplicationScoped
public class SamlAuthFederationEntityMapper extends AbstractBaseEntityMapper<FederationEntity, SamlAuthFederationEntityDto, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private SamlIdpMetadataDao idpDao;
	
	@Inject
	private SamlAuthIdpEntityMapper idpMapper;
	
	@Override
	protected void copyAllProperties(FederationEntity fromBaseEntity,
			SamlAuthFederationEntityDto toDtoEntity) {
		List<SamlIdpMetadataEntity> idpList = idpDao.findAllByFederationOrderByOrgname(fromBaseEntity);
		toDtoEntity.setIdpList(new ArrayList<SamlAuthIdpEntityDto>(idpList.size()));
		for (SamlIdpMetadataEntity idp : idpList) {
			SamlAuthIdpEntityDto dto = new SamlAuthIdpEntityDto();
			idpMapper.copyProperties(idp, dto);
			toDtoEntity.getIdpList().add(dto);
		}
	}

	@Override
	public Class<SamlAuthFederationEntityDto> getEntityDtoClass() {
		return SamlAuthFederationEntityDto.class;
	}

	@Override
	protected String[] getPropertiesToCopy() {
		return new String[] { "displayName", "entityId", "description", "orgName" };
	}

}
