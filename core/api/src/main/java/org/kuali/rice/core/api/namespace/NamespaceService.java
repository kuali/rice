/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.core.api.namespace;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "namespaceService", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface NamespaceService {

    /**
     * Gets a {@link Namespace} from a namespace code.
     *
     * <p>
     *   This method will return null if the namespace does not exist.
     * </p>
     *
     * @param code the code to retrieve the namespace by. cannot be blank.
     * @return a {@link Namespace} or null
     * @throws IllegalArgumentException if the code is blank
     */
    @WebMethod(operationName="getNamespace")
    @WebResult(name = "namespace")
	Namespace getNamespace(@WebParam(name = "code") String code) throws RiceIllegalArgumentException;
}
