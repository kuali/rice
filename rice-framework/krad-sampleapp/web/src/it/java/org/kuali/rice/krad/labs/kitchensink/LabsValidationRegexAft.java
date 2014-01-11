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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsValidationRegexAft extends LabsKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=4e87b0ca-c718-49c2-ac6d-f86e8dbabf6c&cacheKey=ca03hvydzk027i3l2hw0ldkuik&pageId=UifCompView-Page4#UifCompView-Page4";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Validation - Regex");
	}
	
	@Test
    public void testValidationRegexBookmark() throws Exception {
        testValidationRegex();
        passed();
    }

    @Test
    public void testValidationRegexNav() throws Exception {
        testValidationRegex();
        passed();
    }
    
    protected void testValidationRegex() throws InterruptedException 
    {
        assertFocusTypeBlurError("field50","1qqqqq.qqqqqq");
        assertFocusTypeBlurError("field51","-1.0E");
        assertFocusTypeBlurError("field77","1.2");
        assertFocusTypeBlurError("field52","asddffgghj");
        assertFocusTypeBlurError("field53"," :_");
        assertFocusTypeBlurError("field54","as");
        assertFocusTypeBlurError("field84","kuali.org");
        assertFocusTypeBlurError("field55","1234");
        assertFocusTypeBlurError("field75","aws");
        assertFocusTypeBlurError("field82","12");
        assertFocusTypeBlurError("field83","24");
        assertFocusTypeBlurError("field57","1599");
        assertFocusTypeBlurError("field58","0");
        assertFocusTypeBlurError("field61","360001");
        assertFocusTypeBlurError("field62","@#");
        assertFocusTypeBlurError("field63","2a#");
        assertFocusTypeBlurError("field64","1@");
        assertFocusTypeBlurError("field76","a2");
        assertFocusTypeBlurError("field65","a e");
        assertFocusTypeBlurError("field66","sdfa");
        assertFocusTypeBlurError("field67","1234-a");
        assertFocusTypeBlurError("field68","4.a");
        assertFocusTypeBlurError("field67","");
    }
}
