package edu.kit.scc.regapp.dto.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.bpm.KnowledgeSessionWorker;
import edu.kit.scc.regapp.dao.BaseDao;
import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.dao.UserDao;
import edu.kit.scc.regapp.dto.entity.ServiceEntityDto;
import edu.kit.scc.regapp.dto.mapper.BaseEntityMapper;
import edu.kit.scc.regapp.dto.mapper.ServiceEntityShortMapper;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.entity.UserEntity;

@Stateless
public class ServiceShortDtoServiceImpl extends BaseDtoServiceImpl<ServiceEntity, ServiceEntityDto, Long> implements ServiceShortDtoService {

	private static final long serialVersionUID = 1L;
	
	public static final String SERVICE_FILTER_RULE = "service_filter_rule";
	
	@Inject
	private ServiceEntityShortMapper mapper;
	
	@Inject
	private ServiceDao dao;

	@Inject
	private UserDao userDao;
	
	@Inject
	private ApplicationConfig appConfig;
	
	@Inject
	private KnowledgeSessionWorker knowledgeSession;
	
	@Override
	public List<ServiceEntityDto> findServicesAvailableForUser(Long userId) {
		UserEntity user = userDao.findById(userId);
		List<ServiceEntity> serviceList = dao.findAvailableForUser(user);
		
		if (appConfig.getConfigValue(SERVICE_FILTER_RULE) != null) {
			List<ServiceEntity> filteredServiceList = 
					knowledgeSession.checkServiceFilterRule(
							appConfig.getConfigValue(SERVICE_FILTER_RULE), user, serviceList);
			
			return createListDto(filteredServiceList);
		}
		else {
			return createListDto(serviceList);
		}
	}
	
	@Override
	protected BaseEntityMapper<ServiceEntity, ServiceEntityDto, Long> getMapper() {
		return mapper;
	}

	@Override
	protected BaseDao<ServiceEntity, Long> getDao() {
		return dao;
	}

}
