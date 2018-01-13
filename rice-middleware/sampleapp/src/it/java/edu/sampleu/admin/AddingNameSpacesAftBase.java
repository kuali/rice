/**
 * Copyright 2005-2018 The Kuali Foundation
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
package edu.sampleu.admin;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AddingNameSpacesAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+WebDriverUtils.getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+ITUtil.PORTAL_URL+"&hideReturnLink=true";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Namespace&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+ AutomatedFunctionalTestUtils.PORTAL_URL+"&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Namespace
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Namespace";
    }

    public void testAddingNamespaceBookmark(JiraAwareFailable failable) throws Exception {
        testAddingNamespace(this);
        passed();
    }

    public void testAddingNamespaceNav(JiraAwareFailable failable) throws Exception {
        testAddingNamespace(this);
        passed();
    }

    public void testSearchEditBackNav(JiraAwareFailable failable) throws Exception {
        testSearchEditBack(this);
        passed();
    }

    public void testSearchSearchBackNav(JiraAwareFailable failable) throws Exception {
        testSearchSearchBack(this, "code", "KR-SYS");
        passed();
    }

    protected void testAddingNamespace(JiraAwareFailable failable) throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        waitForElementPresentByXpath(SAVE_XPATH_2, "save button does not exist on the page");

        //Enter details for Namespace.
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Adding PEANUTS");
        waitAndTypeByXpath("//*[@id='document.documentHeader.explanation']", "I want to add PEANUTS to test KIM");
        waitAndTypeByXpath(DOC_CODE_XPATH, "PEANUTS");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "The Peanuts Gang");
        checkByXpath("//input[@id='document.newMaintainableObject.active']");
        waitAndClickByXpath(SAVE_XPATH_2);
        waitForElementPresentByXpath(SAVE_SUCCESSFUL_XPATH, "Document is not saved successfully");

        //checks it is saved and initiator is admin.
        assertEquals(DOC_STATUS_SAVED, findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
    }
}
