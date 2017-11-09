package edu.kit.scc.regapp.ds;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

@ApplicationScoped
public class DataSourceProducer {

	@PersistenceContext(unitName = "bwidm")
	private EntityManager em;

	@PersistenceUnit(unitName = "org.jbpm.domain")
	private EntityManagerFactory emf;

	@Produces
	@DefaultDatasource
	public EntityManager getEntityManager() {
		return em;
	}

	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
}
