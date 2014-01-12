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
package edu.sampleu.main;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesBkMrkAft extends WorkFlowRouteRulesAftBase {

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testWorkFlowRouteRulesBookmark() throws Exception {
        testWorkFlowRouteRulesBookmark(this);
    }

    @Test
    /**
     * tests that a new Routing Rule maintenance document can be cancelled
     */
    public void testWorkFlowRouteRulesCreateNewBookmark() throws Exception {
        testWorkFlowRouteRulesCreateNew();
    }

    @Test
    /**
     * tests that a Routing Rule maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testWorkFlowRouteRulesEditRouteRulesBookmark() throws Exception {
        testWorkFlowRouteRulesEditRouteRules();
    }
}
