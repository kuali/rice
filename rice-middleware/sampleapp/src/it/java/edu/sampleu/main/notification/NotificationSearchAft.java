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
package edu.sampleu.main.notification;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationSearchAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Notification%20Search&channelUrl=" + WebDriverUtils
     * .getBaseUrlString() + "/kew/DocumentSearch.do?documentTypeName=KualiNotification&returnLocation=" +
     * AutomatedFunctionalTestUtils.PORTAL +"&hideReturnLink=true";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Notification%20Search&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/kew/DocumentSearch.do?documentTypeName=KualiNotification&returnLocation=" +
                    AutomatedFunctionalTestUtils.PORTAL +"&hideReturnLink=true";
    
    /*
     * //input[@title='search']
     */
    private static final String SEARCH_BUTTON_XPATH="//input[@title='search']";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickByLinkText("Notification Search");
    }
    
    private void testNotificationSearch() throws Exception{
        selectFrameIframePortlet();
        waitAndClickByXpath(SEARCH_BUTTON_XPATH);
        checkForIncidentReport();
        // No results
    }
    
    @Test
    public void testNotificationSearchBookmark() throws Exception {
        testNotificationSearch();
        passed();
    }

    @Test
    public void testNotificationSearchNav() throws Exception {
        testNotificationSearch();
        passed();
    }
    
}
