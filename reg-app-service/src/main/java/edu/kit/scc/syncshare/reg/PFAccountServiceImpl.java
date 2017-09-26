/*******************************************************************************
 * Copyright (c) 2014 Michael Simon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Michael Simon - initial
 ******************************************************************************/
package edu.kit.scc.syncshare.reg;

import javax.ejb.Stateless;
import javax.inject.Inject;

import edu.kit.scc.regapp.dao.ServiceDao;
import edu.kit.scc.regapp.entity.ServiceEntity;
import edu.kit.scc.regapp.exc.RegisterException;
import edu.kit.scc.regapp.register.PropertyReader;

@Stateless
public class PFAccountServiceImpl implements PFAccountService {

	@Inject
	private ServiceDao serviceDao;

	@Override
	public PFAccount findById(String id, ServiceEntity serviceEntity) throws RegisterException {

		serviceEntity = serviceDao.findByIdWithServiceProps(serviceEntity.getId());
		PFWorker pfWorker = new PFWorker(PropertyReader.newRegisterPropReader(serviceEntity), null);
		return pfWorker.getAccountInfoById(id);
	}

	@Override
	public PFAccount findByUsername(String username, ServiceEntity serviceEntity) throws RegisterException {

		serviceEntity = serviceDao.findByIdWithServiceProps(serviceEntity.getId());
		PFWorker pfWorker = new PFWorker(PropertyReader.newRegisterPropReader(serviceEntity), null);
		return pfWorker.getAccountInfoByUsername(username);
	}

	@Override
	public PFAccount update(PFAccount account, ServiceEntity serviceEntity) throws RegisterException {

		serviceEntity = serviceDao.findByIdWithServiceProps(serviceEntity.getId());
		PFWorker pfWorker = new PFWorker(PropertyReader.newRegisterPropReader(serviceEntity), null);
		return pfWorker.storeAccount(account);
	}
}
