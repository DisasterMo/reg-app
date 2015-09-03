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
package edu.kit.scc.webreg.rest.exc;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericRestInterfaceExceptionMapper implements ExceptionMapper<GenericRestInterfaceException> {

	@Override
	public Response toResponse(GenericRestInterfaceException ex) {
		return Response.status(500).entity(ex.getMessage())
				.type(MediaType.TEXT_PLAIN).build();
	}

}
