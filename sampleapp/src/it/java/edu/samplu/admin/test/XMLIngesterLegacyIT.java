/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * tests uploads of new users and group
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XMLIngesterLegacyIT extends AdminMenuLegacyITBase {

    // values set by default for repeatable testing; left as configurable for load tests
    private List<File> fileUploadList;
    private int userCnt = Integer.valueOf(System.getProperty("test.xmlingester.user.cnt", "10"));
    private boolean userPadding = Boolean.valueOf(System.getProperty("test.xmlingester.user.padding", "true"));
    private String userPrefix = System.getProperty("test.xmlingester.user.prefix", ITUtil.DTS);
    private String emailDomain = System.getProperty("test.xmlingester.user.email.domain", "@kuali.org");
    // group default values
    private String groupId = System.getProperty("test.xmlingester.grp.id", "2203");
    private String groupNamespace = System.getProperty("test.xmlingester.grp.namespace","KUALI");
    private String groupName = System.getProperty("test.xmlingester.grp.name", "eDoc.Example1.IUPUI.Workgroup");
    private String groupDesc = System.getProperty("test.xmlingester.grp.desc", "Edoclite Documentation workgroup");

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Ignore
    @Override
    public void testCreateNewCancel() throws Exception {}

    @Ignore
    @Override
    public void testEditCancel() throws Exception {}


    @Override
    protected String getLinkLocator() {
        return "XML Ingester";
    }

    @Override
    public String getUserName() {
        return "admin"; // xml ingestion requires admin permissions
    }

    @Override
    public void setUp() throws Exception {
       super.setUp();
        // generated load users and group resources
       buildFileUploadList();

    }

    private void buildFileUploadList() throws Exception {
        fileUploadList = new ArrayList<File>();
        fileUploadList.add(generateLoadUsersFile(userCnt, userPrefix));
        fileUploadList.add(generateLoadGroupFile(userCnt, userPrefix));
    }

    /**
     * Generates a temporary file for given number of users and prefix
     * 
     * @param numberOfUsers
     * @param prefix
     * @throws Exception
     */
    private File generateLoadUsersFile(int numberOfUsers, String prefix) throws Exception {
        File loadUsersFile = folder.newFile("loadtest-users.xml");

        java.util.Date date= new java.util.Date();

        FileWriter writer = new FileWriter(loadUsersFile);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<data xmlns=\"ns:workflow\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"ns:workflow resource:WorkflowData\">\n");
        writer.write("\t<users xmlns=\"ns:workflow/User\" xsi:schemaLocation=\"ns:workflow/User resource:User\">\n");
        String count = "";
        String format = "%0" + (numberOfUsers + "").length() + "d";
        for(int i = 0; i < numberOfUsers; i++) {
            if (userPadding) {
                count = prefix + String.format(format, i);
            } else {
                count = prefix + i;
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\t\t<user><principalId>lt" + count + "</principalId>");
            stringBuffer.append("<emplId>lt" + count + "emplid</emplId>");
            stringBuffer.append("<principalName>loadtester" + count + "</principalName>");
            stringBuffer.append("<givenName>Tester" + count + "</givenName>");
            stringBuffer.append("<lastName>McLoady" + count + "</lastName>");
            stringBuffer.append("<emailAddress>loadtester" + count + emailDomain + "</emailAddress>");
            stringBuffer.append("</user>\n");
            writer.write(stringBuffer.toString());
        }
        writer.write("\t</users>\n</data>\n");
        writer.close();
        return loadUsersFile;
    }

    /**
     *  Generates a temporary file for a group given number of users and principal name prefix
     *
     *
     * @param numberOfUsers
     * @param prefix
     * @return
     * @throws Exception
     */
    private File generateLoadGroupFile(int numberOfUsers, String prefix) throws Exception {
        File loadGroupFile = folder.newFile("loadtest-group.xml");

        FileWriter writer = new FileWriter(loadGroupFile);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<data xmlns=\"ns:workflow\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"ns:workflow resource:WorkflowData\">\n");
        writer.write("\t<groups xmlns=\"ns:workflow/Group\" xsi:schemaLocation=\"ns:workflow/Group resource:Group\">\n");
        writer.write("\t\t<group><id>" + groupId + "</id><namespace>" + groupNamespace + "</namespace><description>" + groupDesc + "</description>");
        writer.write("<name>" + groupName + "</name>");
        writer.write("<members>");
        writer.write("<principalName>admin</principalName>");
        writer.write("<principalName>notsys</principalName>");
        String count = "";
        String format = "%0" + (numberOfUsers + "").length() + "d";
        for(int i = 0; i < numberOfUsers; i++) {
            if (userPadding) {
                count = prefix + String.format(format, i);
            } else {
                count = prefix + i;
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<principalName>loadtester" + count + "</principalName>");
            writer.write(stringBuffer.toString());
        }
        writer.write("\t\t</members>\n\t</group>\n</groups>\n</data>\n");
        writer.close();
        return loadGroupFile;
    }


    /**
     * Based on load user and groups manual tests; load a dynamically generated user and group file into the xml ingester screen
     *
     */
    @Test
    public void testXMLIngesterSuccessfulFileUpload() throws Exception {
        gotoMenuLinkLocator();
        int cnt = 0;
        for(File file : fileUploadList) {
            String path = file.getAbsolutePath().toString();
            driver.findElement(By.name("file[" + cnt + "]")).sendKeys(path);
            cnt++;
        }
        waitAndClickByXpath("//*[@id='imageField']");

        // confirm all files were uploaded successfully
        for(File file: fileUploadList) {
            assertTextPresent("Ingested xml doc: " + file.getName());
        }
        passed();
    }

}
