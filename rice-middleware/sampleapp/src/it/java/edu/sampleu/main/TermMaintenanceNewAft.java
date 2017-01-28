/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermMaintenanceNewAft extends MainTmplMthdSTNavBase {

    /**
     * ITUtil.PORTAL + "?Term%20Lookup&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     *  "/kr-krad/maintenance?viewTypeName=MAINTENANCE&methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.TermBo&returnLocation=" +
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

    protected void createNewEnterDetails() throws InterruptedException {
        selectFrameIframePortlet();
        waitAndClickLinkContainingText("Create New");

        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName("document.newMaintainableObject.dataObject.description","New Term " + randomCode);
        waitAndTypeByName("document.newMaintainableObject.dataObject.specificationId", "T1000");
        fireEvent("document.newMaintainableObject.dataObject.specificationId", "blur");
        waitForProgressLoading();
        waitForTextPresent("campusSize");
        waitForTextPresent("java.lang.Integer");
        waitForElementPresentByXpath("//label[contains(text(),'Specification Description')]/span[contains(text(),'Size in # of students of the campus')]");
        waitAndTypeByName("document.newMaintainableObject.dataObject.parametersMap[Campus Code]","FakeCampus" + randomCode);

        waitAndClickByXpath("//button[contains(text(),'Submit')]");
        waitAndClickConfirmSubmitOk();
        waitForProgressLoading();
        waitForTextPresent("Document was successfully submitted.", WebDriverUtils.configuredImplicityWait() * 2);
        waitForTextPresent("FakeCampus" + randomCode);
    }

    @Test
    public void testCreateNewTermWithParameterBookmark() throws Exception {
        createNewEnterDetails();
        passed();
    }

    @Test
    public void testCreateNewTermWithParameterNav() throws Exception {
        createNewEnterDetails();
        passed();
    }
}