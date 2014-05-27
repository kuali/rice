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
package org.kuali.rice.krad.demo.uif.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.lookup.LookupForm;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.util.GlobalVariables;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelLookupableImpl extends LookupableImpl {

    /**
     * Add additional validation to check that numeric values are positive
     *
     * @see LookupableImpl#validateSearchParameters(org.kuali.rice.krad.lookup.LookupForm, java.util.Map)
     */
    @Override
    protected boolean validateSearchParameters(LookupForm form, Map<String, String> searchCriteria) {
        boolean valid = super.validateSearchParameters(form, searchCriteria);

        if (form.getViewPostMetadata() != null && form.getViewPostMetadata().getLookupCriteria() != null) {
            for (Map.Entry<String, Map<String, Object>> lookupCriteria : form.getViewPostMetadata().getLookupCriteria().entrySet()) {
                String propertyName = lookupCriteria.getKey();

                validateSearchParameterPositiveValues(form, propertyName, searchCriteria.get(propertyName));
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            valid = false;
        }

        return valid;
    }

    /**
     * Validates that any numeric value is non-negative.
     *
     * @param form lookup form instance containing the lookup data
     * @param propertyName property name of the search criteria field to be validated
     * @param searchPropertyValue value given for field to search for
     */
    protected void validateSearchParameterPositiveValues(LookupForm form, String propertyName, String searchPropertyValue) {
        if (StringUtils.isBlank(searchPropertyValue)) {
            return;
        }

        NumberFormat format = NumberFormat.getInstance();
        Number number = null;
        try {
            number = format.parse(searchPropertyValue);
        } catch (ParseException e) {
            return;
        }

        if (Math.signum(number.doubleValue()) < 0) {
            GlobalVariables.getMessageMap().putError(propertyName,
                        RiceKeyConstants.ERROR_NEGATIVES_NOT_ALLOWED_ON_FIELD, getCriteriaLabel(form, propertyName));
        }
    }
}
