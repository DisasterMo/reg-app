package edu.kit.scc.webreg.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.scc.webreg.entity.ApplicationConfigEntity;

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
		
	}

}
