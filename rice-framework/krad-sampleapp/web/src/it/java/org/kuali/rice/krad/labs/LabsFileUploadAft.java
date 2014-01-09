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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsFileUploadAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/fileUploads?viewId=Lab-FileUploads
     */
    public static final String BOOKMARK_URL = "/kr-krad/fileUploads?viewId=Lab-FileUploads";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("File Upload View");
    }

    protected void testDemoFileUpload() throws InterruptedException {
    	waitForElementPresentByXpath("//input[@type='file' and @name='uploadOne']");
    	assertElementPresentByXpath("//button[contains(text(),'Upload - Ajax')]");
    	assertElementPresentByXpath("//input[@type='file' and @name='uploadTwo']");
    	assertElementPresentByXpath("//button[contains(text(),'Upload - NonAjax')]");
    }

    @Test
    public void testDemoFileUploadBookmark() throws Exception {
    	testDemoFileUpload();
        passed();
    }

    @Test
    public void testDemoFileUploadNav() throws Exception {
    	testDemoFileUpload();
        passed();
    }
}
