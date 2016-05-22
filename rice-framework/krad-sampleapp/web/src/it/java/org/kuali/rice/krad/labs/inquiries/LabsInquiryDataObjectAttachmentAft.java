/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryDataObjectAttachmentAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR6C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR6C3";
    
    protected List<File> fileUploadList;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry - Data Object Attachments");
    }

    protected void testInquiryforDataObjectWithAttachment() throws Exception {
        String attachmentId = createDataObjectAttacmentDocument();
        driver.navigate().back();
        waitAndClickByLinkText("Travel Attachment Lookup");
        waitAndTypeByName("lookupCriteria[id]", attachmentId);
        waitAndClickButtonByText("Search");
        waitAndClickLinkContainingText("attachment");
        gotoLightBox();
        waitForElementPresentByXpath("//button[contains(text(),'download attachment')]");
        waitAndClickButtonByExactText("download attachment");
    }

    protected void testInquiryforDataObjectWithAttachmentCollection() throws Exception {
        String number = createDataObjectAttacmentCollectionDocument();
        driver.navigate().back();
        waitAndClickByLinkText("Travel Attachment Collection Lookup");
        waitAndTypeByName("lookupCriteria[number]", number);
        waitAndClickButtonByText("Search");
        waitAndClickLinkContainingText(number);
        gotoLightBox();
        waitForElementPresentByXpath("//button[contains(text(),'download attachment')]");
        waitAndClickButtonByExactText("download attachment");
    }

    protected String createDataObjectAttacmentDocument() throws Exception {
        waitAndClickByLinkText("Create Travel Attachment");
        String desc = getDescriptionUnique();
        String attachmentId = "attachment" + uniqueString;
        waitAndTypeByName("document.documentHeader.documentDescription", desc);
        waitAndTypeByName("document.newMaintainableObject.dataObject.id",attachmentId);
        waitAndTypeByName("document.newMaintainableObject.dataObject.travelAttachmentGroupNumber","123");
        waitAndTypeByName("document.newMaintainableObject.dataObject.description", desc);
        fileUploadSetUp();
        fileIngester();
        waitAndClickButtonByText("Submit");
        waitAndClickByXpath("//div[@data-parent='ConfirmSubmitDialog']/button[contains(text(),'OK')]");
        waitForElementPresentByXpath("//button[contains(text(),'download attachment')]");
        return attachmentId;
    }

    protected String createDataObjectAttacmentCollectionDocument() throws Exception {
        waitAndClickByLinkText("Create Travel Attachment Collection");
        String desc = getDescriptionUnique();
        waitAndTypeByName("document.documentHeader.documentDescription", desc);
        String number = "Num" + uniqueString.substring(6, 13); // number length is not validated, max length is 10
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", number);
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", "Name" + uniqueString);
        fileUploadSetUp();

        if (fileUploadList != null && fileUploadList.size() > 0) {
            for (File file : fileUploadList) {
                waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.attachments'].id", "attachment" + uniqueString);
                String path = file.getAbsolutePath().toString();
                waitIsVisibleByXpath("//div[@data-label='Attached File']/fieldset/div/div/input[@type='file']");
                driver.findElement(By.xpath("//div[@data-label='Attached File']/fieldset/div/div/input[@type='file']"))
                        .sendKeys(path);
                System.out.println("In for " + path);
                waitAndClickButtonByExactText("Add");
                waitForTextPresent("Adding Line...");
            }
        }

        waitForElementPresentByXpath("//button[contains(text(),'download attachment')]");
        waitAndClickButtonByText("Submit");
        waitForProgressLoading();
        waitAndClickByXpath("//div[@data-parent='ConfirmSubmitDialog']/button[contains(text(),'OK')]");
        waitForElementPresentByXpath("//button[contains(text(),'download attachment')]");
        waitForTextPresent("Document was successfully submitted.", WebDriverUtils.configuredImplicityWait() * 2);
        return number;
    }

    private void fileUploadSetUp() throws Exception {
    	setUpResourceDir("general");
    }

    protected void setUpResourceDir(String resourceDir) {
        try {
            setUpFiles("src/test/resources/" + resourceDir);
            System.out.println("Try for setUpResourceDir");
        } catch (Exception e) {
            System.out.println("Problem loading files from filesystem ( " + e.getMessage() + "). If running from "
                    + "Intellij make sure working directory is rice-framework/krad-sampleapp/web attempt "
                    + "to load as resource.");
                // Example setUpFiles("rice-framework/krad-sampleapp/web/src/test/resources/" + resourceDir);
            try {
                setUpResourceFiles(resourceDir);
            } catch (Exception e1) {
                e1.printStackTrace();
                jiraAwareFail("Problems loading files as resources " + e1.getMessage());
            }
            System.out.println("Catch for setUpResourceDir");
        }
    }

    protected void setUpResourceFiles(String resourceDir) throws Exception {
        System.out.println("In for setUpResourceFiles");
        String[] resources = getResourceListing(getClass(), resourceDir);
        fileUploadList = new ArrayList<File>();

        for (String resource : resources) {
            InputStream inputStream = getClass().getResourceAsStream(resource);
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + resource);
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            fileUploadList.add(file);
            System.out.println("For for setUpResourceFiles");
        }

        Collections.sort(fileUploadList);
    }

    protected String[] getResourceListing(Class clazz, String pathStartsWith) throws Exception {
        System.out.println("In for getResourceListing");
        String classPath = clazz.getName().replace(".", "/")+".class";
        URL dirUrl = clazz.getClassLoader().getResource(classPath);

        if (!"jar".equals(dirUrl.getProtocol())) {
            throw new UnsupportedOperationException("Cannot list files for URL " + dirUrl);
        }

        String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!")); //strip out only the JAR file
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        Enumeration<JarEntry> entries = jar.entries();
        Set<String> result = new HashSet<String>();

        while(entries.hasMoreElements()) {
            String entry = entries.nextElement().getName();
            if (entry.startsWith(pathStartsWith) && !entry.endsWith("/")) { //filter according to the pathStartsWith skipping directories
                result.add(entry);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    protected void setUpFiles(String path) throws Exception {
        System.out.println("In for setUpFiles");
        fileUploadList = new ArrayList<File>();

        File dir = new File(path);

        if (dir != null && dir.listFiles().length > 0) {
            Integer i = 1;

            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".txt")) {
                    fileUploadList.add(file);
                }

                i++;
            }

            Collections.sort(fileUploadList);
        } else {
            throw new Exception("----Resources not found----");
        }
    }

    /**
     * Performs Ingesting files to fileupload component and asserts successful ingestion.
     *
     */
    private void fileIngester() throws Exception {
    	System.out.println("In for fileIngester");
        if(fileUploadList!=null && fileUploadList.size()>0)
        {
	        for (File file : fileUploadList) {
	            String path = file.getAbsolutePath().toString();
	            driver.findElement(By.name("document.newMaintainableObject.dataObject.attachmentFile")).sendKeys(path);
	            System.out.println("In for -------");
	        }
        }
    }

    @Test
    public void testInquiryforDataObjectWithAttachmentBookmark() throws Exception {
        testInquiryforDataObjectWithAttachment();
        passed();
    }

    @Test
    public void testInquiryforDataObjectWithAttachmentNav() throws Exception {
        testInquiryforDataObjectWithAttachment();
        passed();
    }

    @Test
    public void testInquiryforDataObjectWithAttachmentCollectionBookmark() throws Exception {
        testInquiryforDataObjectWithAttachmentCollection();
        passed();
    }

    @Test
    public void testInquiryforDataObjectWithAttachmentCollectionNav() throws Exception {
        testInquiryforDataObjectWithAttachmentCollection();
        passed();
    }
}
