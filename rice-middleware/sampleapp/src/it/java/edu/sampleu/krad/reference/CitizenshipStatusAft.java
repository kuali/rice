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
public class CitizenshipStatusAft extends WebDriverLegacyITBase {

    /**
     *  AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Citizenship%20Status&channelUrl="
     *   + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
     *   "org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo&returnLocation="
     *   + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Citizenship%20Status&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
            "org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Citizenship Status");
    }

    //Code for KRAD Test Package.
    protected void testCitizenshipStatus() throws Exception {
      selectFrameIframePortlet();
        waitAndClickSearchByText();
      Thread.sleep(3000);
//      Code cannot be executed as there is not data to test on Citizenship Status.
//      assertTextPresent("AFLT");
//      assertTextPresent("FCLTY");
//      assertTextPresent("STAFF");
//      waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
//      waitAndClickSearchByText();
//      Thread.sleep(3000);
//      if(isTextPresent("AFLT")) {
//          fail("Conditions not working !");
//      }
//      waitAndTypeByName("lookupCriteria[code]","AFLT");
//      waitAndClickSearchByText();
//      Thread.sleep(3000);
//      if(isTextPresent("FCLTY")) {
//          fail("Conditions not working !");
//      }
    }

    @Test
    public void testCitizenshipStatusBookmark() throws Exception {
        testCitizenshipStatus();
        passed();
    }

    @Test
    public void testCitizenshipStatusNav() throws Exception {
        testCitizenshipStatus();
        passed();
    }
}
