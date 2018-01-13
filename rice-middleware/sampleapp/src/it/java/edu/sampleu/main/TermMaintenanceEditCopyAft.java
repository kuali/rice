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
package edu.sampleu.main;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermMaintenanceEditCopyAft extends MainTmplMthdSTNavBase {

    /**
     * ITUtil.PORTAL + "?Term%20Lookup&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     *  "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.TermBo&returnLocation=" +
     *  ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Term%20Lookup&channelUrl=" + WebDriverUtils.getBaseUrlString()
            + "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.TermBo&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Term Lookup
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Term Lookup";
    }

    protected void testCopyEditExistingTermWithParameter() throws InterruptedException {
        // Copy an existing term to test copy and to ensure we don't edit term T1000
        copyExistingTermWithParameter();

        // Edit a term
        selectTopFrame();
        waitAndClickByLinkText("Main Menu", "");
        waitAndClickByLinkText("Term Lookup", "");
        selectFrameIframePortlet();
        waitAndTypeByName("lookupCriteria[id]", "1*");
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickEdit();

        waitForTextPresent("campusSize");
        waitForTextPresent("java.lang.Integer");
        waitForElementPresentByXpath("//label[contains(text(),'Specification Description')]/span[contains(text(),'Size in # of students of the campus')]");
        clearTextByName("document.newMaintainableObject.dataObject.description");
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName("document.newMaintainableObject.dataObject.description", "Edit Term - New ID "
                + StringUtils.substring(randomCode, 0, 4));
        clearTextByName("document.newMaintainableObject.dataObject.parametersMap[Campus Code]");
        waitAndTypeByName("document.newMaintainableObject.dataObject.parametersMap[Campus Code]",randomCode);
        submitSuccessfully();
        waitForTextPresent(randomCode);
    }

    protected void copyExistingTermWithParameter() throws InterruptedException {
        selectFrameIframePortlet();
        waitAndTypeByName("lookupCriteria[id]", "T1000");
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickCopy();
        waitForTextPresent("BL");
        String newRandomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        clearTextByName("document.newMaintainableObject.dataObject.description");
        waitAndTypeByName("document.newMaintainableObject.dataObject.description", "Copy Term - Short ID "
                + StringUtils.substring(newRandomCode, 0, 4));
        clearTextByName("document.newMaintainableObject.dataObject.parametersMap[Campus Code]");
        waitAndTypeByName("document.newMaintainableObject.dataObject.parametersMap[Campus Code]", newRandomCode);
        submitSuccessfully();
        waitForTextPresent(newRandomCode);
    }

    @Test
    public void testCopyEditExistingTermWithParameterBookmark() throws Exception {
        testCopyEditExistingTermWithParameter();
        passed();
    }

    @Test
    public void testCopyEditExistingTermWithParameterNav() throws Exception {
        testCopyEditExistingTermWithParameter();
        passed();
    }
}