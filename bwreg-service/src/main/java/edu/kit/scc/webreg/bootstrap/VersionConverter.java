package edu.kit.scc.webreg.bootstrap;

import edu.kit.scc.webreg.entity.ApplicationConfigEntity;
import edu.kit.scc.webreg.service.AdminUserService;
import edu.kit.scc.webreg.service.GroupService;
import edu.kit.scc.webreg.service.RoleService;
import edu.kit.scc.webreg.service.SerialService;
import edu.kit.scc.webreg.service.ServiceService;
import edu.kit.scc.webreg.service.UserService;

public interface VersionConverter {

	public String fromVersion();
	public String toVersion();
	public void populateServices(GroupService groupService, UserService userService, RoleService roleService,
			ServiceService serviceService, AdminUserService adminUserService, SerialService serialService);
	public void convert(ApplicationConfigEntity appConfig) throws ConversionException;
}
