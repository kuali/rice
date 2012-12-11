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
 * tests the group section in Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GroupLegacyIT extends AdminMenuLegacyITBase {
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuLegacyITBase#getLinkLocator()
     */
    @Override
    protected String getLinkLocator() {
        return "Group";
    }
    @Test
    public void testAddingBrownGroup() throws Exception {
        
        super.gotoCreateNew();
        waitForPageToLoad();
        String docId = driver.findElement(By.xpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]")).getText();
        //Enter details for BrownGroup.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Brown Group");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Brown Group to test KIM");
        selectOptionByName("document.groupNamespace", "KR-IDM");
        waitForPageToLoad();
        String groupName = "BrownGroup "+ITUtil.DTS_TWO;
        waitAndTypeByName("document.groupName", groupName);
        checkByName("document.active");
        waitAndClickByXpath("//*[@name='methodToCall.save' and @alt='save']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully saved.')]", "Document is not saved successfully");
        checkForIncidentReport();
        //checks it is saved and initiator is admin.
        assertEquals("SAVED", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", driver.findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]", "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        waitAndClickByLinkText("Group");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndTypeByName("name", groupName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(groupName);
    }

    
}