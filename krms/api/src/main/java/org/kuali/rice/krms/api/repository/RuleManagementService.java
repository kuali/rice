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
package org.kuali.rice.krms.api.repository;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageUsage;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.Set;

/**
 * The rule maintenance service operations facilitate management of rules and
 * associated information.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "ruleManagementService", targetNamespace = KrmsConstants.Namespaces.KRMS_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
        parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RuleManagementService extends RuleRepositoryService {

    /**
     * Create RefObject-KRMS object binding
     *
     * @param referenceObjectDefinition  data for the new ReferenceObjectBinding to be created
     * @return newly created ReferenceObjectBinding
     * @throws RiceIllegalArgumentException if the given referenceObjectDefinition
     *                                      is null or invalid
     */
    @WebMethod(operationName = "createReferenceObjectBinding")
    @WebResult(name = "referenceObjectBinding")
    public ReferenceObjectBinding createReferenceObjectBinding(@WebParam(
            name = "referenceObjectDefinition") ReferenceObjectBinding referenceObjectDefinition) throws RiceIllegalArgumentException;

    /**
     * Retrieve referenceObjectBinding  given a specific id
     *
     * @param id identifier of the ReferenceObjectBinding to be retrieved
     * @return a ReferenceObjectBinding with the given id value
     * @throws RiceIllegalArgumentException if the given  id is blank or
     *                                      invalid
     */
    @WebMethod(operationName = "getReferenceObjectBinding")
    @WebResult(name = "referenceObjectBinding")
    public ReferenceObjectBinding getReferenceObjectBinding(@WebParam(
            name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Retrieve list of ReferenceObjectBinding objects given ids
     *
     * @param ids identifiers of the ReferenceObjectBinding to be retrieved
     * @return list of ReferenceObjectBinding objects for the given ids
     * @throws RiceIllegalArgumentException if one or more ids in the give list
     *                                      is blank or invalid
     */
    @WebMethod(operationName = "getReferenceObjectBindings")
    @XmlElementWrapper(name = "referenceObjectBindings", required = true)
    @XmlElement(name = "referenceObjectBinding", required = false)
    @WebResult(name = "referenceObjectBindings")
    List<ReferenceObjectBinding> getReferenceObjectBindings(@WebParam(
            name = "ids") List<String> ids) throws RiceIllegalArgumentException;

    /**
     * Retrieves list of ReferenceObjectBinding objects for the given ref obj
     * discriminator type
     *
     * @param referenceObjectReferenceDiscriminatorType  reference object type
     * @return list of ReferenceObjectBinding objects for the given discriminator
     *         type
     * @throws RiceIllegalArgumentException if the given  referenceObjectReferenceDiscriminatorType is
     *                                      blank or invalid
     */
    @WebMethod(operationName = "findReferenceObjectBindingsByReferenceDiscriminatorType")
    @XmlElementWrapper(name = "referenceObjectBindings", required = true)
    @XmlElement(name = "referenceObjectBinding", required = false)
    @WebResult(name = "referenceObjectBindings")
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByReferenceDiscriminatorType(
            @WebParam(name = "referenceObjectReferenceDiscriminatorType") String referenceObjectReferenceDiscriminatorType) throws RiceIllegalArgumentException;

    /**
     * Retrieves list of ReferenceObjectBinding objects for the given krms obj
     * discriminator type
     *
     * @param referenceObjectKrmsDiscriminatorType  reference object type
     * @return list of ReferenceObjectBinding objects for the given discriminator
     *         type
     * @throws RiceIllegalArgumentException if the given  referenceObjectKrmsDiscriminatorType is
     *                                      blank or invalid
     */
    @WebMethod(operationName = "findReferenceObjectBindingsByKrmsDiscriminatorType")
    @XmlElementWrapper(name = "referenceObjectBindings", required = true)
    @XmlElement(name = "referenceObjectBinding", required = false)
    @WebResult(name = "referenceObjectBindings")
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByKrmsDiscriminatorType(
            @WebParam(name = "referenceObjectKrmsDiscriminatorType") String referenceObjectKrmsDiscriminatorType) throws RiceIllegalArgumentException;


    /**
     * Retrieves list of ReferenceObjectBinding objects for the given KRMS obj
     * id.
     *
     * @param krmsObjectId identifier of the KRMS obj
     * @return list of ReferenceObjectBinding objects for the given KRMS obj
     * @throws RiceIllegalArgumentException if the given krmsObjectId is blank or
     *                                      invalid
     */
    @WebMethod(operationName = "findReferenceObjectBindingsByKrmsObjectId")
    @XmlElementWrapper(name = "referenceObjectBindings", required = true)
    @XmlElement(name = "referenceObjectBinding", required = false)
    @WebResult(name = "referenceObjectBindings")
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByKrmsObject(
            @WebParam(name = "krmsObjectId") String krmsObjectId) throws RiceIllegalArgumentException;

    /**
     * Update the ReferenceObjectBinding object specified by the identifier in the
     * given DTO
     *
     * @param referenceObjectBindingDefinition DTO with updated info and id of the object to be updated
     * @throws RiceIllegalArgumentException if the given  referenceObjectBindingDefinition
     *                                      is null or invalid
     */
    @WebMethod(operationName = "updateReferenceObjectBinding")
    public void updateReferenceObjectBinding(ReferenceObjectBinding referenceObjectBindingDefinition) throws RiceIllegalArgumentException;

    /**
     * Delete the specified ReferenceObjectBinding object
     *
     * @param id identifier of the object to be deleted
     * @throws RiceIllegalArgumentException if the given  id is null or invalid
     */
    @WebMethod(operationName = "deleteReferenceObjectBinding")
    public void deleteReferenceObjectBinding(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;


    /**
     * Query for ReferenceObjectBinding ids based on the given search criteria
     * which is a Map of ReferenceObjectBinding field names to values. <p/> <p>
     * This method returns it's results as a List of ReferenceObjectBinding ids
     * that match the given search criteria. </p>
     *
     * @param queryByCriteria the criteria.  Cannot be null.
     * @return a list of ids matching the given criteria properties.  An empty
     *         list is returned if an invalid or non-existent criteria is
     *         supplied.
     * @throws RiceIllegalArgumentException if the queryByCriteria is null
     */
    @WebMethod(operationName = "findReferenceObjectBindingIds")
    @XmlElementWrapper(name = "referenceObjectBindingIds", required = true)
    @XmlElement(name = "referenceObjectBindingId", required = false)
    @WebResult(name = "referenceObjectBindingIds")
    List<String> findReferenceObjectBindingIds(@WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException;

    /**
     * Create Agenda
     *
     * @param agendaDefinition data for the new Agenda to be created
     * @return newly created Agenda
     * @throws RiceIllegalArgumentException if the given agendaDefinition is
     *                                      null or invalid
     */
    @WebMethod(operationName = "createAgenda")
    @WebResult(name = "agenda")
    public AgendaDefinition createAgenda(@WebParam(name = "AgendaDefinition") AgendaDefinition agendaDefinition) throws RiceIllegalArgumentException;

    /**
     * Retrieve Agenda for the specified id
     *
     * @param id identifier for the Agenda
     * @return specified Agenda
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "getAgenda")
    @WebResult(name = "agenda")
    public AgendaDefinition getAgenda(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Retrieve Agendas of the specified type
     *
     * @param typeId type of the Agenda
     * @return list of Agendas of the specified type
     * @throws RiceIllegalArgumentException if the given typeId is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getAgendasByType")
    @XmlElementWrapper(name = "agendas", required = true)
    @XmlElement(name = "agenda", required = false)
    @WebResult(name = "agendas")
    public List<AgendaDefinition> getAgendasByType(@WebParam(name = "typeId") String typeId) throws RiceIllegalArgumentException;

    /**
     * Retrieve Agendas associated with the specified context
     *
     * @param contextId  context of interest
     * @return list of Agendas associated with the context
     * @throws RiceIllegalArgumentException if the given contextId is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getAgendasByContext")
    @XmlElementWrapper(name = "agendas", required = true)
    @XmlElement(name = "agenda", required = false)
    @WebResult(name = "agendas")
    public List<AgendaDefinition> getAgendasByContext(@WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException;

    /**
     * Retrieve Agendas of the specified type and context
     *
     * @param typeId  type of the Agenda
     * @param contextId  context of interest
     * @return list of Agendas associated with the specified type and context
     * @throws RiceIllegalArgumentException if the given typeId or contextId
     *                                      null or invalid
     */
    @WebMethod(operationName = "getAgendasByTypeAndContext")
    @XmlElementWrapper(name = "agendas", required = true)
    @XmlElement(name = "agenda", required = false)
    @WebResult(name = "agendas")
    public List<AgendaDefinition> getAgendasByTypeAndContext(@WebParam(name = "typeId") String typeId,
            @WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException;

    /**
     * Update the Agenda specified by the identifier in the input DTO
     *
     * @param agendaDefinition DTO with updated info and identifier of the object to be updated
     * @throws RiceIllegalArgumentException if the given agendaDefinition is
     *                                      null or invalid
     */
    @WebMethod(operationName = "updateAgenda")
    public void updateAgenda(@WebParam(name = "agendaDefinition") AgendaDefinition agendaDefinition) throws RiceIllegalArgumentException;

    /**
     * Delete the specified Agenda
     *
     * @param id identifier of the object to be deleted
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "deleteAgenda")
    public void deleteAgenda(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Create AgendaItem
     *
     * @param agendaItemDefinition  data for the new AgendaItem to be created
     * @return newly created AgendaItem
     * @throws RiceIllegalArgumentException if the given agendaItemDefinition is
     *                                      null or invalid
     */
    @WebMethod(operationName = "createAgendaItem")
    @WebResult(name = "agendaItem")
    public AgendaItemDefinition createAgendaItem(@WebParam(name = "AgendaItemDefinition") AgendaItemDefinition agendaItemDefinition) throws RiceIllegalArgumentException;

    /**
     * Retrieve AgendaItem by the specified identifier
     *
     * @param id identifier of the AgendaItem
     * @return AgendaItem specified by the identifier
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "getAgendaItem")
    @WebResult(name = "agendaItem")
    public AgendaItemDefinition getAgendaItem(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Retrieve AgendaItems by specified type
     *
     * @param typeId type of the AgendaItems
     * @return list of AgendaItems of the specified type
     * @throws RiceIllegalArgumentException if the given typeId is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getAgendaItemsByType")
    @XmlElementWrapper(name = "agendaItems", required = true)
    @XmlElement(name = "agendaItem", required = false)
    @WebResult(name = "agendaItems")
    public List<AgendaItemDefinition> getAgendaItemsByType(@WebParam(name = "typeId") String typeId) throws RiceIllegalArgumentException;

    /**
     * Retrieve AgendaItems associated with a context
     *
     * @param contextId context identifier
     * @return list of AgendaItems associated with a context
     * @throws RiceIllegalArgumentException if the given  contextId is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getAgendaItemsByContext")
    @XmlElementWrapper(name = "agendaItems", required = true)
    @XmlElement(name = "agendaItem", required = false)
    @WebResult(name = "agendaItems")
    public List<AgendaItemDefinition> getAgendaItemsByContext(@WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException;

    /**
     * Retrieve AgendaItems by type and context
     *
     * @param typeId type of the Agendas
     * @param contextId context with which the Agendas are associated
     * @return list of AgendaItems of the specified type and context
     * @throws RiceIllegalArgumentException if the given  typeId or contextId
     *                                      null or invalid
     */
    @WebMethod(operationName = "getAgendaItemsByTypeAndContext")
    @XmlElementWrapper(name = "agendaItems", required = true)
    @XmlElement(name = "agendaItem", required = false)
    @WebResult(name = "agendaItems")
    public List<AgendaItemDefinition> getAgendaItemsByTypeAndContext(@WebParam(name = "typeId") String typeId,
            @WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException;

    /**
     * Update an AgendaItem
     *
     * @param agendaItemDefinition  updated data for the AgendaItem, with id of the object to be updated
     * @throws RiceIllegalArgumentException if the given  agendaItemDefinition
     *                                      is null or invalid
     */
    @WebMethod(operationName = "updateAgendaItem")
    public void updateAgendaItem(@WebParam(name = "agendaItemDefinition") AgendaItemDefinition agendaItemDefinition) throws RiceIllegalArgumentException;

    /**
     * Delete the specified AgendaItem
     *
     * @param id identifier of the AgendaItem to be deleted
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "deleteAgendaItem")
    public void deleteAgendaItem(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;


    /**
     * Create Rule
     *
     * @param ruleDefinition data for the new Rule to be created
     * @return newly created Rule
     * @throws RiceIllegalArgumentException if the given ruleDefinition is null
     *                                      or invalid
     */
    @WebMethod(operationName = "createRule")
    @WebResult(name = "rule")
    public RuleDefinition createRule(@WebParam(name = "ruleDefinition") RuleDefinition ruleDefinition) throws RiceIllegalArgumentException;

    /**
     * Retrieves the rule for the given ruleId.  The rule includes the
     * propositions which define the condition that is to be evaluated on the
     * rule.  It also defines a collection of actions which will be invoked if
     * the rule succeeds.
     *
     * @param ruleId the id of the rule to retrieve
     * @return the rule definition, or null if no rule could be located for the
     *         given ruleId
     * @throws IllegalArgumentException if the given ruleId is null
     */
    @WebMethod(operationName = "getRule")
    @WebResult(name = "rule")
    @Cacheable(value = RuleDefinition.Cache.NAME, key = "'ruleId=' + #p0")
    public RuleDefinition getRule(@WebParam(name = "ruleId") String ruleId);

    /**
     * Retrieves all of the rules for the given list of ruleIds.  The rule
     * includes the propositions which define the condition that is to be
     * evaluated on the rule.  It also defines a collection of actions which
     * will be invoked if the rule succeeds.
     * <p/>
     * <p>The list which is returned from this operation may not be the same
     * size as the list which is passed to this method.  If a rule doesn't exist
     * for a given rule id then no result for that id will be returned in the
     * list.  As a result of this, the returned list can be empty, but it will
     * never be null.
     *
     * @param ruleIds the list of rule ids for which to retrieve the rules
     * @return the list of rules for the given ids, this list will only contain
     *         rules for the ids that were resolved successfully, it will never
     *         return null but could return an empty list if no rules could be
     *         loaded for the given set of ids
     * @throws IllegalArgumentException if the given list of ruleIds is null
     */
    @WebMethod(operationName = "getRules")
    @XmlElementWrapper(name = "rules", required = true)
    @XmlElement(name = "rule", required = false)
    @WebResult(name = "rules")
    public List<RuleDefinition> getRules(@WebParam(name = "ruleIds") List<String> ruleIds);

    /**
     * Update the Rule specified by the identifier in the DTO
     *
     * @param ruleDefinition updated Rule information, object specified by the id
     * @throws RiceIllegalArgumentException if the given ruleDefinition is null
     *                                      or invalid
     */
    @WebMethod(operationName = "updateRule")
    public void updateRule(@WebParam(name = "ruleDefinition") RuleDefinition ruleDefinition) throws RiceIllegalArgumentException;

    /**
     * Delete the specified Rule
     *
     * @param id identifier of the Rule to be deleted
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "deleteRule")
    public void deleteRule(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Create a Proposition
     *
     * @param propositionDefinition data for the new Proposition to be created
     * @return newly created Proposition
     * @throws RiceIllegalArgumentException if the given propositionDefinition
     *                                      is null or invalid
     */
    @WebMethod(operationName = "createProposition")
    @WebResult(name = "proposition")
    public PropositionDefinition createProposition(@WebParam(name = "propositionDefinition") PropositionDefinition propositionDefinition) throws RiceIllegalArgumentException;

    /**
     * Retrieve Proposition specified by the identifier
     *
     * @param id identifier of the Proposition to be retrieved
     * @return specified Proposition
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "getProposition")
    @WebResult(name = "proposition")
    public PropositionDefinition getProposition(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Retrieve Propositions of the specified type
     *
     * @param typeId type of the Propositions to be retrieved
     * @return list of Propositions of the specified type
     * @throws RiceIllegalArgumentException if the given typeId is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getPropositionsByType")
    @XmlElementWrapper(name = "propositions", required = true)
    @XmlElement(name = "proposition", required = false)
    @WebResult(name = "propositions")
    public Set<PropositionDefinition> getPropositionsByType(@WebParam(name = "typeId") String typeId) throws RiceIllegalArgumentException;

    /**
     * Retrieve Propositions associated with the specified Rule
     *
     * @param ruleId identifier of the Rule to which the Propositions are associated with
     * @return list of Propositions associated with the Rule
     * @throws RiceIllegalArgumentException if the given ruleId is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getPropositionsByRule")
    @XmlElementWrapper(name = "propositions", required = true)
    @XmlElement(name = "proposition", required = false)
    @WebResult(name = "propositions")
    public Set<PropositionDefinition> getPropositionsByRule(@WebParam(name = "ruleId") String ruleId) throws RiceIllegalArgumentException;

    /**
     * Update the Proposition
     *
     * @param propositionDefinition updated data for the Proposition, id specifies the object to be updated
     * @throws RiceIllegalArgumentException if the given propositionDefinition
     *                                      is null or invalid
     */
    @WebMethod(operationName = "updateProposition")
    public void updateProposition(
            @WebParam(name = "propositionDefinition") PropositionDefinition propositionDefinition) throws RiceIllegalArgumentException;

    /**
     * Delete the Proposition
     *
     * @param id identifier of the Proposition to be deleted
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "deleteProposition")
    public void deleteProposition(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Create NaturalLanguageUsage
     *
     * @param naturalLanguageUsage data for the new NaturalLanguageUsage to be created
     * @return newly created NaturalLanguageUsage
     * @throws RiceIllegalArgumentException if the given naturalLanguageUsage is
     *                                      null or invalid
     */
    @WebMethod(operationName = "createNaturalLanguageUsage")
    @WebResult(name = "naturalLanguageUsage")
    public NaturalLanguageUsage createNaturalLanguageUsage(@WebParam(name = "naturalLanguageUsage") NaturalLanguageUsage naturalLanguageUsage) throws RiceIllegalArgumentException;

    /**
     * Retrieve NaturalLanguageUsage specified by the identifier
     *
     * @param id identifier of the NaturalLanguageUsage to be retrieved
     * @return NaturalLanguageUsage specified by the identifier
     * @throws RiceIllegalArgumentException if the given id is null or invalid
     */
    @WebMethod(operationName = "getNaturalLanguageUsage")
    @WebResult(name = "naturalLanguageUsage")
    public NaturalLanguageUsage getNaturalLanguageUsage(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    /**
     * Update NaturalLanguageUsage
     *
     * @param naturalLanguageUsage updated data for the NaturalLanguageUsage object specified by the id
     * @throws RiceIllegalArgumentException if the given naturalLanguageUsage is
     *                                      null or invalid
     */
    @WebMethod(operationName = "updateNaturalLanguageUsage")
    public void updateNaturalLanguageUsage(@WebParam(name = "naturalLanguageUsage") NaturalLanguageUsage naturalLanguageUsage) throws RiceIllegalArgumentException;

    /**
     * Delete NaturalLanguageUsage
     *
     * @param naturalLanguageUsageId  identifier of the NaturalLanguageUsage to be deleted
     * @throws RiceIllegalArgumentException  if the given naturalLanguageUsageId is null or invalid
     */
    @WebMethod(operationName = "deleteNaturalLanguageUsageType")
    public void deleteNaturalLanguageUsageType(@WebParam(name = "naturalLanguageUsageId") String naturalLanguageUsageId) throws RiceIllegalArgumentException;

    /**
     * Translates and retrieves a NaturalLanguage for a given KRMS object (e.g, proposition
     * or agenda), NaturalLanguage usage type (context) and language into natural language
     * TODO: Add appropriate caching annotation
     *
     * @param naturalLanguageUsageId Natural language usage information
     * @param typeId    KRMS object type id (for example, could refer to agenda
     *                  or proposition)
     * @param krmsObjectId KRMS object identifier
     * @param languageCode  desired
     * @return natural language corresponding to the NaturalLanguage usage, KRMS object id, KRMS object type and desired language
     * @throws RiceIllegalArgumentException if the given naturalLanguageUsageId, typeId,
     *                                      krmsObjectId or language is null or
     *                                      invalid
     */
    @WebMethod(operationName = "getNaturalLanguageForType")
    @WebResult(name = "naturalLanguage")
    public String getNaturalLanguageForType(@WebParam(name = "naturalLanguageUsageId") String naturalLanguageUsageId, @WebParam(name = "typeId") String typeId, @WebParam(name = "krmsObjectId") String krmsObjectId,
            @WebParam(name = "languageCode") String languageCode) throws RiceIllegalArgumentException;

    /**
     * Retrieve all the NaturalLanguageUsages
     *
     * @return list of NaturalLanguageUsages
     */
    @WebMethod(operationName = "getNaturalLanguageUsages")
    @XmlElementWrapper(name = "naturalLanguageUsages", required = true)
    @XmlElement(name = "naturalLanguageUsage", required = false)
    @WebResult(name = "naturalLanguageUsages")
    public Set<NaturalLanguageUsage> getNaturalLanguageUsages();
}
