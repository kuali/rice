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
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * tests that user 'admin', on blanket approving a new County maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationCountyBlanketAppIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    public void testCounty() throws Exception {
        String remotePublicUrl = getBaseUrlString();
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Administration");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=County");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//img[@alt='create new']");
        selenium.waitForPageToLoad("30000");
        //selenium.setSpeed("2000");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test County");
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString()+ "/kr/lookup.do;::::).anchor4']";
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (selenium.isElementPresent(countryLookUp)) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.click(countryLookUp);
        selenium.waitForPageToLoad("30000");
        selenium.type("code", "US");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        selenium.type("//input[@id='document.newMaintainableObject.code']", RandomStringUtils.randomAlphabetic(2).toUpperCase());
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (selenium.isElementPresent(stateLookUp)) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.click(stateLookUp);   
        selenium.waitForPageToLoad("30000");
        selenium.type("code", "IN");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        String countyName = "Validation Test County"+Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        selenium.type("//input[@id='document.newMaintainableObject.name']", countyName);
        selenium.click("//input[@id='document.newMaintainableObject.active']");
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
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }        
    }
}
