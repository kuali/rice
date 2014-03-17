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

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.demo.travel.dataobject.TravelAccount;
import org.kuali.rice.krad.lookup.LookupForm;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.util.ArrayList;
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
        Collection<org.kuali.rice.krad.demo.travel.dataobject.TravelAccount> results =
                (Collection<org.kuali.rice.krad.demo.travel.dataobject.TravelAccount>) super.performSearch(form,
                        searchCriteria, bounded);

        // get additional parameter
        String minSubsidized = form.getInitialRequestParameters().get("minSubsidized")[0];

        // filter results
        Collection<TravelAccount> finalResults = new ArrayList();
        for (TravelAccount travelAccount : results) {
            if (travelAccount.getSubsidizedPercent() != null && travelAccount.getSubsidizedPercent().isGreaterEqual(
                    new KualiDecimal(minSubsidized))) {
                finalResults.add(travelAccount);
            }
        }

        // over write the default search message that was set
        MessageMap messageMap = GlobalVariables.getMessageMap();
        messageMap.getInfoMessages().remove(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES);
        messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                RiceKeyConstants.INFO_LOOKUP_RESULTS_DISPLAY_ALL, finalResults.size() + "");

        // NOTE: an alternative to the above is to add the new value as a part of the searchCriteria passed in
        // then database can filter results and would not have to over write default search message
        // however KualiPercent is broke at time of this example
        //searchCriteria.put("subsidizedPercent", ">=" + minSubsidized);

        return finalResults;
    }

}
