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
package edu.sampleu.admin.notification;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ManageContentTypesAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Manage%20Content%20Types&channelUrl=" + WebDriverUtils
     *.getBaseUrlString() + "/ken/ContentTypeManager.form";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Manage%20Content%20Types&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/ken/ContentTypeManager.form";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Manage Content Types");
    }
    
    private void testContentTypes() throws Exception{
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[@href='ContentTypeForm.form?name=Event']");
        waitAndClickByXpath("//input[@title='Update']");
        waitAndClickByLinkText("Add New Content Type");
        waitAndClickByXpath("//input[@title='Cancel']");
    }
    
    @Test
    public void testContentTypesBookmark() throws Exception {
        testContentTypes();
        passed();
    }

    @Test
    public void testContentTypesNav() throws Exception {
        testContentTypes();
        passed();
    }
    
}
