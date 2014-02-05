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
package org.kuali.rice.krad.demo.travel.account;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.krad.labs.inquiries.LabsInquiryBase;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountTypeInquiryAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/inquiry?methodToCall=start&accountTypeCode=IAT&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccountType
     */
    public static final String BOOKMARK_URL = "/kr-krad/inquiry?methodToCall=start&accountTypeCode=IAT&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccountType";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Account Type Inquiry");
    }

    @Test
    @Ignore // link removed
    public void testTravelAccountTypeInquiryNav() throws Exception {
        testTravelAccountTypeInquiry();
        passed();
    }

    @Test
    public void testTravelAccountTypeInquiryBookmark() throws Exception {
        testTravelAccountTypeInquiry();
        passed();
    }
    
    protected void testTravelAccountTypeInquiry() throws Exception {
        waitForElementPresentByXpath("//a[contains(text(),'IAT')]");
        assertTextPresent(LabsInquiryBase.LABELED_IAT_TEXT);
     }
}
