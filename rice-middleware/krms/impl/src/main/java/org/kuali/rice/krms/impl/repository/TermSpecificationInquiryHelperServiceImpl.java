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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.Map;

/**
 * Helper service to assist with fetching TermSpecificationBos that have all the properties needed for display
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermSpecificationInquiryHelperServiceImpl extends KualiInquirableImpl {

    private DataObjectService dataObjectService;

    /**
     * Fetches the TermSpecificationBo for the given id, and manually adds ContextBos for display
     *
     * @param fieldValues the search fields
     * @return the TermSpecificationBo with any contexts mapped by contextValidTerms added
     */
    @Override
    public TermSpecificationBo retrieveDataObject(Map fieldValues) {
        TermSpecificationBo termSpecification = getDataObjectService().find(TermSpecificationBo.class, fieldValues.get("id"));

        if (termSpecification == null) { return null; }

        for (ContextValidTermBo contextValidTerm : termSpecification.getContextValidTerms()) {
            ContextBo context = getDataObjectService().find(ContextBo.class, contextValidTerm.getContextId());

            if (context != null) {
                termSpecification.getContextIds().add(context.getId());
                termSpecification.getContexts().add(context);
            }
        }

        return termSpecification;
    }

    public DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            return KRADServiceLocator.getDataObjectService();
        }
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
