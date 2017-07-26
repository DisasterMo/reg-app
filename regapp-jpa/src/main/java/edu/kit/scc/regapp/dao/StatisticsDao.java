package edu.kit.scc.regapp.dao;

import java.util.List;

import edu.kit.scc.regapp.entity.ServiceEntity;

public interface StatisticsDao {

	List<Object> countUsersPerIdp();

	List<Object> countUsersPerService();

	List<Object> countUsersPerIdpAndService(ServiceEntity service);

	List<Object> countUsersPerMonth();

	List<Object> countRegistriesPerMonthAndService();

}
