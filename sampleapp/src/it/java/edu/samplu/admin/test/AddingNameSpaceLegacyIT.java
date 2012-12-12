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

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * tests adding a namespace to Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddingNameSpaceLegacyIT extends AdminMenuLegacyITBase {
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuLegacyITBase#getLinkLocator()
     */
    @Override
    protected String getLinkLocator() {
        return "Namespace";
    }
    @Test
    public void testAddingNamespace() throws Exception {
        
//        assertEquals("Kuali Portal Index", getTitle());
//        waitAndClickByLinkText("Administration");
//        waitForPageToLoad();
//        Thread.sleep(2000);
//        assertEquals("Kuali Portal Index", getTitle());
//        waitAndClickByLinkText("Namespace");
//        waitForPageToLoad();
//        Thread.sleep(3000);
//        assertEquals("Kuali Portal Index", getTitle());
//        selectFrame("iframeportlet");
//        assertElementPresentByXpath("//img[@alt='create new']","create new button does not exist on the page");
//        waitAndClickByXpath("//img[@alt='create new']");
        super.gotoCreateNew();
        waitForPageToLoad();
        assertElementPresentByXpath("//*[@name='methodToCall.save' and @alt='save']","save button does not exist on the page");
        //Enter details for Namespace.
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Adding PEANUTS");
        waitAndTypeByXpath("//*[@id='document.documentHeader.explanation']", "I want to add PEANUTS to test KIM");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.code']", "PEANUTS");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']","The Peanuts Gang");
        checkByXpath("//input[@id='document.newMaintainableObject.active']");
        waitAndClickByXpath("//*[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        checkForIncidentReport();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]", "Document is not saved successfully");
        //checks it is saved and initiator is admin.
        assertEquals("SAVED", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
        
    }

    
}