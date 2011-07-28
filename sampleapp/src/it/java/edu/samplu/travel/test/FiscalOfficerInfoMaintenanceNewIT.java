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
        //---- 3rd parameter(*firefox) can also be replaced by *chrome , *googlechrome.
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/"); 
        selenium.start();
    }
    
    @Test
    public void testUntitled() throws Exception {
        selenium.open("/kr-dev/portal.do?selectedTab=main");
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");     
        selenium.waitForPageToLoad("50000");    
        selenium.click("//a[@title='FiscalOfficerInfo Maintenance (New)']");
        selenium.waitForPageToLoad("50000");
        selenium.selectFrame("iframeportlet");       
        String docId = selenium.getText("//span[contains(@id , '_attribute_span')][position()=1]");
        selenium.type("//input[@name='document.documentHeader.documentDescription']", "New FO Doc");      
        selenium.type("//input[@name='document.newMaintainableObject.dataObject.id']", "5");      
        selenium.type("//input[@name='document.newMaintainableObject.dataObject.userName']", "Jigar");
        
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
    
        selenium.click("//input[@value='save']");
        
        int docIdInt = Integer.valueOf(docId).intValue(); 
        
        selenium.waitForPageToLoad("50000");
        selenium.selectFrame("relative=up");
        selenium.click("//img[@alt='action list']");     
        selenium.waitForPageToLoad("50000");      
        
        selenium.selectFrame("iframeportlet");
        if(selenium.isElementPresent("link=Last")){
            selenium.click("link=Last");
            selenium.waitForPageToLoad("50000");
            selenium.click("link="+docIdInt);
            System.out.println("--------------------- :: doc found at last : " + docId + " :: --------------------------");
        } else {                                  
            selenium.click("link="+docIdInt);
            System.out.println("--------------------- :: doc found : " + docId + " :: --------------------------");
            
        }
        
        Thread.sleep(5000); 
        String[] windowTitles = selenium.getAllWindowTitles();
              
        selenium.selectWindow(windowTitles[1]);  
        selenium.windowFocus();
        assertEquals(windowTitles[1], selenium.getTitle());
        //------submit-----//
        selenium.selectFrame("relative=up");
        selenium.click("//input[@value='submit']");
        selenium.waitForPageToLoad("50000");       
        selenium.close();             
        System.out.println("--------------- :: Submitted and Closed :: -------------- ");
      
        //------submit over---//        
        
        //----step 2----//  
        selenium.selectWindow("null");
        selenium.windowFocus();
        selenium.click("//img[@alt='doc search']");
        selenium.waitForPageToLoad("50000");
        assertEquals(windowTitles[0], selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("50000");
        //----step 2 over ----//
        
        //-----Step 3 verifies that doc is final-------//        
        assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        selenium.selectFrame("relative=up");
        selenium.click("link=Main Menu");
        selenium.waitForPageToLoad("50000");
        assertEquals(windowTitles[0], selenium.getTitle());
        System.out.println("---------------------- :: Test complete :: ----------------------");
        //-----Step 3 verified that doc is final -------//      
     
    }
    
    
    
    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
