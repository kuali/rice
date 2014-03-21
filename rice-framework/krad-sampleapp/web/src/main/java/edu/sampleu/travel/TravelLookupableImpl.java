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
package edu.sampleu.travel;

import org.kuali.rice.krad.lookup.LookupForm;
import org.kuali.rice.krad.lookup.LookupableImpl;
import java.util.Collection;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelLookupableImpl extends LookupableImpl {

    /**
     * Override the performSearch method so that we can perform any additional filtering of results from the standard query.
     * Here we are getting the additional parameters being passed in and filtering on minSubsidized value.
     *
     * @{inheritDoc}
     */
    @Override
    public Collection<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean bounded) {
        // get additional parameter
        String minSubsidized = form.getInitialRequestParameters().get("minSubsidized")[0];

        searchCriteria.put("subsidizedPercent", ">=" + minSubsidized);

        return super.performSearch(form, searchCriteria,bounded);
    }

}
