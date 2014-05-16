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
package edu.sampleu.krad.reference;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleGroupPermissionResponsibilityTypeAft extends WebDriverLegacyITBase {

    /**
     *   AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Role/Group/Permission/Responsibility%20Type&channelUrl="
     *   + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
     *   "org.kuali.rice.kim.impl.type.KimTypeBo&returnLocation="
     *   + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Role/Group/Permission/Responsibility%20Type&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
            "org.kuali.rice.kim.impl.type.KimTypeBo&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Role/Group/Permission/Responsibility Type");
    }

    //Code for KRAD Test Package.
    protected void testRoleGroupPermissionResponsibilityType() throws Exception {
        selectFrameIframePortlet();
        selectByName("lookupCriteria[namespaceCode]","KR-BUS - Service Bus");
        waitAndClickSearchByText();
        Thread.sleep(3000);
        assertTextPresent("No values match this search.");
        waitAndClickButtonByText("Clear Values");
        waitAndClickSearchByText();
        assertDataTableContains(new String[][]{{"KUALI"}, {"KR-NS"}});
        waitAndTypeByName("lookupCriteria[name]","Permission");
        waitAndClickSearchByText();
        waitForTextNotPresent("Namespace or Component");
        assertTextPresent("KR-IDM");
    }

    @Test
    public void testRoleGroupPermissionResponsibilityTypeBookmark() throws Exception {
        testRoleGroupPermissionResponsibilityType();
        passed();
    }

    @Test
    public void testRoleGroupPermissionResponsibilityTypeNav() throws Exception {
        testRoleGroupPermissionResponsibilityType();
        passed();
    }
}
