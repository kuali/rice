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
 * it tests travel account lookup screen.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAccountLookupLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    
    @Test
    public void testTravelAccountLookup() throws Exception {
        waitAndClickByLinkText("KRAD");
        waitAndClickByLinkText("Travel Account Lookup");
        selectFrame("iframeportlet");
        
        //Blank Search
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertElementPresentByLinkText("a1");
        assertElementPresentByLinkText("a2");
        assertElementPresentByLinkText("a3");
        
        //QuickFinder Lookup
        
        waitAndTypeByName("lookupCriteria[number]", "a*");
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertElementPresentByLinkText("a1");
        assertElementPresentByLinkText("a2");
        assertElementPresentByLinkText("a3");
        waitAndClickByXpath("//button[@id='u19']");
        Thread.sleep(2000);
        
        //search with each field
        waitAndTypeByName("lookupCriteria[number]", "a2");
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertElementPresentByLinkText("a2");
        waitAndClickByXpath("//button[@id='u19']");
        Thread.sleep(2000);
        
        waitAndTypeByName("lookupCriteria[foId]", "1");
        waitAndClickByXpath("//*[@id='u18']");
        Thread.sleep(2000);
        assertEquals("1", getTextByXpath("//table[@id='u27']//tr//td[8]").trim().substring(0, 1));
        waitAndClickByXpath("//button[@id='u19']");
        Thread.sleep(2000);
        
        selectOptionByName("lookupCriteria[extension.accountTypeCode]", "CAT");
        waitAndClickByXpath("//*[@id='u18']");
        waitAndClickByXpath("//table[@id='u27']//tr//td[2]//a");
        Thread.sleep(2000);
        selectTopFrame();
        Thread.sleep(5000);
        WebElement iframe1= driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        driver.switchTo().frame(iframe1);
        assertEquals("Travel Account Inquiry", getTextByXpath("//h1/span").trim());
        assertEquals("CAT - Clearing Account Type", getTextByXpath("//*[@id='u44_control']").trim());
        waitAndClickByXpath("//button[@id='u13']");
        selectFrame("iframeportlet");
       
    }
}
