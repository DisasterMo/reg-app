package edu.kit.scc.regapp.register;

import edu.kit.scc.regapp.audit.Auditor;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.exc.RegisterException;

public interface GroupCapable {

	void deleteGroup(GroupEntity group, ServiceEntity service, Auditor auditor)
			throws RegisterException;

	void updateGroups(ServiceEntity service, GroupUpdateStructure updateStruct,
			Auditor auditor) throws RegisterException;
}
