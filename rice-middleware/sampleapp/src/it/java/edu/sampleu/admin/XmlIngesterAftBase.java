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
package edu.sampleu.admin;

import edu.sampleu.common.FreemarkerAftBase;
import freemarker.template.DefaultObjectWrapper;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.common.PropertiesUtils;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Tests uploads of new users and group.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class XmlIngesterAftBase extends FreemarkerAftBase {

    /**
     * http://env12.rice.kuali.org/portal.do?channelTitle=XML%20Ingester&channelUrl=http://env12.rice.kuali.org/kew/../core/Ingester.do
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=XML%20Ingester&channelUrl="
            + WebDriverUtils.getBaseUrlString() + "/kew/../core/Ingester.do";

    // File generation
    private String PROPS_LOCATION = System.getProperty("xmlingester.props.location", null);
    private static final String DEFAULT_PROPS_LOCATION = "XML/xmlingester.properties";

    // Templates for File Generation
    private static final String DIR_TMPL = "/XML/";
    private static final String TMPL_USER_CONTENT = "SimpleUserContent.ftl";
    private static final String TMPL_GROUP_CONTENT = "SimpleGroupContent.ftl";

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    protected File newTempFile(String name) throws IOException {
        return folder.newFile(name);
    }

    /**
     * {@inheritDoc}
     * {@link #DIR_TMPL}
     * @return
     */
    @Override
    protected String getTemplateDir() {
        return DIR_TMPL;
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * "admin" xml ingestion requires admin permissions.
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getUserName() {
        return "admin";
    }

    /**
     * go to the getMenuLinkLocator() Menu and click the getLinkLocator()
     */
    @Override
    protected void navigate() throws Exception {
        selectTopFrame();
        waitAndClickAdministration();
        waitForTitleToEqualKualiPortalIndex();
        waitAndClickXMLIngester(this);
        selectFrameIframePortlet();
        checkForIncidentReport("XML Ingester", this, "");
    }

    public void testXmlIngesterUserList() throws Exception {
        List<File> fileUploadList = buildUserFileUploadList();
        testIngestion(this, fileUploadList);
    }

    /**
     * Navigate to the page under test and call {@link #testIngestion}
     *
     * @param failable {@link org.kuali.rice.testtools.common.JiraAwareFailable}
     * @throws Exception
     */
    protected void testIngestionNav(JiraAwareFailable failable) throws Exception {
        List<File> fileUploadList = buildFileUploadList();
        testIngestion(failable, fileUploadList);
        passed();
    }

    protected void testIngestionBookmark(JiraAwareFailable failable) throws Exception {
        List<File> fileUploadList = buildFileUploadList();
        testIngestion(failable, fileUploadList);
        passed();
    }

    /**
     * Based on load user and groups manual tests; dynamically generates user and group file
     * and loads into the xml ingester screen.
     * This test should suffice for both KRAD and KNS versions of the ingester screen.
     *
     *
     */
    protected void testIngestion(JiraAwareFailable failable, List<File> fileUploadList) throws Exception {
        selectFrameIframePortlet();
        int cnt = 0;

        for(File file : fileUploadList) {
            String path = file.getAbsolutePath().toString();
            if (isKrad()){
                driver.findElement(By.name("files[" + cnt + "]")).sendKeys(path);
            } else {
                driver.findElement(By.name("file[" + cnt + "]")).sendKeys(path);
            }
            cnt++;
        }

        // Click the Upload Button
        if (isKrad()){
            waitAndClickByXpath("//button");
        } else {
            waitAndClickByXpath("//*[@id='imageField']");
        }

        // confirm all files were uploaded successfully
        Thread.sleep(1000);
        for(File file: fileUploadList) {
            waitForTextPresent("Ingested xml doc: " + file.getName(), 360);
        }
    }

    protected List<File> buildFileUploadList() throws Exception {
        List<File> fileUploadList = new ArrayList<File>();
        try {
            // update properties with timestamp value if includeDTSinPrefix is true
            Properties props = loadProperties(PROPS_LOCATION, DEFAULT_PROPS_LOCATION);
            if(props.get("userIncludeDTSinPrefix") != null
                    && "true".equalsIgnoreCase((String) props.get("userIncludeDTSinPrefix"))) {
                props.setProperty("userPrefix", "" + props.get("userPrefix") + AutomatedFunctionalTestUtils.DTS);
            }
            props = new PropertiesUtils().systemPropertiesOverride(props, "XMLIngester");

            // build files and add to array
            fileUploadList.add(
                    writeTemplateToFile(newTempFile("loadtest-users.xml"), cfg.getTemplate(TMPL_USER_CONTENT), props));
            fileUploadList.add(
                    writeTemplateToFile(newTempFile("loadtest-group.xml"), cfg.getTemplate(TMPL_GROUP_CONTENT), props));
        } catch( Exception e) {
            throw new Exception("Unable to generate files for upload " + e.getMessage(), e);
        }

        return fileUploadList;
    }

    protected List<File> buildUserFileUploadList() throws Exception {
        List<File> fileUploadList = new ArrayList<File>();
        try {
            Properties props = loadProperties(PROPS_LOCATION, DEFAULT_PROPS_LOCATION);

            String usersArg = System.getProperty("xmlingester.user.list").replace(".", "").replace("-", "").toLowerCase();
            List<XmlIngesterUser> xmlIngesterUsers = new LinkedList<XmlIngesterUser>();
            StringTokenizer token = new StringTokenizer(usersArg, ",");
            while (token.hasMoreTokens()) {
                xmlIngesterUsers.add(new XmlIngesterUser(token.nextToken()));
            }

            props.put("xmlIngesterUsers", xmlIngesterUsers);

            cfg.setObjectWrapper(new DefaultObjectWrapper());

            // build files and add to array
            fileUploadList.add(
                    writeTemplateToFile(newTempFile("userlist-users.xml"), cfg.getTemplate("UserListIngestion.ftl"), props));

        } catch( Exception e) {
            throw new Exception("Unable to generate files for upload " + e.getMessage(), e);
        }

        return fileUploadList;
    }
}

