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
package edu.sampleu.admin.workflow;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XmlStylesheetsAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=XML%20Stylesheets&channelUrl="+ WebDriverUtils
     *  .getBaseUrlString()+"/kr/lookup.do?businessObjectClassName=org.kuali.rice.coreservice.impl.style.StyleBo&docFormKey=88888888&returnLocation="
     *  + AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=XML%20Stylesheets&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?businessObjectClassName=org.kuali.rice.coreservice.impl.style.StyleBo&docFormKey=88888888&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("XML Stylesheets");
    }

    protected void testXmlStylesheet() throws Exception {
        selectFrameIframePortlet();
        waitAndClickSearch();
        Thread.sleep(2000);
        assertTextPresent("eDoc.Example1.Style");
        assertTextPresent("2009");
        assertTextPresent("No");
        waitAndClickByXpath("//input[@name='active' and @value='Y']");
        waitAndClickSearch();
        Thread.sleep(2000);
        assertTextPresent("2020");
        assertTextPresent("2021");
        if(isTextPresent("2009"))  {
            jiraAwareFail("Filter not working ! " + this.getClass().toString() );
        }
    }

    @Test
    public void testXmlStylesheetBookmark() throws Exception {
        testXmlStylesheet();
        passed();
    }

    @Test
    public void testXmlStylesheetNav() throws Exception {
        testXmlStylesheet();
        passed();
    }
}
