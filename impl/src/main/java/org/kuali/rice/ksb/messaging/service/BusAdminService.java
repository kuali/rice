/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.messaging.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.ksb.api.KsbApiConstants;

/**
 * A service for administrative functions for a node on the service bus.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = "busAdminServiceSoap", targetNamespace = KsbApiConstants.Namespaces.KSB_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface BusAdminService {

	@WebMethod(operationName="ping")
    public void ping();

	@WebMethod(operationName="setCorePoolSize")
    public void setCorePoolSize(@WebParam(name = "corePoolSize") int corePoolSize);

	@WebMethod(operationName="setMaximumPoolSize")
    public void setMaximumPoolSize(@WebParam(name = "maxPoolSize") int maxPoolSize);

	@WebMethod(operationName="setConfigProperty")
    public void setConfigProperty(@WebParam(name = "propertyName") String propertyName,
    		@WebParam(name = "propertyValue") String propertyValue);

}
