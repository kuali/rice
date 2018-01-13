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
package edu.sampleu.kim.api.identity;

import edu.sampleu.admin.AdminTmplMthdAftNavBlanketAppBase;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityPermissionAft extends AdminTmplMthdAftNavBlanketAppBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Permission&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.kim.impl.permission.UberPermissionBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Permission&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.kim.impl.permission.UberPermissionBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK ;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Permission
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Permission";
    }

    @Override
    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        selectOptionByName("document.newMaintainableObject.templateId", "35"); // KR-IDM : Assign Role
        selectOptionByName("document.newMaintainableObject.namespaceCode", namespaceCode);
        jiraAwareTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
    }

    @Override
    protected void createNewLookupDetails() throws InterruptedException {
        createNewEnterDetails(); // no lookups
    }
}
