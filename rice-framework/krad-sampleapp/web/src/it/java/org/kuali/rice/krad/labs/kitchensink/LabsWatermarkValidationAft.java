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
 * tests whether the watermarks is work as expected even when they contain an
 * apostrophe
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsWatermarkValidationAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page1
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page1";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	/**
	 * if watermarking is ok, the cancel link will bring up a confirmation if something was typed into a textbox i.e
	 * the scripts will be working ok
	 */
	public void testWatermarking() throws Exception {

        String watermarkValue = waitAndGetAttributeByName("field106", "placeholder");
        assertEquals("It's watermarked ", watermarkValue);
		watermarkValue = waitAndGetAttributeByName("field110", "placeholder");
        assertEquals("Watermark... ",watermarkValue);
	}

    @Override
    protected void navigate() throws Exception {
        navigateToKitchenSink("Input Fields");
    }

    @Test
    public void testWatermarkingBookmark() throws Exception {
        testWatermarking();
        passed();
    }

    @Test
    public void testWatermarkingNav() throws Exception {
        testWatermarking();
        passed();
    }
}
