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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupInquiryDisabledAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-InquiryDisabledView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-InquiryDisabledView&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Inquiry Disabled");
    }

    @Test
    public void testLabsLookupInquiryDisabledBookmark() throws Exception {
        testLabsLookupInquiryDisabled();
        passed();
    }

    @Test
    public void testLabsLookupInquiryDisabledNav() throws Exception {
        testLabsLookupInquiryDisabled();
        passed();
    }
    
    protected void testLabsLookupInquiryDisabled()throws Exception {
        waitAndClickSearch3();
        Thread.sleep(2000);
        if(isElementPresentByXpath("//a[contains(text(), 'a1')]")) {
            jiraAwareFail("Inquiry has not been disabled. " + this.getClass().toString());
        }
    }
}
