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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IncidentReportAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uitest?viewId=Travel-testView2&methodToCall=foo
     */
    public static final String BOOKMARK_URL ="/kr-krad/uitest?viewId=Travel-testView2&methodToCall=foo";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Incident Report");
        switchToWindow("Kuali :: Incident Report");
    }
    
    private void testIncidentReport() throws Exception{
        waitForTextPresent("Incident Report");
        waitForTextPresent("Incident Feedback");
        waitForTextPresent("The system has encountered an error and is unable to complete your request at this time. Please provide more information regarding this error by completing this Incident Report.");
        waitForTextPresent("Stacktrace (only in dev mode)");
        waitForElementPresentByXpath("//button[contains(text(),'Submit Report')]");
    }
    
    @Test
    public void testIncidentReportBookmark() throws Exception {
        testIncidentReport();
        passed();
    }

    @Test
    public void testIncidentReportNav() throws Exception {
        testIncidentReport();
        passed();
    }
    
}
