/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krms.api.repository.type;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.krms.api.KrmsConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;


@WebService(name = "KRMSTypeService", targetNamespace = KrmsConstants.Namespaces.KRMS_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface KrmsTypeRepositoryService {

    /**
     * This will create a {@link KrmsTypeDefinition} exactly like the parameter passed in.
     *
     * @param krmsType - KrmsType
     * @throws IllegalArgumentException if the krmsType is
     * +
     * @throws IllegalStateException if the KrmsType already exists in the system
     */
    @WebMethod(operationName="createKrmsType")
    @WebResult(name = "krmsType")
    @CacheEvict(value={KrmsTypeDefinition.Cache.NAME}, allEntries = true)
    KrmsTypeDefinition createKrmsType(@WebParam(name = "krmsType") KrmsTypeDefinition krmsType)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * This will update an existing {@link KrmsTypeDefinition}
     *
     * @param krmsType - KrmsType
     * @throws IllegalArgumentException if the krmsType is null
     * @throws IllegalStateException if the KrmsType does not exist in the system
     */
    @WebMethod(operationName="updateKrmsType")
    @WebResult(name = "krmsType")
    @CacheEvict(value={KrmsTypeDefinition.Cache.NAME}, allEntries = true)
    KrmsTypeDefinition updateKrmsType(@WebParam(name = "krmsType") KrmsTypeDefinition krmsType)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * Lookup a krms type based on the given id.
     *
     * @param id the given KRMS type id
     * @return a KRMS KrmsType object with the given id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getTypeById")
    @WebResult(name = "type")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'id=' + #p0")
    KrmsTypeDefinition getTypeById(@WebParam(name = "id") String id)
        throws RiceIllegalArgumentException;

    /**
     * Get a krms type object based on name and namespace
     *
     * @param namespaceCode the given type namespace
     * @param name the given type name
     * 
     * @return A krms type object with the given namespace and name if one with that name and namespace
     *         exists.  Otherwise, null is returned.
     *
     * @throws IllegalArgumentException if the given namespaceCode or name is a null or blank value
     * @throws IllegalStateException if multiple krms types exist with the same name and namespace
     */
    @WebMethod(operationName = "getTypeByName")
    @WebResult(name = "type")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'namespaceCode=' + #p0 + '|' + 'name=' + #p1")
    KrmsTypeDefinition getTypeByName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name)
        throws RiceIllegalArgumentException, RiceIllegalStateException;

   /**
     * Returns all KRMS types that for a given namespace.
     *
     * @return all KRMS types for a namespace
     * @throws IllegalArgumentException if the given namespaceCode is a null or blank value
     */
    @WebMethod(operationName = "findAllTypesByNamespace")
    @XmlElementWrapper(name = "namespaceTypes", required = true)
    @XmlElement(name = "namespaceType", required = false)
    @WebResult(name = "namespaceTypes")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'allByNamespaceCode=' + #p0")
    List<KrmsTypeDefinition> findAllTypesByNamespace(
    		@WebParam(name = "namespaceCode") String namespaceCode)
        throws RiceIllegalArgumentException;

    /**
     * Returns all KRMS types
     *
     * @return all KRMS types
     */
    @WebMethod(operationName = "findAllTypes")
    @XmlElementWrapper(name = "types", required = true)
    @XmlElement(name = "type", required = false)
    @WebResult(name = "types")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'all'")
    List<KrmsTypeDefinition> findAllTypes();

    /**
     * Returns all agenda types for the given contextId.
     *
     * @param contextId the id of the context
     * @return all agenda types for the given contextId
     * @throws IllegalArgumentException if the given contextId is a null or blank value
     */
    @WebMethod(operationName = "findAllAgendaTypesByContextId")
    @XmlElementWrapper(name = "agendaTypes", required = true)
    @XmlElement(name = "agendaType", required = false)
    @WebResult(name = "agendaTypes")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'{AgendaType}contextId=' + #p0")
    List<KrmsTypeDefinition> findAllAgendaTypesByContextId(
            @WebParam(name="contextId") String contextId)
        throws RiceIllegalArgumentException;

    /**
     * Return the agenda type by agendaItemId for the given contextId.
     *
     * @param agendaTypeId the id of the agendaType
     * @param contextId the id of the context
     * @return agendaType or null if none is found
     * @throws IllegalArgumentException if the given agendaTypeId or contextId is a null or blank value
     */
    @WebMethod(operationName = "getAgendaTypeByAgendaTypeIdAndContextId")
    @WebResult(name = "type")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'agendaTypeId=' + #p0 + '|' + 'contextId=' + #p1")
    KrmsTypeDefinition getAgendaTypeByAgendaTypeIdAndContextId(
            @WebParam(name="agendaTypeId") String agendaTypeId,
            @WebParam(name="contextId") String contextId)
        throws RiceIllegalArgumentException;

    /**
     * Returns all rule types for the given contextId.
     *
     * @param contextId the id of the context
     * @return all rule types for the given contextId
     * @throws IllegalArgumentException if the given contextId is a null or blank value
     */
    @WebMethod(operationName = "findAllRuleTypesByContextId")
    @XmlElementWrapper(name = "ruleTypes", required = true)
    @XmlElement(name = "ruleType", required = false)
    @WebResult(name = "ruleTypes")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'{RuleType}contextId=' + #p0")
    List<KrmsTypeDefinition> findAllRuleTypesByContextId(
            @WebParam(name="contextId") String contextId)
        throws RiceIllegalArgumentException;

    /**
     * Return the rule type by ruleItemId for the given contextId.
     *
     * @param ruleTypeId the id of the ruleType
     * @param contextId the id of the context
     * @return ruleType or null if none is found
     * @throws IllegalArgumentException if the given ruleTypeId or contextId is a null or blank value
     */
    @WebMethod(operationName = "getRuleTypeByRuleTypeIdAndContextId")
    @WebResult(name = "ruleType")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'ruleTypeId=' + #p0 + '|' + 'contextId=' + #p1")
    KrmsTypeDefinition getRuleTypeByRuleTypeIdAndContextId(
            @WebParam(name="ruleTypeId") String ruleTypeId,
            @WebParam(name="contextId") String contextId)
        throws RiceIllegalArgumentException;

    /**
     * Returns all action types for the given contextId.
     *
     * @param contextId the id of the context
     * @return all action types for the given contextId
     * @throws IllegalArgumentException if the given contextId is a null or blank value
     */
    @WebMethod(operationName = "findAllActionTypesByContextId")
    @XmlElementWrapper(name = "actionTypes", required = true)
    @XmlElement(name = "actionType", required = false)
    @WebResult(name = "actionTypes")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'{ActionType}contextId=' + #p0")
    List<KrmsTypeDefinition> findAllActionTypesByContextId(
            @WebParam(name="contextId") String contextId)
        throws RiceIllegalArgumentException;

    /**
     * Return the action type by actionItemId for the given contextId.
     *
     * @param actionTypeId the id of the actionType
     * @param contextId the id of the context
     * @return actionType or null if none is found
     * @throws IllegalArgumentException if the given actionTypeId or contextId is a null or blank value
     */
    @WebMethod(operationName = "getActionTypeByActionTypeIdAndContextId")
    @WebResult(name = "actionType")
    @Cacheable(value= KrmsTypeDefinition.Cache.NAME, key="'actionTypeId=' + #p0 + '|' + 'contextId=' + #p1")
    KrmsTypeDefinition getActionTypeByActionTypeIdAndContextId(
            @WebParam(name="actionTypeId") String actionTypeId,
            @WebParam(name="contextId") String contextId)
        throws RiceIllegalArgumentException;

    /**
     * Retrieves an attribute definition for the given id.
     *
     * @param attributeDefinitionId the id of the attribute definition to retrieve
     *
     * @return the attribute definition matching the given id, or null if no corresponding attribute definition could
     * be found with the given id value
     * @throws IllegalArgumentException if the given attributeDefinitionId is a null or blank value
     */
    @WebMethod(operationName = "getAttributeDefinitionById")
    @WebResult(name = "attribute")
    @Cacheable(value= KrmsAttributeDefinition.Cache.NAME, key="'attributeDefinitionId=' + #p0")
    KrmsAttributeDefinition getAttributeDefinitionById(@WebParam(name = "attributeDefinitionId") String attributeDefinitionId)
            throws RiceIllegalArgumentException;

    /**
     * Retrieves an attribute definition for the given namespace code and name.
     *
     * @param namespaceCode the namespace under which to locate the attribute definition
     * @param name the name of the attribute definition to retrieve
     *
     * @return the attribute definition matching the give namespace code and name, or null if no corresponding attribute
     * definition could be located
     *
     * @throws RiceIllegalArgumentException if the given namespaceCode or name is a null or blank value
     */
    @WebMethod(operationName = "getAttributeDefinitionByName")
    @WebResult(name = "attribute")
    @Cacheable(value= KrmsAttributeDefinition.Cache.NAME, key="'namespaceCode=' + #p0 + '|' + 'name=' + #p1")
    KrmsAttributeDefinition getAttributeDefinitionByName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name
    ) throws RiceIllegalArgumentException;
}
