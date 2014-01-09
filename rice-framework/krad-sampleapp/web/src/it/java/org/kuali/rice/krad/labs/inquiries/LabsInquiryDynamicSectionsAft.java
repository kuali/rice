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
package org.kuali.rice.krad.labs.inquiries;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryDynamicSectionsAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR2C2
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR2C2";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry Dynamic Sections");
    }

    protected void testInquiryDynamicSections() throws InterruptedException {
    	waitAndClickByLinkText("Link to Inquiry with Dynamic Sections");
    	
    	// Lightbox
        waitAndClickByLinkText("IAT");
        gotoLightBox();
        assertLabeledIatText();
        waitAndClickButtonByText(CLOSE);
        selectTopFrame();
        assertLabeledIatText();

        //Dynamically Added 
        String[][] LabeledTextDynamic = {{"Dynamically Added Field:", "This is a dynamically set value."}};
        assertLabeledTextPresent(LabeledTextDynamic);
        clickCollapseAll();
        assertLabeledTextNotPresent(LabeledTextDynamic);
        clickExpandAll();
        assertLabeledTextPresent(LabeledTextDynamic);
    }

    @Test
    public void testInquiryDynamicSectionsBookmark() throws Exception {
    	testInquiryDynamicSections();
        passed();
    }

    @Test
    public void testInquiryDynamicSectionsNav() throws Exception {
    	testInquiryDynamicSections();
        passed();
    }
}
