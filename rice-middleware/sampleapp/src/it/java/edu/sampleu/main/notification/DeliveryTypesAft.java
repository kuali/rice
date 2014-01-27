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
public class DeliveryTypesAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Delivery%20Types&channelUrl=" + WebDriverUtils
     * .getBaseUrlString() + "/kcb/prefs.kcb";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Delivery%20Types&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + "/kcb/prefs.kcb";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickByLinkText("Delivery Types");
    }
    
    private void testDeliveryTypes() throws Exception{
        selectFrameIframePortlet();
        selectByName("Mock.channels","None");
        clearTextByName("SMS.sms_mobile_number");
        waitAndTypeByName("SMS.sms_mobile_number","555-555-5555");
        selectByName("SMS.channels","Kuali Rice Channel");
        clearTextByName("Email.email_address");
        clearTextByName("Email.email_delivery_format");
        waitAndTypeByName("Email.email_address","dmoteria@kuali.org");
        waitAndTypeByName("Email.email_delivery_format","html");
        selectByName("Email.channels","Kuali Rice Channel");
        clearTextByName("AIM.aim_screen_name");
        waitAndTypeByName("AIM.aim_screen_name","dm");
        selectByName("AIM.channels","Kuali Rice Channel");
        waitAndClickByName("save");
        waitForTextPresent("Update Successful");
    }
    
    @Test
    public void testDeliveryTypesBookmark() throws Exception {
        testDeliveryTypes();
        passed();
    }

    @Test
    public void testDeliveryTypesNav() throws Exception {
        testDeliveryTypes();
        passed();
    }
    
}
