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
public class PermissionLegacyIT extends AdminMenuLegacyITBase {

    String docId;
    String permissionName;

    @Override
    protected String getLinkLocator() {
        return "Permission";
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
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(.,'Document Description (Description) is a required field.')]/img[@alt='error']");
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Adding Permission removeme");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[@class='error']");
        assertElementPresentByXpath("//div[contains(.,'Template (Template) is a required field.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'Permission Namespace (Permission Namespace) is a required field.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'Permission Name (Permission Name) is a required field.')]/img[@alt='error']");
        System.out.println("------------------------------------Validation Test Successful--------------------------");
        
        
        selectOptionByName("document.newMaintainableObject.templateId", "36");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-SYS");
        permissionName = "removeme" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", permissionName);
        waitAndTypeByName("document.newMaintainableObject.description", "namespaceCode=KR*");
        checkByName("document.newMaintainableObject.active");
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]");
        assertEquals("SAVED", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        assertEquals("ENROUTE", getTextByXpath("//table[@class='headerinfo']//tr[1]/td[2]"));
        System.out.println("------------------------------------Permission document submitted successfully--------------------------");
        
        selectTopFrame();
        super.gotoMenuLinkLocator();
        waitForPageToLoad();
        waitAndTypeByName("name", permissionName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(permissionName);
        System.out.println("----------------------------------Lookup successful-----------------------------");
        
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        Thread.sleep(2000);
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Editing Permission removeme");
        uncheckByName("document.newMaintainableObject.active");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        System.out.println("------------------------------------Inactivation of Permission successfull--------------------------");
        
        selectTopFrame();
        super.gotoMenuLinkLocator();
        waitForPageToLoad();
        waitAndTypeByName("name", permissionName);
        waitAndClickByXpath("//input[@title='Active Indicator - No']");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(permissionName);
        

        
    }

}