/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.CategoryBo;
import org.kuali.rice.krms.impl.repository.ContextValidTermBo;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.PropositionBo;
import org.kuali.rice.krms.impl.repository.TermBo;
import org.kuali.rice.krms.impl.repository.TermResolverBo;
import org.kuali.rice.krms.impl.repository.TermSpecificationBo;
import org.kuali.rice.krms.impl.util.KrmsImplConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ValuesFinder used to populate the list of available Terms when creating/editing a proposition in a
 * KRMS Rule.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidTermsForPropositionValuesFinder extends UifKeyValuesFinderBase {

    /**
     * get the value list for the Term dropdown in the KRMS rule editing UI
     * @param model
     * @return
     */
    @Override
    public List<KeyValue> getKeyValues(ViewModel model) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) model;
        PropositionBo rootProposition = ((PropositionBo) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());

        PropositionBo editModeProposition = findPropositionUnderEdit(rootProposition);
        String selectedCategoryId = (editModeProposition != null) ? editModeProposition.getCategoryId() : null;

        // Get all valid terms

        List<ContextValidTermBo> contextValidTerms = getContextValidTerms(rootProposition.getRuleId());

        List<String> termSpecIds = new ArrayList();

        for (ContextValidTermBo validTerm : contextValidTerms) {
            termSpecIds.add(validTerm.getTermSpecificationId());
        }

        if (termSpecIds.size() > 0) { // if we don't have any valid terms, skip it
            QueryResults<TermBo> terms = null;
            Map<String,Object> critMap = new HashMap<String,Object>();
            critMap.put("specificationId", termSpecIds);

            QueryByCriteria criteria =
                    QueryByCriteria.Builder.forAttribute("specificationId", termSpecIds).setOrderByAscending("description").build();

            terms = KRADServiceLocator.getDataObjectService().findMatching(TermBo.class, criteria);

            // add all terms that are in the selected category (or else add 'em all if no category is selected)
            if (!CollectionUtils.isEmpty(terms.getResults())) for (TermBo term : terms.getResults()) {
                String selectName = term.getDescription();

                if (StringUtils.isBlank(selectName) || "null".equals(selectName)) {
                    selectName = term.getSpecification().getName();
                }

                if (!StringUtils.isBlank(selectedCategoryId)) {
                    // only add if the term has the selected category
                    if (isTermSpecificationInCategory(term.getSpecification(), selectedCategoryId)) {
                        keyValues.add(new ConcreteKeyValue(term.getId(), selectName));
                    }
                } else {
                    keyValues.add(new ConcreteKeyValue(term.getId(), selectName));
                }
            }

            //
            // Add Parameterized Term Specs
            //

            // get term resolvers for the given term specs
            QueryByCriteria.Builder termResolverCritBuilder = QueryByCriteria.Builder.forAttribute("outputId", termSpecIds);
            termResolverCritBuilder.setOrderByAscending("name");
            QueryResults<TermResolverBo> termResolvers =
                    KRADServiceLocator.getDataObjectService().findMatching(TermResolverBo.class, termResolverCritBuilder.build());

            // TODO: what if there is more than one resolver for a given term specification?

            if (termResolvers.getResults() != null) for (TermResolverBo termResolver : termResolvers.getResults()) {
                if (!CollectionUtils.isEmpty(termResolver.getParameterSpecifications())) {
                    TermSpecificationBo output = termResolver.getOutput();

                    // filter by category
                    if (StringUtils.isBlank(selectedCategoryId) ||
                            isTermSpecificationInCategory(output, selectedCategoryId)) {

                        // we use a special prefix to differentiate these, as they are term spec ids instead of term ids.
                        keyValues.add(new ConcreteKeyValue(KrmsImplConstants.PARAMETERIZED_TERM_PREFIX
                                + output.getId(), output.getName()
                                // build a string that indicates the number of parameters
                                + "(" + StringUtils.repeat("_", ",", termResolver.getParameterSpecifications().size()) +")"));
                    }
                }
            }
        }

        return keyValues;
    }

    /**
     * Get all of the valid terms for the Context that we're in.  This is a bit of a process since we're starting
     * from the proposition and there is a lot of indirection to get the context ID.
     *
     * @param ruleId
     * @return the mappings from the context(s) to the valid terms
     */
    private List<ContextValidTermBo> getContextValidTerms(String ruleId) {
        RuleDefinition rule = KrmsRepositoryServiceLocator
            .getRuleBoService().getRuleByRuleId(ruleId);

        QueryByCriteria agendaItemCriteria = QueryByCriteria.Builder.forAttribute("ruleId", rule.getId()).build();
        QueryResults<AgendaItemBo> agendaItems =
                KRADServiceLocator.getDataObjectService().findMatching(AgendaItemBo.class, agendaItemCriteria);

        Set<String> agendaIds = new HashSet<String>();
        if (!CollectionUtils.isEmpty(agendaItems.getResults())) for (AgendaItemBo agendaItem : agendaItems.getResults()) {
            agendaIds.add(agendaItem.getAgendaId());
        }

        Set<String> contextIds = new HashSet<String>();
        for (String agendaId : agendaIds) {
            AgendaDefinition agenda = KrmsRepositoryServiceLocator.getAgendaBoService().getAgendaByAgendaId(agendaId);

            if (agenda != null) {
                contextIds.add(agenda.getContextId());
            }
        }

        List<ContextValidTermBo> contextValidTerms = new ArrayList<ContextValidTermBo>();

        for (String contextId : contextIds) {
            QueryResults<ContextValidTermBo> queryResults =
                    KRADServiceLocator.getDataObjectService().findMatching(ContextValidTermBo.class,
                            QueryByCriteria.Builder.forAttribute("contextId", contextId).build());

            if (!CollectionUtils.isEmpty(queryResults.getResults())) {
                contextValidTerms.addAll(queryResults.getResults());
            }
        }
        return contextValidTerms;
    }

    /**
     * @return true if the term specification is in the given category
     */
    private boolean isTermSpecificationInCategory(TermSpecificationBo termSpec, String categoryId) {
        if (termSpec.getCategories() != null) {
            for (CategoryBo category : termSpec.getCategories()) {
                if (categoryId.equals(category.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * helper method to find the proposition under edit
     */
    private PropositionBo findPropositionUnderEdit(PropositionBo currentProposition) {
        PropositionBo result = null;
        if (currentProposition.getEditMode()) {
            result = currentProposition;
        } else {
            if (currentProposition.getCompoundComponents() != null) {
                for (PropositionBo child : currentProposition.getCompoundComponents()) {
                    result = findPropositionUnderEdit(child);
                    if (result != null) break;
                }
            }
        }
        return result;
    }

}
