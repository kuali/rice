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

package org.kuali.rice.krms.api.type;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.krms.api.repository.RepositoryConstants;
import org.kuali.rice.krms.api.repository.RepositoryConstants.Namespaces;

import java.util.List;


@WebService(name = "KRMSTypeService", targetNamespace = RepositoryConstants.Namespaces.KRMS_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface KrmsTypeRepositoryService {

    /**
     * This will create a {@link KrmsTypeDefinition} exactly like the parameter passed in.
     *
     * @param krmsType - KrmsType
     * @throws IllegalArgumentException if the krmsType is null
     * @throws IllegalStateException if the KrmsType already exists in the system
     */
    @WebMethod(operationName="createKrmsType")
    void createKrmsType(@WebParam(name = "krmsType") KrmsTypeDefinition krmsType);

    /**
     * This will update an existing {@link KrmsTypeDefinition}
     *
     * @param krmsType - KrmsType
     * @throws IllegalArgumentException if the krmsType is null
     * @throws IllegalStateException if the KrmsType does not exist in the system
     */
    @WebMethod(operationName="updateKrmsType")
    void updateKrmsType(@WebParam(name = "krmsType") KrmsTypeDefinition krmsType);

    /**
     * Lookup a krms type based on the given id.
     *
     * @param id the given KRMS type id
     * @return a KRMS KrmsType object with the given id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getTypeById")
    @WebResult(name = "type")
    KrmsTypeDefinition getTypeById(@WebParam(name = "id") String id);

    /**
     * Get a krms type object based on name and namespace
     *
     * @param name the given type name
     * @param namespace the given type namespace
     * @return A krms type object with the given namespace and name if one with that name and namespace
     *         exists.  Otherwise, null is returned.
     * @throws IllegalStateException if multiple krms types exist with the same name and namespace
     */
    @WebMethod(operationName = "getTypeByNameAndNamespace")
    @WebResult(name = "type")
    KrmsTypeDefinition getTypeByNameAndNamespace(
            @WebParam(name = "name") String name,
            @WebParam(name = "namespace") String namespace);

   /**
     * Returns all KRMS types that for a given namespace.
     *
     * @return all KRMS types for a namespace
     */
    @WebMethod(operationName = "findAllTypesByNamespace")
    @WebResult(name = "namespaceTypes")
    List<KrmsTypeDefinition> findAllTypesByNamespace(
    		@WebParam(name = "namespace") String namespace);

    /**
     * Returns all KRMS types
     *
     * @return all KRMS types
     */
    @WebMethod(operationName = "findAllTypes")
    @WebResult(name = "allTypes")
    List<KrmsTypeDefinition> findAllTypes();
    
    /**
     * This will create a {@link KrmsTypeAttribute} exactly like the parameter passed in.
     *
     * @param krmsTypeAttribute - KrmsTypeAttribute
     * @throws IllegalArgumentException if the krmsTypeAttribute is null
     * @throws IllegalStateException if the KrmsTypeAttribute already exists in the system
     */
    @WebMethod(operationName="createKrmsTypeAttribute")
    void createKrmsTypeAttribute(@WebParam(name = "krmsTypeAttribute") KrmsTypeAttribute krmsTypeAttribute);

    /**
     * This will update an existing {@link KrmsTypeAttribute}
     *
     * @param krmsType - KrmsTypeAttribute
     * @throws IllegalArgumentException if the krmsTypeAttribute is null
     * @throws IllegalStateException if the KrmsTypeAttribute does not exist in the system
     */
    @WebMethod(operationName="updateKrmsTypeAttribute")
    void updateKrmsTypeAttribute(@WebParam(name = "krmsTypeAttribute") KrmsTypeAttribute krmsTypeAttribute);


}
