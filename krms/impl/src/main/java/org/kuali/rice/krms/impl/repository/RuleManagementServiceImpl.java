/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.RuleManagementService;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageUsage;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.in;

/**
 * The implementation of {@link RuleManagementService} operations facilitate management of rules and
 * associated information.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleManagementServiceImpl extends RuleRepositoryServiceImpl implements RuleManagementService {

    ReferenceObjectBindingBoServiceImpl referenceObjectBindingBoServiceImpl = new ReferenceObjectBindingBoServiceImpl();
    AgendaBoServiceImpl agendaBoServiceImpl = new AgendaBoServiceImpl();

    RuleBoServiceImpl ruleBoServiceImpl = new RuleBoServiceImpl();
    PropositionBoServiceImpl propositionBoServiceImpl = new PropositionBoServiceImpl();
    NaturalLanguageUsageBoServiceImpl naturalLanguageUsageBoService = new NaturalLanguageUsageBoServiceImpl();
    NaturalLanguageTemplateBoServiceImpl naturalLanguageTemplateBoService = new NaturalLanguageTemplateBoServiceImpl();

    public void setReferenceObjectBindingBoServiceImpl(
            ReferenceObjectBindingBoServiceImpl referenceObjectBindingBoServiceImpl) {
        this.referenceObjectBindingBoServiceImpl = referenceObjectBindingBoServiceImpl;
    }

    public void setAgendaBoServiceImpl(AgendaBoServiceImpl agendaBoServiceImpl) {
        this.agendaBoServiceImpl = agendaBoServiceImpl;
    }

    public void setRuleBoServiceImpl(RuleBoServiceImpl ruleBoServiceImpl) {
        this.ruleBoServiceImpl = ruleBoServiceImpl;
    }

    public void setPropositionBoServiceImpl(PropositionBoServiceImpl propositionBoServiceImpl) {
        this.propositionBoServiceImpl = propositionBoServiceImpl;
    }

    public void setNaturalLanguageUsageBoService(NaturalLanguageUsageBoServiceImpl naturalLanguageUsageBoService) {
        this.naturalLanguageUsageBoService = naturalLanguageUsageBoService;
    }

    public void setNaturalLanguageTemplateBoService(
            NaturalLanguageTemplateBoServiceImpl naturalLanguageTemplateBoService) {
        this.naturalLanguageTemplateBoService = naturalLanguageTemplateBoService;
    }

    @Override
    public ReferenceObjectBinding createReferenceObjectBinding(@WebParam(
            name = "referenceObjectDefinition") ReferenceObjectBinding referenceObjectDefinition) throws RiceIllegalArgumentException {
        return referenceObjectBindingBoServiceImpl.createReferenceObjectBinding(referenceObjectDefinition);
    }

    @Override
    public ReferenceObjectBinding getReferenceObjectBinding(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        return referenceObjectBindingBoServiceImpl.getReferenceObjectBinding(id);
    }

    @Override
    public List<ReferenceObjectBinding> getReferenceObjectBindings(
            @WebParam(name = "ids") List<String> ids) throws RiceIllegalArgumentException {
        if (ids == null) throw new IllegalArgumentException("reference binding object ids must not be null");

        // Fetch BOs
        List<ReferenceObjectBindingBo> bos = null;
        if (ids.size() == 0) {
            bos = Collections.emptyList();
        } else {
            QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
            List<Predicate> pList = new ArrayList<Predicate>();
            qBuilder.setPredicates(in("id", ids.toArray()));
            GenericQueryResults<ReferenceObjectBindingBo> results = getCriteriaLookupService().lookup(ReferenceObjectBindingBo.class, qBuilder.build());

            bos = results.getResults();
        }

        // Translate BOs
        List<ReferenceObjectBinding> bindings = new LinkedList<ReferenceObjectBinding>();
        for (ReferenceObjectBindingBo bo : bos) {
            ReferenceObjectBinding binding = ReferenceObjectBindingBo.to(bo);
            bindings.add(binding);
        }
        return Collections.unmodifiableList(bindings);
    }

//    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByReferenceDiscriminatorType(@WebParam(
            name = "referenceObjectReferenceDiscriminatorType") String referenceObjectReferenceDiscriminatorType) throws RiceIllegalArgumentException {
        return referenceObjectBindingBoServiceImpl.findReferenceObjectBindingsByReferenceDiscriminatorType(referenceObjectReferenceDiscriminatorType);
    }

//    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByKrmsDiscriminatorType(@WebParam(
            name = "referenceObjectKrmsDiscriminatorType") String referenceObjectKrmsDiscriminatorType) throws RiceIllegalArgumentException {
        return referenceObjectBindingBoServiceImpl.findReferenceObjectBindingsByKrmsDiscriminatorType(referenceObjectKrmsDiscriminatorType);
    }

//    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByKrmsObject(
            @WebParam(name = "krmsObjectId") String krmsObjectId) throws RiceIllegalArgumentException {
        return referenceObjectBindingBoServiceImpl.findReferenceObjectBindingsByKrmsObject(krmsObjectId);
    }

    @Override
    public void updateReferenceObjectBinding(
            ReferenceObjectBinding referenceObjectBindingDefinition) throws RiceIllegalArgumentException {
        referenceObjectBindingBoServiceImpl.updateReferenceObjectBinding(referenceObjectBindingDefinition);
    }

    @Override
    public void deleteReferenceObjectBinding(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        referenceObjectBindingBoServiceImpl.deleteReferenceObjectBinding(id);
    }

    @Override
    public List<String> findReferenceObjectBindingIds(
            @WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        return referenceObjectBindingBoServiceImpl.findReferenceObjectBindingIds(queryByCriteria);
    }

    @Override
    public AgendaDefinition createAgenda(@WebParam(
            name = "AgendaDefinition") AgendaDefinition agendaDefinition) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.createAgenda(agendaDefinition);
    }

    @Override
    public AgendaDefinition getAgenda(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendaByAgendaId(id);
    }

    @Override
    public List<AgendaDefinition> getAgendasByContext(
            @WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendasByContextId(contextId);
    }

    @Override
    public void updateAgenda(@WebParam(
            name = "agendaDefinition") AgendaDefinition agendaDefinition) throws RiceIllegalArgumentException {
        agendaBoServiceImpl.updateAgenda(agendaDefinition);
    }

    @Override
    public void deleteAgenda(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        agendaBoServiceImpl.deleteAgenda(id);
    }

    @Override
    public AgendaItemDefinition createAgendaItem(@WebParam(
            name = "AgendaItemDefinition") AgendaItemDefinition agendaItemDefinition) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.createAgendaItem(agendaItemDefinition);
    }

    @Override
    public AgendaItemDefinition getAgendaItem(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendaItemById(id);
    }

    @Override
    public List<AgendaDefinition> getAgendasByType(
            @WebParam(name = "typeId") String typeId) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendasByType(typeId);
    }

    @Override
    public List<AgendaDefinition> getAgendasByTypeAndContext(@WebParam(name = "typeId") String typeId,
            @WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendasByTypeAndContext(typeId, contextId);
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByType(
            @WebParam(name = "typeId") String typeId) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendaItemsByType(typeId);
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByContext(
            @WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendaItemsByContext(contextId);
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByTypeAndContext(@WebParam(name = "typeId") String typeId,
            @WebParam(name = "contextId") String contextId) throws RiceIllegalArgumentException {
        return agendaBoServiceImpl.getAgendaItemsByTypeAndContext(typeId, contextId);
    }

    @Override
    public void deleteAgendaItem(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        agendaBoServiceImpl.deleteAgendaItem(id);
    }

    @Override
    public void updateAgendaItem(@WebParam(
            name = "agendaItemDefinition") AgendaItemDefinition agendaItemDefinition) throws RiceIllegalArgumentException {
        agendaBoServiceImpl.updateAgendaItem(agendaItemDefinition);
    }

    @Override
    public RuleDefinition createRule(
            @WebParam(name = "ruleDefinition") RuleDefinition ruleDefinition) throws RiceIllegalArgumentException {
        return ruleBoServiceImpl.createRule(ruleDefinition);
    }

    @Override
    public void updateRule(
            @WebParam(name = "ruleDefinition") RuleDefinition ruleDefinition) throws RiceIllegalArgumentException {
        ruleBoServiceImpl.updateRule(ruleDefinition);
    }

    @Override
    public void deleteRule(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        ruleBoServiceImpl.deleteRule(id);
    }

    @Override
    public PropositionDefinition createProposition(@WebParam(
            name = "propositionDefinition") PropositionDefinition propositionDefinition) throws RiceIllegalArgumentException {
        return propositionBoServiceImpl.createProposition(propositionDefinition);
    }

    @Override
    public PropositionDefinition getProposition(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        return propositionBoServiceImpl.getPropositionById(id);
    }

    @Override
    public Set<PropositionDefinition> getPropositionsByType(
            @WebParam(name = "typeId") String typeId) throws RiceIllegalArgumentException {
        return propositionBoServiceImpl.getPropositionsByType(typeId);
    }

    @Override
    public Set<PropositionDefinition> getPropositionsByRule(
            @WebParam(name = "ruleId") String ruleId) throws RiceIllegalArgumentException {
        return propositionBoServiceImpl.getPropositionsByRule(ruleId);
    }

    @Override
    public void updateProposition(@WebParam(
            name = "propositionDefinition") PropositionDefinition propositionDefinition) throws RiceIllegalArgumentException {
        propositionBoServiceImpl.updateProposition(propositionDefinition);
    }

    @Override
    public void deleteProposition(@WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        propositionBoServiceImpl.deleteProposition(id);
    }

    @Override
    public NaturalLanguageUsage createNaturalLanguageUsage(@WebParam(
            name = "naturalLanguageUsage") NaturalLanguageUsage naturalLanguageUsage) throws RiceIllegalArgumentException {
        return naturalLanguageUsageBoService.createNaturalLanguageUsage(naturalLanguageUsage);
    }

    @Override
    public NaturalLanguageUsage getNaturalLanguageUsage(
            @WebParam(name = "id") String id) throws RiceIllegalArgumentException {
        return naturalLanguageUsageBoService.getNaturalLanguageUsage(id);
    }

    @Override
    public void updateNaturalLanguageUsage(@WebParam(
            name = "naturalLanguageUsage") NaturalLanguageUsage naturalLanguageUsage) throws RiceIllegalArgumentException {
        naturalLanguageUsageBoService.updateNaturalLanguageUsage(naturalLanguageUsage);
    }

    @Override
    public void deleteNaturalLanguageUsageType(@WebParam(
            name = "naturalLanguageUsageId") String naturalLanguageUsageId) throws RiceIllegalArgumentException {
        naturalLanguageUsageBoService.deleteNaturalLanguageUsage(naturalLanguageUsageId);
    }

    @Override
    public String getNaturalLanguageForType(@WebParam(name = "naturalLanguageUsageId") String naturalLanguageUsageId,
            @WebParam(name = "typeId") String typeId, @WebParam(name = "krmsObjectId") String krmsObjectId,
            @WebParam(name = "languageCode") String languageCode) throws RiceIllegalArgumentException {
        NaturalLanguageTemplate naturalLanguageTemplate = naturalLanguageTemplateBoService.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(languageCode, typeId, naturalLanguageUsageId);
        return naturalLanguageTemplateBoService.template(naturalLanguageTemplate);
    }

    @Override
    public Set<NaturalLanguageUsage> getNaturalLanguageUsages() {
        return null;  //TODO EGHM ConextDefition
    }


    /**
     * Sets the businessObjectService property.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    @Override
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        super.setBusinessObjectService(businessObjectService);
        referenceObjectBindingBoServiceImpl.setBusinessObjectService(businessObjectService);
        agendaBoServiceImpl.setBusinessObjectService(businessObjectService);
        ruleBoServiceImpl.setBusinessObjectService(businessObjectService);
        propositionBoServiceImpl.setBusinessObjectService(businessObjectService);
        naturalLanguageUsageBoService.setBusinessObjectService(businessObjectService);
        naturalLanguageTemplateBoService.setBusinessObjectService(businessObjectService);
    }
}
