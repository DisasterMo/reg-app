package edu.kit.scc.regapp.bootstrap;

import edu.kit.scc.webreg.entity.ApplicationConfigEntity;

public interface VersionConverter {

	public String fromVersion();
	public String toVersion();
	public void convert(ApplicationConfigEntity appConfig) throws ConversionException;
}
