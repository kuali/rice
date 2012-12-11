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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests creating and cancelling new and edit Routing Rule Delegation maintenance screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesDelegationLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

//    @Test // There are no results from the search so no edit link to click on
    /**
     * tests that a Routing Rule Delegation maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditRouteRulesDelegation() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Routing Rules Delegation");
        waitForPageToLoad();
        Thread.sleep(3000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
      //  setSpeed("2000");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        Thread.sleep(3000);
        assertTrue(isElementPresentByName("methodToCall.cancel"));
        waitAndClickByName("methodToCall.cancel");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        passed();
    }
    
    @Test
    public void testCreateNewRRDTravelRequestDestRouting() throws Exception {
        waitAndClickByLinkText("Routing Rules Delegation");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClick("img[alt=\"create new\"]");
        waitForPageToLoad();
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.RuleBaseValues!!).(((id:parentRuleId))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
   //     Thread.sleep(6000);
        waitAndClickByXpath("//td[@class='infoline']/input[@name='methodToCall.search']");
        waitForPageToLoad();
     //   Thread.sleep(6000);
        waitAndClick("a[title=\"return valueRule Id=1046 \"]");
        waitForPageToLoad();
        waitAndClickByName("parentResponsibilityId");
        waitAndClickByName("methodToCall.createDelegateRule");
        waitForPageToLoad();
        Thread.sleep(3000);
        waitAndClickByName("methodToCall.cancel");
        waitForPageToLoad();
        //Thread.sleep(3000);
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        //Thread.sleep(3000);
        driver.switchTo().defaultContent();
        waitAndClickByXpath("(//input[@name='imageField'])[2]");
        waitForPageToLoad();
        passed();
    }
}
