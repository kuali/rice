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
public class RowDetailsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Demo-RowDetails&methodToCall=start
     */
    public static final String BOOKMARK_URL ="/kr-krad/uicomponents?viewId=Demo-RowDetails&methodToCall=start";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Row Details Demo");
        switchToWindow("Kuali :: Row Details Demo");
    }
    
    private void testRowDetails() throws Exception{
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section6_detLink_line0']");
        waitForElementPresentByXpath("//input[@name='field4']");
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section1_detLink_line0']");
        waitForElementPresentByXpath("//input[@name='list2[0].field4']");
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section2_detLink_line0']");
        waitForElementPresentByXpath("//tr/td/div/div[@data-label='Field 3']");      
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section3_detLink_line0']");
        waitForTextPresent("SubField 2");
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section4_detLink_line0']");
        waitForTextPresent("SubCollection Title");
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section5_detLink_line0']");
        waitForTextPresent("SubCollection");
        waitAndClickByXpath("//a[@id='Demo-RowDetails-Section7_detLink_line0']");
        waitForElementPresentByXpath("//table[@class='uif-lightTable']");
    }
    
    @Test
    public void testRowDetailsBookmark() throws Exception {
        testRowDetails();
        passed();
    }

    @Test
    public void testRowDetailsNav() throws Exception {
        testRowDetails();
        passed();
    }
    
}
