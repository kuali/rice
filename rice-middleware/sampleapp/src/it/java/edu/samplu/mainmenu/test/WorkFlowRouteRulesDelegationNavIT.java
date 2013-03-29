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
package edu.samplu.mainmenu.test;

import org.junit.Test;

/**
 * tests creating and cancelling new and edit Routing Rule Delegation maintenance screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesDelegationNavIT extends MainTmplMthdSTNavBase {

    @Override
    protected String getLinkLocator() {
        return "Routing Rules Delegation";
    }

    /**
     * tests that a Routing Rule Delegation maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditRouteRulesDelegation() throws Exception {
        gotoMenuLinkLocator();
        super.testEditRouteRulesDelegation();
    }
    
    @Test
    public void testCreateNewRRDTravelRequestDestRouting() throws Exception {
        gotoMenuLinkLocator();
        super.testCreateNewRRDTravelRequestDestRouting();
    }
}
