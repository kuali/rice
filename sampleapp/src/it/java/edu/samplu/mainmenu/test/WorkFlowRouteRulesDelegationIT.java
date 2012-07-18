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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


/**
 * tests creating and cancelling new and edit Routing Rule Delegation maintenance screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesDelegationIT {
    private Selenium selenium;
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    /**
     * tests that a new Routing Rule Delegation maintenance document can be cancelled
     */
    public void testCreateNew() throws Exception {
        selenium.open(System.getProperty("remote.public.url"));
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Routing Rules Delegation");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//img[@alt='create new']");        
        selenium.selectFrame("relative=up");        
        selenium.waitForPageToLoad("30000");
        selenium.click("name=methodToCall.performLookup.(!!org.kuali.rice.kew.rule.RuleBaseValues!!).(((id:parentRuleId))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        selenium.waitForPageToLoad("30000");
        selenium.click("css=td.infoline > input[name=\"methodToCall.search\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("css=a[title=\"return valueRule Id=1046 \"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("name=parentResponsibilityId");
        selenium.click("name=methodToCall.createDelegateRule");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        selenium.click("methodToCall.cancel");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.processAnswer.button0");
        selenium.waitForPageToLoad("30000");
    }

    @Test
    /**
     * tests that a Routing Rule Delegation maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditRouteRulesDelegation() throws Exception {
        selenium.open(System.getProperty("remote.public.url"));
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Routing Rules Delegation");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.setSpeed("2000");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=edit");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        selenium.click("methodToCall.cancel");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.processAnswer.button0");
        selenium.waitForPageToLoad("30000");
              
    }
    
    @Test
    public void testCreateNewRRDTravelRequestDestRouting() throws Exception {
        selenium.open(System.getProperty("remote.public.url"));
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Routing Rules Delegation");
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        selenium.click("css=img[alt=\"create new\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("name=methodToCall.performLookup.(!!org.kuali.rice.kew.rule.RuleBaseValues!!).(((id:parentRuleId))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        selenium.waitForPageToLoad("30000");
        selenium.click("css=td.infoline > input[name=\"methodToCall.search\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("css=a[title=\"return valueRule Id=1046 \"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("name=parentResponsibilityId");
        selenium.click("name=methodToCall.createDelegateRule");
        selenium.waitForPageToLoad("30000");
        selenium.click("name=methodToCall.cancel");
        selenium.waitForPageToLoad("30000");
        selenium.click("name=methodToCall.processAnswer.button0");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("xpath=(//input[@name='imageField'])[2]");
        selenium.waitForPageToLoad("30000");
        
              
    }
    
    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
