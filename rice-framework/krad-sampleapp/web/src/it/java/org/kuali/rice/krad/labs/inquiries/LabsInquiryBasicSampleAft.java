/**
 * Copyright 2005-2017 The Kuali Foundation
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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryBasicSampleAft extends LabsInquiryBase {

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
    	navigateToInquiry("Inquiry Sample - Basic");
    }

    protected void testDemoInquiryBasicSample() throws InterruptedException {
        assertLabeledIatText();
    }

    @Test
    public void testDemoInquiryBasicSampleBookmark() throws Exception {
    	testDemoInquiryBasicSample();
        passed();
    }

    @Test
    public void testDemoInquiryBasicSampleNav() throws Exception {
    	testDemoInquiryBasicSample();
        passed();
    }
}
