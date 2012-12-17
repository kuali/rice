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
package edu.samplu.travel.krad.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * it tests travel account type lookup screen.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAccountTypeLookupLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    
    @Test
    public void testTravelAccountTypeLookup() throws Exception {
        waitAndClickByLinkText("KRAD");
        waitAndClickByLinkText("Travel Account Type Lookup");
        selectFrame("iframeportlet");
        
        //Blank Search
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertElementPresentByXpath("//table[@id='u27']//tr[contains(td[1],'CAT')]");
        assertElementPresentByXpath("//table[@id='u27']//tr[contains(td[1],'EAT')]");
        assertElementPresentByXpath("//table[@id='u27']//tr[contains(td[1],'IAT')]");
        
        //search with each field
        
        waitAndTypeByName("lookupCriteria[accountTypeCode]", "CAT");
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertElementPresentByXpath("//table[@id='u27']//tr[contains(td[1],'CAT')]");
        waitAndClickByXpath("//button[@id='u19']");
        Thread.sleep(2000);
        
        waitAndTypeByName("lookupCriteria[name]", "Expense Account Type");
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertElementPresentByXpath("//table[@id='u27']//tr[contains(td[1],'EAT')]");
        
        //Currently No links available for Travel Account Type Inquiry so cant verify heading and values.
       
    }
}
