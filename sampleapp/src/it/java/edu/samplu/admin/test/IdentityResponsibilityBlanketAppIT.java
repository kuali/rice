/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * TODO Administrator don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityResponsibilityBlanketAppIT {
    private Selenium selenium;
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
        selenium.start();
    }

    @Test
    public void testResponsibility() throws Exception {
        selenium.open("http://localhost:8080/kr-dev/portal.do");
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Administration");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Responsibility");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//img[@alt='create new']");
        selenium.waitForPageToLoad("30000");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test Responsibility");
        selenium.select("//select[@id='document.newMaintainableObject.namespaceCode']", "label=Kuali Systems");
        selenium.type("//input[@id='document.newMaintainableObject.name']", "Validation Test Responsibility1");
        selenium.type("//input[@id='document.newMaintainableObject.documentTypeName']", "Test");
        selenium.type("//input[@id='document.newMaintainableObject.routeNodeName']", "Test");
        selenium.click("//input[@id='document.newMaintainableObject.actionDetailsAtRoleMemberLevel']");
        selenium.click("//input[@id='document.newMaintainableObject.required']");
        selenium.click("methodToCall.blanketApprove");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("//img[@alt='doc search']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        docId= "link=" + docId;
        assertTrue(selenium.isElementPresent(docId));
        if(selenium.isElementPresent(docId)){            
            assertEquals("ENROUTE", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));
            assertEquals("ENROUTE", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
