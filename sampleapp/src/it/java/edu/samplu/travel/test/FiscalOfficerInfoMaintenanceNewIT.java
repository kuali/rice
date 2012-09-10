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
package edu.samplu.travel.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * tests that user 'admin', can initiate, save and submit a FiscalOfficerInfo maintenance document
 * resulting in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerInfoMaintenanceNewIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    public void testUntitled() throws Exception {
        waitAndClick("link=KRAD");
        selenium.waitForPageToLoad("50000");
        waitAndClick("//a[@title='FiscalOfficerInfo Maintenance (New)']");
        selenium.waitForPageToLoad("50000");
        selectFrame("iframeportlet");
        // String docId = selenium.getText("//span[contains(@id , '_attribute_span')][position()=1]");
        ITUtil.checkForIncidentReport(selenium, "", "https://jira.kuali.org/browse/KULRICE-7723 FiscalOfficerInfoMaintenanceNewIT.testUntitled need a better name and user permission error");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        selenium.type("//input[@name='document.documentHeader.documentDescription']", "New FO Doc");      
        selenium.type("//input[@name='document.newMaintainableObject.dataObject.id']", "5");      
        selenium.type("//input[@name='document.newMaintainableObject.dataObject.userName']", "Jigar");
        
//        selenium.type("newCollectionLines['document.newMaintainableObject.dataObject.accounts'].number", "123123");       
//        selenium.type("xpath = //input[@name='document.newMaintainableObject.dataObject.accounts[0].number']", "123123");
//        selenium.type("//input[contains(@name , '.number')]", "123123");
//        selenium.type("newCollectionLines['document.newMaintainableObject.dataObject.accounts'].name", "ChrisTravelAcc");
//        selenium.type("xpath = //input[@name='document.newMaintainableObject.dataObject.accounts[0].name']", "ChrisTravelAcc");
//        selenium.type("//input[contains(@name , '.name')]", "JustinTravelAcc");
//        waitAndClick("add");
//        waitAndClick("//input[@value='add']");
//        System.out.println("adding Row");
//        selenium.wait(90000);
    
        waitAndClick("//button[@value='save']");
        
        int docIdInt = Integer.valueOf(docId).intValue(); 
        
        selenium.waitForPageToLoad("50000");
        selectFrame("relative=up");
        waitAndClick("//img[@alt='action list']");
        selenium.waitForPageToLoad("50000");              
        selectFrame("iframeportlet");
        if(selenium.isElementPresent("link=Last")){
            waitAndClick("link=Last");
            selenium.waitForPageToLoad("50000");
            waitAndClick("link="+docIdInt);
        } else {                                  
            waitAndClick("link="+docIdInt);
        }
        
        Thread.sleep(5000); 
        String[] windowTitles = selenium.getAllWindowTitles();              
        selectWindow(windowTitles[1]);
        selenium.windowFocus();
        assertEquals(windowTitles[1], selenium.getTitle());
        
        //------submit-----//
        selectFrame("relative=up");
        waitAndClick("//button[@value='submit']");
        selenium.waitForPageToLoad("50000");       
        selenium.close();                   
        //------submit over---//        
        
        //----step 2----//  
        selectWindow("null");
        selenium.windowFocus();
        waitAndClick("//img[@alt='doc search']");
        selenium.waitForPageToLoad("50000");
        assertEquals(windowTitles[0], selenium.getTitle());
        selectFrame("iframeportlet");
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("50000");
        //----step 2 over ----//
        
        //-----Step 3 verifies that doc is final-------//        
        assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectFrame("relative=up");
        waitAndClick("link=Main Menu");
        selenium.waitForPageToLoad("50000");
        assertEquals(windowTitles[0], selenium.getTitle());
        System.out.println("---------------------- :: Test complete :: ----------------------");
        //-----Step 3 verified that doc is final -------//      
     
    }
}
