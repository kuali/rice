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
package edu.sampleu.main;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

import java.io.File;

/**
 * Tests the Notes and Attachments in the People Flow screen.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowCreateNewNotesAndAttachmentsAft extends PeopleFlowCreateNewAftBase {

    /**
     * WebDriverUtils.getBaseUrlString() + "/kr-krad/peopleFlowMaintenance?" +
     * "viewTypeName=MAINTENANCE&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL_ENCODED  + "&methodToCall=start&" +
     * "dataObjectClassName=org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo"
     */
    public static final String BOOKMARK_URL =  WebDriverUtils.getBaseUrlString() + "/kr-krad/peopleFlowMaintenance?" +
            "viewTypeName=MAINTENANCE&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL_ENCODED  + "&methodToCall=start&" +
            "dataObjectClassName=org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo";

    /**
     * Provider of the temporary folder.
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String NOTES_AND_ATTACHMENTS_PREFIX = UifPropertyPaths.NEW_COLLECTION_LINES
            + "['"
            + KRADConstants.DOCUMENT_PROPERTY_NAME
            + "."
            + KRADConstants.NOTES_PROPERTY_NAME
            + "']";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * Tests adding both the required note and an optional attachment.
     *
     * @throws Exception when an error is encountered in the test
     */
    @Test
    public void testPeopleFlowCreateNewNotesAndAttachments_DefaultAttachmentBookmark() throws Exception {
        testDefaultAttachement();
    }

    /**
     * Tests adding both the required note and an optional attachment.
     *
     * @throws Exception when an error is encountered in the test
     */
    @Test
    public void testPeopleFlowCreateNewNotesAndAttachments_DefaultAttachmentNav() throws Exception {
        navigateToCreateNew();

        testDefaultAttachement();
    }

    protected void testDefaultAttachement() throws Exception {
        navigateToNotesAndAttachments();
        waitAndTypeByName(NOTES_AND_ATTACHMENTS_PREFIX + "." + KRADConstants.NOTE_TEXT_PROPERTY_NAME,
                "Attachment_Note");
        waitAndAddAttachment("attachment.txt", "Testing123");
        waitAndClick(By.cssSelector("button[title='Add a Note']"));

        Thread.sleep(2000);

        waitForTextPresent("Attachment_Note");
        assertTextPresent("attachment.txt");
        assertTextNotPresent("Note Text is a required field.");
        passed();
    }

    /**
     * Tests adding just the required note.
     *
     * @throws Exception when an error is encountered in the test
     */
    @Test
    public void testPeopleFlowCreateNewNotesAndAttachments_DefaultNoAttachmentBookmark() throws Exception {
        testDefaultNoAttachment();
    }

    /**
     * Tests adding just the required note.
     *
     * @throws Exception when an error is encountered in the test
     */
    @Test
    public void testPeopleFlowCreateNewNotesAndAttachments_DefaultNoAttachmentNav() throws Exception {
        navigateToCreateNew();

        testDefaultNoAttachment();
    }

    protected void testDefaultNoAttachment() throws InterruptedException {
        navigateToNotesAndAttachments();
        waitAndTypeByName(NOTES_AND_ATTACHMENTS_PREFIX + "." + KRADConstants.NOTE_TEXT_PROPERTY_NAME,
                "Attachment_Note");
        waitAndClick(By.cssSelector("button[title='Add a Note']"));

        waitForTextPresent("Attachment_Note");
        assertTextNotPresent("Note Text is a required field");
        passed();
    }

    /**
     * Tests adding just the optional attachment, which should result in an error.
     *
     * @throws Exception when an error is encountered in the test
     */
    @Test
    public void testPeopleFlowCreateNewNotesAndAttachments_NoNoteTextBookmark() throws Exception {
        testNoNoteText();
    }

    /**
     * Tests adding just the optional attachment, which should result in an error.
     *
     * @throws Exception when an error is encountered in the test
     */
    @Test
    public void testPeopleFlowCreateNewNotesAndAttachments_NoNoteTextNav() throws Exception {
        navigateToCreateNew();

        testNoNoteText();
    }

    protected void testNoNoteText() throws Exception {
        navigateToNotesAndAttachments();
        waitAndAddAttachment("attachment.txt", "Testing123");
        waitAndClick(By.cssSelector("button[title='Add a Note']"));

        Thread.sleep(2000);
        waitForTextPresent("Note Text is a required field");
        assertTextNotPresent("attachment.txt");
        passed();
    }

    protected void navigateToCreateNew() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByLinkText("Create New");
    }

    private void navigateToNotesAndAttachments() throws InterruptedException {
        waitForElementPresent(
                "div[data-header_for='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");

        waitAndClickByLinkText("Notes and Attachments (0)");
    }

    private void waitAndAddAttachment(String fileName, String content) throws Exception {
        File file = temporaryFolder.newFile(fileName);
        FileUtils.writeStringToFile(file, content);
        String path = file.getAbsolutePath().toString();
        waitAndTypeByName("attachmentFile", path);
    }
}
