package edu.kit.scc.webreg.bootstrap;

import edu.kit.scc.webreg.entity.ApplicationConfigEntity;
import edu.kit.scc.webreg.service.AdminUserService;
import edu.kit.scc.webreg.service.GroupService;
import edu.kit.scc.webreg.service.RoleService;
import edu.kit.scc.webreg.service.SerialService;
import edu.kit.scc.webreg.service.ServiceService;
import edu.kit.scc.webreg.service.UserService;

public abstract class AbstractVersionConverter implements VersionConverter {

	protected GroupService groupService;
	protected UserService userService;
	protected RoleService roleService;
	protected ServiceService serviceService;
	protected AdminUserService adminUserService;
	protected SerialService serialService;
	
	public abstract String fromVersion();
	public abstract String toVersion();
	public abstract void convert(ApplicationConfigEntity appConfig) throws ConversionException;

	public void populateServices(GroupService groupService, UserService userService, RoleService roleService,
			ServiceService serviceService, AdminUserService adminUserService, SerialService serialService) {
		this.groupService = groupService;
		this.userService = userService;
		this.roleService = roleService;
		this.serviceService = serviceService;
		this.adminUserService = adminUserService;
		this.serialService = serialService;
	}
	
	public GroupService getGroupService() {
		return groupService;
	}
	
	public UserService getUserService() {
		return userService;
	}
	
	public RoleService getRoleService() {
		return roleService;
	}
	
	public ServiceService getServiceService() {
		return serviceService;
	}
	
	public AdminUserService getAdminUserService() {
		return adminUserService;
	}
	public SerialService getSerialService() {
		return serialService;
	}
	
}
