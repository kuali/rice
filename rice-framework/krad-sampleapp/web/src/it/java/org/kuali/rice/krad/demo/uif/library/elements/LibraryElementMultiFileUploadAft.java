/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.library.elements;

import java.io.File;
import java.util.List;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverFileResourceAftBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryElementMultiFileUploadAft extends WebDriverFileResourceAftBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-MultiFileUploadView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-MultiFileUploadView";
    
    // values set by default for repeatable testing; left as configurable for load tests
    protected List<File> fileUploadList;
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickLibraryLink();
        waitAndClickByLinkText("Elements");
    	waitAndClickByLinkText("Multi-file Upload");
    }

    protected void testElementMultiFileUpload() throws Exception {
    	fileUploadSetUpforText();
    	fileIngesterByName("files");
        assertLinked();
        assertDelete();

    	selectByName("exampleShown", "Max Size Multi-file Upload");
        fileUploadSetUpforPdf();
        fileIngesterByName("files1");
        waitForElementVisibleBy(By.linkText("test.pdf"));
        assertTrue(isTextPresent("9.2 KB")); // file size
        clickFirstTrashIcon();
        waitForElementNotPresent(By.linkText("test.pdf"));

    	selectByName("exampleShown", "Extra Fields Multi-file Upload");
        fileUploadSetUpforText();
        fileIngesterByName("files2");
        assertLinked();
        isElementPresentByName("files2[0].detail1"); // detail 1 extra field
        isElementPresentByName("files2[0].detail2"); // detail 2 extra field
        assertDelete();

        selectByName("exampleShown", "File Types Multi-file Upload");
    	fileUploadSetUpforJpg();
        fileIngesterByName("files3");
        waitForElementVisibleBy(By.linkText("home.jpg"));
        clickFirstTrashIcon();
        waitForElementNotPresent(By.linkText("home.jpg"));
    }

    private void assertLinked() throws InterruptedException {
        waitForElementVisibleBy(By.linkText("test.txt"));
        waitForElementVisibleBy(By.linkText("test1.txt"));
    }

    private void assertDelete() throws InterruptedException {
        clickFirstTrashIcon();
        waitForElementNotPresent(By.linkText("test.txt"));

        clickFirstTrashIcon();
        waitForElementNotPresent(By.linkText("test1.txt"));
    }

    private void clickFirstTrashIcon() throws InterruptedException {
        jGrowl("Click trash icon");
        waitAndClickByXpath("//div[@id='Demo-MultiFileUploadView']//button[contains(@class,'icon-trash')]");
        waitAndClickConfirmDeleteYes();
    }

    private void fileUploadSetUpforText() throws Exception {
    	setUpResourceDir("general", "txt");
    }
    
    private void fileUploadSetUpforJpg() throws Exception {
    	setUpResourceDir("general", "jpg");
    }

    private void fileUploadSetUpforPdf() throws Exception {
        setUpResourceDir("general", "pdf");
    }

    @Test
    public void testElementMultiFileUploadBookmark() throws Exception {
    	testElementMultiFileUpload();
        passed();
    }

    @Test
    public void testElementMultiFileUploadNav() throws Exception {
    	testElementMultiFileUpload();
        passed();
    }
}
