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
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * tests that user 'admin', can initiate, save and submit a FiscalOfficerInfo maintenance document
 * resulting in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerInfoMaintenanceNewWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL =ITUtil.PORTAL+"?channelTitle=FiscalOfficerInfo%20Maintenance%20(New)&channelUrl="+ITUtil.getBaseUrlString()+"/kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&returnLocation="+ITUtil.PORTAL_URL+"&hideReturnLink=true";
    
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    
    @Test
    public void testUntitled() throws Exception {
//        waitAndClickByLinkText("KRAD");
//        waitAndClickByXpath("//a[@title='FiscalOfficerInfo Maintenance (New)']");
        selectFrame("iframeportlet");
        // String docId = getText("//span[contains(@id , '_attribute_span')][position()=1]");
        checkForIncidentReport("", "https://jira.kuali.org/browse/KULRICE-7723 FiscalOfficerInfoMaintenanceNewIT.testUntitled need a better name and user permission error");
        String docId = getTextByXpath("//*[@id='u13_control']");
        waitAndTypeByXpath("//input[@name='document.documentHeader.documentDescription']", "New FO Doc");
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.dataObject.id']", "5");
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.dataObject.userName']", "Jigar");
        
        waitAndClickByXpath("//button[@id='usave']");
        
        Integer docIdInt = Integer.valueOf(docId).intValue(); 
        selectTopFrame();
        waitAndClickByXpath("//img[@alt='action list']");
        selectFrame("iframeportlet");
        if(isElementPresentByLinkText("Last")){
            waitAndClickByLinkText("Last");
            waitAndClickByLinkText(docIdInt.toString());
        } else {                                  
            waitAndClickByLinkText(docIdInt.toString());
        }
        
        
//        ------------------------------- Not working in code when click docId link in list--------------------------
//        Thread.sleep(5000); 
//        String[] windowTitles = getAllWindowTitles();
//        selectWindow(windowTitles[1]);
//        windowFocus();
//        assertEquals(windowTitles[1], getTitle());
//        checkForIncidentReport("Action List Id link opened window.", "https://jira.kuali.org/browse/KULRICE-9062 Action list id links result in 404 or NPE");
//        
//        //------submit-----//
//        selectFrame("relative=up");
//        waitAndClick("//button[@value='submit']");
//        waitForPageToLoad50000();
//        close();
//        //------submit over---//        
//        
//        //----step 2----//  
//        selectWindow("null");
//        windowFocus();
//        waitAndClick("//img[@alt='doc search']");
//        waitForPageToLoad50000();
//        assertEquals(windowTitles[0], getTitle());
//        selectFrame("iframeportlet");
//        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
//        waitForPageToLoad50000();
//        //----step 2 over ----//
//        
//        //-----Step 3 verifies that doc is final-------//        
//        assertEquals("FINAL", getText("//table[@id='row']/tbody/tr[1]/td[4]"));
//        selectFrame("relative=up");
//        waitAndClick("link=Main Menu");
//        waitForPageToLoad50000();
//        assertEquals(windowTitles[0], getTitle());
//        System.out.println("---------------------- :: Test complete :: ----------------------");
//        //-----Step 3 verified that doc is final -------//      
//     
    }
}
