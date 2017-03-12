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

import java.io.File;
import java.util.List;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverFileResourceAftBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsMultiFileUploadBasic1Aft extends WebDriverFileResourceAftBase {

    /**
     * /kr-krad/labs?viewId=Lab-MultiFileUpload1
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-MultiFileUpload1";
    
    // values set by default for repeatable testing; left as configurable for load tests
    protected List<File> fileUploadList;
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("MultiFile Upload - Basic");
    }

    protected void testMultiFileUploadBasic1() throws Exception {
    	fileUploadSetUp();
    	fileIngesterByName("files");
        waitForElementVisibleBy(By.linkText("test.txt"));
        waitForElementVisibleBy(By.linkText("test1.txt"));
    }
    
    private void fileUploadSetUp() throws Exception {
    	setUpResourceDir("general");
    }
    
    @Test
    public void testMultiFileUploadBasic1Bookmark() throws Exception {
    	testMultiFileUploadBasic1();
        passed();
    }

    @Test
    public void testMultiFileUploadBasic1Nav() throws Exception {
    	testMultiFileUploadBasic1();
        passed();
    }
}
