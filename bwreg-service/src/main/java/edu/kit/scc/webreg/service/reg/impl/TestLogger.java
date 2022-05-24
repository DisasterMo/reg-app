/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.scc.webreg.service.reg.impl;

import edu.kit.scc.webreg.audit.Auditor;
import edu.kit.scc.webreg.entity.RegistryEntity;
import edu.kit.scc.webreg.entity.ServiceEntity;
import edu.kit.scc.webreg.entity.UserEntity;
import edu.kit.scc.webreg.exc.RegisterException;
import edu.kit.scc.webreg.service.reg.RegisterUserWorkflow;
import edu.kit.scc.webreg.service.reg.SetPasswordCapable;
import edu.kit.scc.webreg.service.ssh.SshConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bum-admin
 */
public class TestLogger implements RegisterUserWorkflow, SetPasswordCapable {

	private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);
        private static final SshConnector sshCon = new SshConnector();

	@Override
	public void registerUser(UserEntity user, ServiceEntity service, RegistryEntity registry, Auditor auditor) throws RegisterException {
                //String[] connDetails = {"~/.ssh/id_ed25519","6:q{T+[&2U7i","~/.ssh/known_hosts"};
                String errOut = "";
                logger.info("Testlogger registriert User {} für {} !", user.getEppn(), service.getName());
                errOut = sshCon.setPassword("bloeb", "geheim", "bv1.hpc.itc.rwth-aachen.de", "bv", "/home/.ssh/id_ed25519", "6:q{T+[&2U7i", "");
                
                if (! errOut.isBlank())
                    logger.warn("Mit dem SshConnector ist ein Fehler aufgetreten:\n" + errOut);
                else
                    logger.info("Testlogger hat User {} für {} registriert!", user.getEppn(), service.getName());
	}

	@Override
	public void deregisterUser(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor) throws RegisterException {
		logger.info("User {} wurde aus {} rausgeschmissen!", user.getEppn(), service.getName());
	}

	@Override
	public void reconciliation(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor) {
		logger.info("Was ist denn Reconciliation? User: {}; Service: {}", user.getEppn(), service.getName());
	}

	@Override
	public Boolean updateRegistry(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor) throws RegisterException {
		logger.info("Üpdate Registry för üser {} för Serviiice {}", user.getEppn(), service.getName());
		return false;
	}
        
	@Override
	public void setPassword(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor, String password) throws RegisterException {
		logger.info("Setze Passwort für User {} und Service {}", user.getEppn(), service.getName());
	}

	@Override
	public void deletePassword(UserEntity user, ServiceEntity service,
			RegistryEntity registry, Auditor auditor) throws RegisterException {
		logger.info("Lösche Passwort für User {} und Service {}", user.getEppn(), service.getName());
	}
}
