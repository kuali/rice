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

import org.junit.Test;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersonLegacyIT extends AdminMenuLegacyITBase {

    String docId;
    String personName;

    @Override
    protected String getLinkLocator() {
        return "Person";
    }
    
    @Test
    public void testPerson() throws Exception {
        super.gotoCreateNew();
        waitForPageToLoad();
        Thread.sleep(2000);
        /*assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']",
                "save button does not exist on the page");*/
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Adding Charlie Brown");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Charlie Brown to test KIM");
        //here You should also check for lower case validation for principalName, but it is skipped for now as there is an incident report error there.
        personName = "cbrown" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.principalName", personName);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]");
        assertEquals("SAVED", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(.,'At least one affiliation must be entered.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'At least one name must be entered.')]/img[@alt='error']");
        System.out.println("------------------------------------Validation Test Successful--------------------------");
        
        selectOptionByName("newAffln.affiliationTypeCode", "STDNT");
        selectOptionByName("newAffln.campusCode", "BL");
        checkByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitForPageToLoad();
        //waitAndClickByName("methodToCall.toggleTab.tabContact");
        waitForPageToLoad();
        Thread.sleep(3000);
        selectOptionByName("newName.nameCode", "PRM");
        selectOptionByName("newName.namePrefix", "Mr");
        waitAndTypeByName("newName.firstName", "Charlie");
        waitAndTypeByName("newName.lastName", "Brown");
        checkByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        assertEquals("ENROUTE", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        System.out.println("------------------------------------Person document submitted successfully--------------------------");
        
        selectTopFrame();
        super.gotoMenuLinkLocator();
        waitForPageToLoad();
        waitAndTypeByName("principalName", personName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("firstName","Charlie");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("lastName", "Brown");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("campusCode", "BL");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(personName);
        System.out.println("----------------------------------Lookup successful-----------------------------");
        
        waitAndClickByLinkText(personName);
        waitForPageToLoad();
        Thread.sleep(5000);
        switchToWindow("Kuali :: Person");
        Thread.sleep(2000);
        assertEquals(personName, getTextByXpath("//div[@class='tab-container']/table//tr[2]/td[1]/div").trim());
        assertEquals("BL - BLOOMINGTON", getTextByXpath("//div[@class='tab-container']/table[3]//tr[2]/td[2]/div").trim());
        assertEquals("Student", getTextByXpath("//select/option[@selected]").trim());
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Membership']");
        waitAndClickByName("methodToCall.showAllTabs");
        Thread.sleep(3000);
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Membership']");
        waitAndClickByName("methodToCall.hideAllTabs");
        Thread.sleep(3000);
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Membership']");
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("------------------------------------Viewing from Inquiry Framework Test Successful--------------------------");
        
    }

}