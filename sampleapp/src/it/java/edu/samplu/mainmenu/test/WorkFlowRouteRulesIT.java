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
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * tests creating and cancelling new and edit Routing Rule maintenance screens 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Test
    /**
     * tests that a new Routing Rule maintenance document can be cancelled
     */
    public void testCreateNew() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClick("link=Routing Rules");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClick("//img[@alt='create new']");
//        selectFrame("relative=up");
        waitAndClick("methodToCall.cancel", "https://jira.kuali.org/browse/KULRICE-8161 Work Flow Route Rules cancel new yields 404 not found");
        setSpeed("3000");
        // KULRICE-7753 : WorkFlowRouteRulesIT cancel confirmation missing from create new Route Rules.
        waitAndClick("methodToCall.processAnswer.button0", "https://jira.kuali.org/browse/KULRICE-7753 : WorkFlowRouteRulesIT cancel confirmation missing from create new Route Rules.");
    }

    @Test
    /**
     * tests that a Routing Rule maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditRouteRules() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClick("link=Routing Rules");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitAndClick("link=edit");
        waitAndClick("methodToCall.cancel");
        waitAndClick("methodToCall.processAnswer.button0");
    }
}
