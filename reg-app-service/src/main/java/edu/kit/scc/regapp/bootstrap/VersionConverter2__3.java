package edu.kit.scc.regapp.bootstrap;

import java.util.List;

import javax.naming.NamingException;

import org.drools.core.command.runtime.GetKnowledgeBaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.regapp.entity.ApplicationConfigEntity;
import edu.kit.scc.regapp.entity.BusinessRuleEntity;
import edu.kit.scc.regapp.entity.UserEntity;
import edu.kit.scc.regapp.entity.account.SamlAccountEntity;

public class VersionConverter2__3 extends AbstractVersionConverter {

	private static final Logger logger = LoggerFactory.getLogger(VersionConverter2__3.class);

	@Override
	public String fromVersion() {
		return "2.0.0";
	}

	@Override
	public String toVersion() {
		return "3.0.0";
	}

	@Override
	public void convert(ApplicationConfigEntity appConfig) throws ConversionException {
		logger.info("Starting upgrade");
		try {
			getBpmProcessService().replaceAllInRules("import edu\\.kit\\.scc\\.webreg\\.", "import edu.kit.scc.regapp.");
			getBpmProcessService().replaceAllInRules("import edu\\.kit\\.scc\\.regapp\\.drools\\.", "import edu.kit.scc.regapp.bpm.");
			
//			List<UserEntity> userList = getUserService().findAll();
//			
//			for (UserEntity user : userList) {
//				logger.debug("Analyzing user {} ({})", user.getId(), user.getEppn());
//
//				// if idp is null, user is already converted or no saml user
//				if (user.getIdp() != null) {
//					SamlAccountEntity samlAccountEntity = getSamlAccountService().convertUserForSamlAccount(user);
//					logger.info("SamlAccount {} created for user {}", samlAccountEntity.getId(), user.getId());
//				}
//			}
		} catch (NamingException e) {
			throw new ConversionException(e);
		}
	}

}
