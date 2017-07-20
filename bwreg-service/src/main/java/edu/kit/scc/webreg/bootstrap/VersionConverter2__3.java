package edu.kit.scc.webreg.bootstrap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.webreg.entity.ApplicationConfigEntity;
import edu.kit.scc.webreg.entity.UserEntity;

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
		List<UserEntity> userList = userService.findAll();
		
		for (UserEntity user : userList) {
			logger.debug("Analyzing user {} ({})", user.getId(), user.getEppn());

			// if idp is null, user is already converted or no saml user
			if (user.getIdp() != null) {
				
			}
		}
	}

}
