package edu.kit.scc.regapp.account.saml;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.bootstrap.ApplicationConfig;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.HomeOrgGroupDao;
import edu.kit.scc.regapp.dao.SerialDao;
import edu.kit.scc.regapp.dao.ServiceGroupFlagDao;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;

public interface SamlGroupUpdateMech {

	HashSet<GroupEntity> updateGroups(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException;

	void setEnv(AttributeMapHelper mapHelper, HomeOrgGroupDao homeOrgGroupDao, GroupDao groupDao,
			ServiceGroupFlagDao groupFlagDao, SerialDao serialDao, EventSubmitter eventSubmitter, ApplicationConfig appConfig);

}
