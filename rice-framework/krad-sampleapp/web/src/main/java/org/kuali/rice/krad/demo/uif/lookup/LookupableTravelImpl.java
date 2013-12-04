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
package org.kuali.rice.krad.demo.uif.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.lookup.LookupForm;
import org.kuali.rice.krad.lookup.LookupView;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.util.GlobalVariables;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupableTravelImpl extends LookupableImpl {

    @Override
    protected boolean validateSearchParameters(LookupForm form, Map<String, String> searchCriteria) {
        boolean valid = true;

        // if postedView is null then we are executing the search from get request, in which case we
        // can't validate the criteria
        if (form.getActiveView() == null) {
            return valid;
        }

        Map<String, InputField> criteriaFields = getCriteriaFieldsForValidation((LookupView) form.getActiveView(),
                form);

        // TODO: this should be an error condition but we have an issue when the search is performed from
        // the initial request and there is not a posted view
        if ((criteriaFields == null) || criteriaFields.isEmpty()) {
            return valid;
        }

        // build list of hidden properties configured with criteria fields so they are excluded from validation
        List<String> hiddenCriteria = new ArrayList<String>();
        for (InputField field : criteriaFields.values()) {
            if (field.getAdditionalHiddenPropertyNames() != null) {
                hiddenCriteria.addAll(field.getAdditionalHiddenPropertyNames());
            }
        }

        for (Map.Entry<String, String> searchKeyValue : searchCriteria.entrySet()) {
            String searchPropertyName = searchKeyValue.getKey();
            String searchPropertyValue = searchKeyValue.getValue();

            InputField inputField = criteriaFields.get(searchPropertyName);

            String adjustedSearchPropertyPath = UifPropertyPaths.LOOKUP_CRITERIA + "[" + searchPropertyName + "]";
            if (inputField == null && hiddenCriteria.contains(adjustedSearchPropertyPath)) {
                return valid;
            }

            // if there is not an input field, then this is invalid search criteria
            if (inputField == null) {
                throw new RuntimeException("Invalid search value sent for property name: " + searchPropertyName);
            }

            if (StringUtils.isBlank(searchPropertyValue) && inputField.getRequired()) {
                GlobalVariables.getMessageMap().putError(inputField.getPropertyName(), RiceKeyConstants.ERROR_REQUIRED,
                        inputField.getLabel());
            }

            validateSearchParameterWildcardAndOperators(inputField, searchPropertyValue);
            validateSearchParameterPositiveValues(inputField, searchPropertyValue);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            valid = false;
        }

        return valid;
    }

    /**
     * Validates that any wildcards contained within the search value are valid wildcards and allowed for the
     * property type for which the field is searching.
     *
     * @param inputField attribute field instance for the field that is being searched
     * @param searchPropertyValue value given for field to search for
     */
    protected void validateSearchParameterPositiveValues(InputField inputField, String searchPropertyValue) {
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
            String attributeLabel = inputField.getLabel();
            GlobalVariables.getMessageMap().putError(inputField.getPropertyName(),
                        RiceKeyConstants.ERROR_NEGATIVES_NOT_ALLOWED_ON_FIELD, attributeLabel);
        }
    }
}
