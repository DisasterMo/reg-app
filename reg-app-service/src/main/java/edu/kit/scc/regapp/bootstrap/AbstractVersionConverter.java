package edu.kit.scc.regapp.bootstrap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.kit.scc.regapp.bpm.BpmProcessService;
import edu.kit.scc.regapp.entity.ApplicationConfigEntity;
import edu.kit.scc.regapp.job.JobClassService;
import edu.kit.scc.regapp.service.account.SamlAccountService;
import edu.kit.scc.regapp.service.role.RoleService;

public abstract class AbstractVersionConverter implements VersionConverter {

	protected InitialContext ic;

	private RoleService roleService;
	private SamlAccountService samlAccountService;
	private BpmProcessService bpmProcessService;
	private JobClassService jobClassService;
	
//	private GroupService groupService;
//	private UserService userService;
//	private ServiceService serviceService;
//	private AdminUserService adminUserService;
//	private SerialService serialService;
//	private SamlSpConfigurationService samlSpConfigurationService;
	
	public abstract String fromVersion();
	public abstract String toVersion();
	public abstract void convert(ApplicationConfigEntity appConfig) throws ConversionException;

	public AbstractVersionConverter() {
		try {
			ic = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public RoleService getRoleService() throws NamingException {
		if (roleService == null) {
			roleService = (RoleService) ic.lookup("global/reg-app/reg-app-service/RoleServiceImpl!edu.kit.scc.regapp.service.RoleService");
		}
		return roleService;
	}

	public SamlAccountService getSamlAccountService() throws NamingException {
		if (samlAccountService == null) {
			samlAccountService = (SamlAccountService) ic.lookup("global/reg-app/reg-app-service/SamlAccountServiceImpl!edu.kit.scc.regapp.service.account.SamlAccountService");
		}
		return samlAccountService;
	}

	public BpmProcessService getBpmProcessService() throws NamingException {
		if (bpmProcessService == null) {
			bpmProcessService = (BpmProcessService) ic.lookup("global/reg-app/reg-app-service/BpmProcessServiceImpl!edu.kit.scc.regapp.bpm.BpmProcessService");
		}
		return bpmProcessService;
	}

	public JobClassService getJobClassService() throws NamingException {
		if (jobClassService == null) {
			jobClassService = (JobClassService) ic.lookup("global/reg-app/reg-app-service/JobClassServiceImpl!edu.kit.scc.regapp.job.JobClassService");
		}
		return jobClassService;
	}

//	public GroupService getGroupService() throws NamingException {
//		if (groupService == null) {
//			groupService = (GroupService) ic.lookup("global/reg-app/reg-app-service/GroupServiceImpl!edu.kit.scc.webreg.service.GroupService");
//		}
//		return groupService;
//	}
//	
//	public UserService getUserService() throws NamingException {
//		if (userService == null) {
//			userService = (UserService) ic.lookup("global/reg-app/reg-app-service/UserServiceImpl!edu.kit.scc.webreg.service.UserService");
//		}
//		return userService;
//	}
//	
//	
//	public ServiceService getServiceService() throws NamingException {
//		if (serviceService == null) {
//			serviceService = (ServiceService) ic.lookup("global/reg-app/reg-app-service/ServiceServiceImpl!edu.kit.scc.webreg.service.ServiceService");
//		}
//		return serviceService;
//	}
//	
//	public AdminUserService getAdminUserService() throws NamingException {
//		if (adminUserService == null) {
//			adminUserService = (AdminUserService) ic.lookup("global/reg-app/reg-app-service/AdminUserServiceImpl!edu.kit.scc.webreg.service.AdminUserService");
//		}
//		return adminUserService;
//	}
//
//	public SerialService getSerialService() throws NamingException {
//		if (serialService == null) {
//			serialService = (SerialService) ic.lookup("global/reg-app/reg-app-service/SerialServiceImpl!edu.kit.scc.webreg.service.SerialService");
//		}
//		return serialService;
//	}
//
//
//	public SamlSpConfigurationService getSamlSpConfigurationService() throws NamingException {
//		if (samlSpConfigurationService == null) {
//			samlSpConfigurationService = (SamlSpConfigurationService) ic.lookup("global/reg-app/reg-app-service/SamlSpConfigurationServiceImpl!edu.kit.scc.webreg.service.SamlSpConfigurationService");
//		}
//		return samlSpConfigurationService;
//	}
		
}
