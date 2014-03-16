/**
 * Copyright 2005-2014 The Kuali Foundation
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

import edu.sampleu.admin.AdminTmplMthdAftNavBase;
import org.apache.commons.lang.RandomStringUtils;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LocationCampusBlanketAppAftBase extends AdminTmplMthdAftNavBase {

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
   
    public void testLocationCampusBlanketAppBookmark(JiraAwareFailable failable) throws Exception {
        testLocationCampusBlanketApprove();
        passed();
    }

    public void testLocationCampusBlanketAppNav(JiraAwareFailable failable) throws Exception {
        testLocationCampusBlanketApprove();
        passed();
    }

    protected void testLocationCampusBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        String twoLetters = RandomStringUtils.randomAlphabetic(2);
        waitAndTypeByName("document.documentHeader.documentDescription", "Validation Test Campus " + twoLetters);
        assertBlanketApproveButtonsPresent();
        waitAndTypeByName("document.newMaintainableObject.code", RandomStringUtils.randomAlphabetic(2));
        waitAndTypeByName("document.newMaintainableObject.name", "Validation Test Campus" + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomChars());
        waitAndTypeByName("document.newMaintainableObject.shortName", "VTC " + twoLetters);
        selectByName("document.newMaintainableObject.campusTypeCode", "B - BOTH");
        blanketApproveTest(docId);
    }

}
