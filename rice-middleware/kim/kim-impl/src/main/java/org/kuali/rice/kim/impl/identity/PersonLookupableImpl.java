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
package org.kuali.rice.kim.impl.identity;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.lookup.LookupForm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Custom lookupable for the {@link PersonImpl} lookup to call the person service for searching
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersonLookupableImpl extends LookupableImpl {
    private static final long serialVersionUID = -3149952849854425077L;

    /**
     * Lower cases criteria on principal name and calls the person service to carry out the search
     *
     * @return List<PersonImpl>
     */
    @Override
    protected Collection<?> executeSearch(Map<String, String> adjustedSearchCriteria,
                List<String> wildcardAsLiteralSearchCriteria, boolean bounded, Integer searchResultsLimit) {
        // lower case principal name
        if (adjustedSearchCriteria != null && StringUtils.isNotEmpty(adjustedSearchCriteria.get(
                KIMPropertyConstants.Person.PRINCIPAL_NAME))) {
            adjustedSearchCriteria.put(KIMPropertyConstants.Person.PRINCIPAL_NAME, adjustedSearchCriteria.get(
                    KIMPropertyConstants.Person.PRINCIPAL_NAME).toLowerCase());
        }

        return getPersonService().findPeople(adjustedSearchCriteria, !bounded);
    }

    public PersonService getPersonService() {
        return KimApiServiceLocator.getPersonService();
    }
}
