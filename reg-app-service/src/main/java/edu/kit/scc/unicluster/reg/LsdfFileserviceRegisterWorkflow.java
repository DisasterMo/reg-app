package edu.kit.scc.unicluster.reg;

import java.util.Map;

import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.HomeOrgGroupEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.register.ldap.AbstractLdapRegisterWorkflow;

public class LsdfFileserviceRegisterWorkflow extends AbstractLdapRegisterWorkflow {

	@Override
	protected String constructHomeDir(String homeId, String homeUid, UserEntity user, Map<String, String> reconMap) {
		return "/" + homeId + "/" + reconMap.get("groupName") + "/" + reconMap.get("localUid");
	}

	@Override
	protected String constructLocalUid(String homeId, String homeUid, UserEntity user,
			Map<String, String> reconMap) {
		return homeId + "_" + homeUid;
	}

	@Override
	protected String constructGroupName(GroupEntity group) {
		if (group instanceof HomeOrgGroupEntity) {
			HomeOrgGroupEntity homeOrgGroup = (HomeOrgGroupEntity) group;
			if (homeOrgGroup.getPrefix() == null)
				return "_" + homeOrgGroup.getName();
			else
				return homeOrgGroup.getPrefix() + "_" + homeOrgGroup.getName();
		}
		else 
			return group.getName();
	}

	@Override
	protected Boolean isSambaEnabled() {
		return true;
	}
}
