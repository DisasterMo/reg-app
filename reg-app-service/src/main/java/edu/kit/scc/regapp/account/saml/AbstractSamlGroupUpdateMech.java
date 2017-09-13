package edu.kit.scc.regapp.account.saml;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.regapp.audit.AccountUpdateAuditor;
import edu.kit.scc.regapp.dao.GroupDao;
import edu.kit.scc.regapp.dao.HomeOrgGroupDao;
import edu.kit.scc.regapp.dao.SerialDao;
import edu.kit.scc.regapp.dao.ServiceGroupFlagDao;
import edu.kit.scc.regapp.entity.GroupEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;
import edu.kit.scc.regapp.event.EventSubmitter;
import edu.kit.scc.regapp.exc.UserUpdateException;
import edu.kit.scc.regapp.service.tools.AttributeMapHelper;

public abstract class AbstractSamlGroupUpdateMech implements SamlGroupUpdateMech {

	protected final Logger logger = LoggerFactory.getLogger(AbstractSamlGroupUpdateMech.class);

	protected AttributeMapHelper mapHelper;

	protected HomeOrgGroupDao dao;
	
	protected GroupDao groupDao;

	protected ServiceGroupFlagDao groupFlagDao;
	
	protected SerialDao serialDao;

	protected EventSubmitter eventSubmitter;

	protected abstract HashSet<GroupEntity> update(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException;
	
	@Override
	public HashSet<GroupEntity> updateGroups(SamlAccountEntity account, Map<String, List<Object>> attributeMap,
			AccountUpdateAuditor auditor) throws UserUpdateException {
		return update(account, attributeMap, auditor);
	}

	@Override
	public void setEnv(AttributeMapHelper mapHelper, HomeOrgGroupDao homeOrgGroupDao, GroupDao groupDao,
			ServiceGroupFlagDao groupFlagDao, SerialDao serialDao, EventSubmitter eventSubmitter) {
		this.mapHelper = mapHelper;
		this.dao = homeOrgGroupDao;
		this.groupDao = groupDao;
		this.groupFlagDao = groupFlagDao;
		this.serialDao = serialDao;
		this.eventSubmitter = eventSubmitter;
	}

}
