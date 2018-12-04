package edu.kit.scc.regapp.rest;

import java.util.Locale;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class JsonConfigurator implements ContextResolver<Jsonb> {

	private static final Logger logger = LoggerFactory.getLogger(JsonConfigurator.class);
	
	@Override
	public Jsonb getContext(Class<?> type) {
		logger.info("GetContext called for JsonbConfig");
		JsonbConfig config = getJsonbConfig();
		return JsonbBuilder.newBuilder().withConfig(config).build();
	}

	private JsonbConfig getJsonbConfig() {
		return new JsonbConfig().withDateFormat(JsonbDateFormat.TIME_IN_MILLIS, Locale.GERMANY);
	}
}
