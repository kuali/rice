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
package edu.samplu.mainmenu.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * tests creating and cancelling new and edit Routing Rule maintenance screens 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Routing%20Rules&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?businessObjectClassName=org.kuali.rice.kew.rule.RuleBaseValues&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true&showMaintenanceLinks=true";

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.WebDriverLegacyITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        // TODO dmoteria - THIS METHOD NEEDS JAVADOCS
        return TEST_URL;
    }
    @Test
    /**
     * tests that a new Routing Rule maintenance document can be cancelled
     */
    public void testWorkFlowRouteRulesCreateNew() throws Exception {
       super.testWorkFlowRouteRulesCreateNew();
    }

    @Test
    /**
     * tests that a Routing Rule maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testWorkFlowRouteRulesEditRouteRules() throws Exception {
        super.testWorkFlowRouteRulesEditRouteRules();
    }
   

   
}
