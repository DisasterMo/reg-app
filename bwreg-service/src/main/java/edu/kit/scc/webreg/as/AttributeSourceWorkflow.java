package edu.kit.scc.webreg.as;

import java.io.Serializable;

import edu.kit.scc.webreg.audit.AttributeSourceAuditor;
import edu.kit.scc.webreg.dao.as.ASUserAttrValueDao;
import edu.kit.scc.webreg.dao.as.AttributeSourceGroupDao;
import edu.kit.scc.webreg.entity.as.ASUserAttrEntity;
import edu.kit.scc.webreg.exc.UserUpdateException;

public interface AttributeSourceWorkflow extends Serializable {

	Boolean pollUserAttributes(ASUserAttrEntity asUserAttr,
			ASUserAttrValueDao asValueDao, AttributeSourceGroupDao attributeSourceGroupDao,
			AttributeSourceAuditor auditor)
			throws UserUpdateException;
}
