/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.labs.inquiries;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoInquiryCustomLinkAft extends DemoInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C1
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR4C1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry Custom Link");
    }

    protected void testDemoInquiryCustomLink() throws InterruptedException {
    	waitAndClickByLinkText("Link to Inquiry with a Custom Link");
    	Thread.sleep(2000);
    	assertTextPresent("Income");
    	assertTextPresent("IAT - Income");
    	assertTextPresent("Travel Account Type Code");
    }

    @Test
    public void testDemoInquiryCustomLinkBookmark() throws Exception {
    	testDemoInquiryCustomLink();
        passed();
    }

    @Test
    public void testDemoInquiryCustomLinkNav() throws Exception {
    	testDemoInquiryCustomLink();
        passed();
    }
}
