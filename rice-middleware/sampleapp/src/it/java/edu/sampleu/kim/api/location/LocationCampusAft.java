/**
 * Copyright 2005-2016 The Kuali Foundation
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
package edu.sampleu.kim.api.location;

import edu.sampleu.admin.AdminTmplMthdAftNavBlanketAppBase;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationCampusAft extends AdminTmplMthdAftNavBlanketAppBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Campus&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.location.impl.campus.CampusBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Campus&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.location.impl.campus.CampusBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK ;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Campus
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Campus";
    }

    @Override
    protected void createNewEnterDetails() throws InterruptedException {
        inputDetails();

        selectOptionByName("document.newMaintainableObject.campusTypeCode", "F");
    }

    private void inputDetails() throws InterruptedException {
        waitForElementPresentByName("document.documentHeader.documentDescription");

        jiraAwareClearAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        jiraAwareClearAndTypeByName("document.newMaintainableObject.code", uniqueString.substring(5, 7));
        jiraAwareClearAndTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
        jiraAwareClearAndTypeByName("document.newMaintainableObject.shortName", uniqueString);
    }

    @Override
    protected void createNewLookupDetails() throws InterruptedException {
        inputDetails();

        jGrowl("Click Campus Type Code lookup");
        waitAndClickByXpath("//input[@alt='Search Campus Type Code']");
        waitAndClickSearch();
        waitAndClickReturnValue();
    }
}
