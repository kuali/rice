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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupValidateSearchParamsAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=search&dataObjectClassName=edu.sampleu.travel.dataobject.TravelPerDiemExpense&viewId=LabsLookup-PerDiemExpenseDisabledWildcardsView
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=search&dataObjectClassName=edu.sampleu.travel.dataobject.TravelPerDiemExpense&viewId=LabsLookup-PerDiemExpenseDisabledWildcardsView";

    /**
     *  are treated literally
     */
    private static final String WILDCARD_WARNING_MSG="are treated literally";

    /**
     * Negative values are not allowed on this Breakfast Value field.
     */
    private static final String NEGATIVE_WARNING_MSG="Negative values are not allowed";
    
    /**
     * _)(*
     */
    private static final String WILDCARD_INPUT="_)(*"; 
    
    /**
     * -9_)(*
     */
    private static final String NEGATIVE_WILDCARD_INPUT="-9_)(*"; 
    
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Validate Search Parms");
    }

    @Test
    public void testLabsValidateSearchParamsBookmark() throws Exception {
        testLabsValidateSearchParams();
        passed();
    }

    @Test
    public void testLabsValidateSearchParamsNav() throws Exception {
        testLabsValidateSearchParams();
        passed();
    }

    protected void testLabsValidateSearchParams()throws Exception {
        waitAndTypeByName("lookupCriteria[travelPerDiemExpenseId]",WILDCARD_INPUT);
        assertWarningPresent(Boolean.TRUE,Boolean.FALSE);
        waitAndTypeByName("lookupCriteria[travelAuthorizationDocumentId]",WILDCARD_INPUT);
        assertWarningPresent(Boolean.TRUE,Boolean.FALSE);
        waitAndTypeByName("lookupCriteria[breakfastValue]",NEGATIVE_WILDCARD_INPUT);
        assertWarningPresent(Boolean.TRUE,Boolean.TRUE);
        waitAndTypeByName("lookupCriteria[lunchValue]",NEGATIVE_WILDCARD_INPUT);
        assertWarningPresent(Boolean.TRUE,Boolean.TRUE);
        waitAndTypeByName("lookupCriteria[dinnerValue]",NEGATIVE_WILDCARD_INPUT);
        assertWarningPresent(Boolean.TRUE,Boolean.TRUE);
        waitAndTypeByName("lookupCriteria[incidentalsValue]",NEGATIVE_WILDCARD_INPUT);
        assertWarningPresent(Boolean.TRUE,Boolean.TRUE);
        waitAndTypeByName("lookupCriteria[estimatedMileage]",NEGATIVE_WILDCARD_INPUT);
    }
    
    private void assertWarningPresent(Boolean isWildCardWarningPresent , Boolean isNegativeWarningPresent) throws Exception {
    	waitAndClickSearch3();
    	if(isWildCardWarningPresent){
    		waitForTextPresent(WILDCARD_WARNING_MSG);
    	}
    	if(isNegativeWarningPresent){
    		waitForTextPresent(NEGATIVE_WARNING_MSG);
    	}
    }
}
