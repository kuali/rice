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


@WebService(name = "KrmsAttributeDefinitionService", targetNamespace = RepositoryConstants.Namespaces.KRMS_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface KrmsAttributeDefinitionService {

    /**
     * This will create a {@link KrmsAttributeDefinition} exactly like the parameter passed in.
     *
     * @param attributeDefinition - KrmsAttributeDefinition
     * @throws IllegalArgumentException if the attribute definition is null
     * @throws IllegalStateException if the attribute definition already exists in the system
     */
    @WebMethod(operationName="createKrmsAttributeDefinition")
    void createAttributeDefinition(@WebParam(name = "attributeDefinition") KrmsAttributeDefinition attributeDefinition);

    /**
     * This will update a {@link KrmsAttributeDefinition}.
     *
     *
     * @param attributeDefinition - KrmsAttributeDefinition
     * @throws IllegalArgumentException if the attribute definition is null
     * @throws IllegalStateException if the attribute definition does not exist in the system
     */
    @WebMethod(operationName="updateKrmsAttributeDefinition")
    void updateAttributeDefinition(@WebParam(name = "attributeDefinition") KrmsAttributeDefinition attributeDefinition);

    /**
     * Lookup a KrmsAttributeDefinition based on the given id.
     *
     * @param id the given KrmsAttributeDefinition id
     * @return a KrmsAttributeDefinition object with the given id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getAttributeDefinitionById")
    @WebResult(name = "attributeDefinition")
    KrmsAttributeDefinition getAttributeDefinitionById(@WebParam(name = "id") String id);

    /**
     * Get a KrmsAttributeDefinition object based on name and namespace
     *
     * @param name the given name
     * @param namespace the given type namespace
     * @return A KrmsAttributeDefinition object with the given namespace and name if one with that name and namespace
     *         exists.  Otherwise, null is returned.
     * @throws IllegalStateException if multiple KrmsAttributeDefinitions exist with the same name and namespace
     */
    @WebMethod(operationName = "getAttributeDefinitionByNameAndNamespace")
    @WebResult(name = "attributeDefinition")
    KrmsAttributeDefinition getAttributeDefinitionByNameAndNamespace(
            @WebParam(name = "name") String name,
            @WebParam(name = "namespace") String namespace);

   /**
     * Returns all KrmsAttributeDefinition that for a given namespace.
     *
     * @return all KrmsAttributeDefinition for a namespace
     */
    @WebMethod(operationName = "findAttributeDefinitionsByNamespace")
    @WebResult(name = "attributeDefinitions")
    List<KrmsAttributeDefinition> findAttributeDefinitionsByNamespace(
    		@WebParam(name = "namespace") String namespace);

    /**
     * Returns all KrmsAttributeDefinitions
     *
     * @return all KrmsAttributeDefinitions
     */
    @WebMethod(operationName = "findAllAttributeDefinitions")
    @WebResult(name = "allAttributeDefinitions")
    List<KrmsAttributeDefinition> findAllAttributeDefinitions();
}
