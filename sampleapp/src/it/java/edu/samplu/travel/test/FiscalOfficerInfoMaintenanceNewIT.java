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
package edu.samplu.travel.test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * TODO Administrator don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerInfoMaintenanceNewIT extends SeleneseTestCase{
    
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("127.0.0.1", 4444, "*googlechrome", "http://127.0.0.1:8080/");
        selenium.start();
    }
    
    @Test
    public void testUntitled() throws Exception {
        selenium.open("/kr-dev/portal.do?selectedTab=main");
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        //selenium.click("link=FiscalOfficerInfo Maintenance (New)");
        selenium.waitForPageToLoad("50000");
        selenium.click("//a[@title='FiscalOfficerInfo Maintenance (New)']");
        selenium.waitForPageToLoad("50000");
        selenium.selectFrame("iframeportlet");
        //selenium.type("document.documentHeader.documentDescription", "New FO Doc");
        selenium.type("//input[@name='document.documentHeader.documentDescription']", "New FO Doc");
        //selenium.type("document.newMaintainableObject.dataObject.id", "6");
        selenium.type("//input[@name='document.newMaintainableObject.dataObject.id']", "11");
        //selenium.type("document.newMaintainableObject.dataObject.userName", "Chris");
        selenium.type("//input[@name='document.newMaintainableObject.dataObject.userName']", "Fletcher");
//        selenium.type("newCollectionLines['document.newMaintainableObject.dataObject.accounts'].number", "123123");       
//        selenium.type("xpath = //input[@name='document.newMaintainableObject.dataObject.accounts[0].number']", "123123");
//        selenium.type("//input[contains(@name , '.number')]", "123123");
//        selenium.type("newCollectionLines['document.newMaintainableObject.dataObject.accounts'].name", "ChrisTravelAcc");
//        selenium.type("xpath = //input[@name='document.newMaintainableObject.dataObject.accounts[0].name']", "ChrisTravelAcc");
//        selenium.type("//input[contains(@name , '.name')]", "JustinTravelAcc");
//        selenium.click("add");
//        selenium.click("//input[@value='add']");
//        System.out.println("adding Row");
//        selenium.wait(90000);
        System.out.println("Waiting Test");
        selenium.click("//input[@value='save']");
//      selenium.click("link=FiscalOfficerInfo Maintenance (New)");
        selenium.waitForPageToLoad("50000");
        selenium.click("//input[@value='submit']");
        selenium.waitForPageToLoad("50000");
        System.out.println("Submiting Test");
//      selenium.waitForPageToLoad("60000");
        
        System.out.println("Test complete");
    }
    
    
    
    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
