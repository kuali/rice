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
package org.kuali.rice.krms.impl.ui;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krms.impl.repository.CategoryBo;
import org.kuali.rice.krms.impl.repository.ContextValidTermBo;
import org.kuali.rice.krms.impl.repository.PropositionBo;
import org.kuali.rice.krms.impl.repository.TermBo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ValidTermsValuesFinder extends UifKeyValuesFinderBase {

    @Override
    public List<KeyValue> getKeyValues(ViewModel model) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        MaintenanceForm maintenanceForm = (MaintenanceForm) model;
        AgendaEditor agendaEditor = ((AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());
        String contextId = agendaEditor.getAgenda().getContextId();

        String selectedPropId = agendaEditor.getSelectedPropositionId();

        PropositionBo rootProposition = agendaEditor.getAgendaItemLine().getRule().getProposition();
        PropositionBo editModeProposition = findPropositionUnderEdit(rootProposition);
        String selectedCategoryId = (editModeProposition != null) ? editModeProposition.getCategoryId() : null;

        // Get all valid terms

        Collection<ContextValidTermBo> contextValidTerms = null;
        contextValidTerms = KRADServiceLocator.getBusinessObjectService()
                .findMatching(ContextValidTermBo.class, Collections.singletonMap("contextId", contextId));

        List<String> termSpecIds = new ArrayList();
        for (ContextValidTermBo validTerm : contextValidTerms) {
            termSpecIds.add(validTerm.getTermSpecificationId());
        }

        Collection<TermBo> terms = null;
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("specificationId", termSpecIds);
        terms = KRADServiceLocator.getBusinessObjectService().findMatchingOrderBy(TermBo.class, criteria, "description", true);

        // add all terms that are in the selected category (or else add 'em all if no category is selected)
        for (TermBo term : terms) {
            String selectName = term.getDescription();

            if (StringUtils.isBlank(selectName) || "null".equals(selectName)) {
                selectName = term.getSpecification().getName();
            }

            if (!StringUtils.isBlank(selectedCategoryId)) {
                // only add if the term has the selected category
                if (term.getSpecification().getCategories() != null) {
                    for (CategoryBo category : term.getSpecification().getCategories()) {
                        if (selectedCategoryId.equals(category.getId())) {
                            keyValues.add(new ConcreteKeyValue(term.getId(), selectName));
                            break;
                        }
                    }
                }
            } else {
                keyValues.add(new ConcreteKeyValue(term.getId(), selectName));
            }
        }

        return keyValues;
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