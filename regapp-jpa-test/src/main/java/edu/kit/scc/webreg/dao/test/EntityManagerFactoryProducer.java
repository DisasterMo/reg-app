package edu.kit.scc.webreg.dao.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@ApplicationScoped
public class EntityManagerFactoryProducer {

	@Inject
	private BeanManager beanManager;

	@Inject
	private JpaTestConfiguration configuration;

	@Produces
	@ApplicationScoped
	public EntityManagerFactory produceEntityManagerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put("javax.persistence.bean.manager", beanManager);
		props.put(Environment.CONNECTION_PROVIDER, TransactionalConnectionProvider.class);
		props.put(AvailableSettings.LOADED_CLASSES, findAllEntities());
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("regapp-test", props);
		return factory;
	}

	@SuppressWarnings("rawtypes")
	private List<Class> findAllEntities() {
		Reflections reflections = new Reflections(configuration.getEntitiesBasePackage(),
				Scanners.TypesAnnotated.filterResultsBy(null).filterResultsBy(s -> true));
		return reflections.getTypesAnnotatedWith(Entity.class).stream().collect(Collectors.toList());
	}

	public void close(@Disposes EntityManagerFactory entityManagerFactory) {
		entityManagerFactory.close();
	}

}
