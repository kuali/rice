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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverFileResourceAftBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsMultiFileUploadMultipleUsesOnOnePageAft extends WebDriverFileResourceAftBase {

    /**
     * /kr-krad/labs?viewId=Lab-MultiFileUpload2
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-MultiFileUpload2";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("MultiFile Upload - Multiple Uses on One Page");
    }

    protected void testMultiFileUploadMultipleUsesOnOnePage() throws Exception {
    	fileUploadSetUp();
    	fileIngesterByName("files2");
        waitForElementVisibleBy(By.linkText("test.txt"));
        waitForElementVisibleBy(By.linkText("test1.txt"));
    }
    
    private void fileUploadSetUp() throws Exception {
    	setUpResourceDir("general");
    }

    @Test
    public void testMultiFileUploadMultipleUsesOnOnePageBookmark() throws Exception {
    	testMultiFileUploadMultipleUsesOnOnePage();
        passed();
    }

    @Test
    public void testMultiFileUploadMultipleUsesOnOnePageNav() throws Exception {
    	testMultiFileUploadMultipleUsesOnOnePage();
        passed();
    }
}
