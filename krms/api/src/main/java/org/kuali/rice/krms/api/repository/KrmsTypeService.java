/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.api.repository;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;


@WebService(name = "KRMSTypeService", targetNamespace = KrmsType.Constants.KRMSNAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface KrmsTypeService {

    /**
     * Lookup a krms type based on the given id.
     *
     * @param id the given KRMS type id
     * @return a KRMS KrmsType object with the given id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getTypeById")
    @WebResult(name = "type")
    KrmsType getTypeById(@WebParam(name = "id") String id);

    /**
     * Get a krms type object based on name and namespace
     *
     * @param name the given type name
     * @param namespace the given type namespace
     * @return A krms type object with the given namespace and name if a country with that name and namespace
     *         exists.  Otherwise, null is returned.
     * @throws IllegalStateException if multiple krms types exist with the same name and namespace
     */
    @WebMethod(operationName = "getTypeByNameAndNamespace")
    @WebResult(name = "type")
    KrmsType getTypeByNameAndNamespace(
            @WebParam(name = "name") String name,
            @WebParam(name = "namespace") String namespace);

   /**
     * Returns all KRMS types that for a given namespace.
     *
     * @return all KRMS types for a namespace
     */
    @WebMethod(operationName = "findAllTypesByNamespace")
    @WebResult(name = "namespaceTypes")
    List<KrmsType> findAllTypesByNamespace(
    		@WebParam(name = "namespace") String namespace);

    /**
     * Returns all KRMS types
     *
     * @return all KRMS types
     */
    @WebMethod(operationName = "findAllTypes")
    @WebResult(name = "allTypes")
    List<KrmsType> findAllTypes();
}
