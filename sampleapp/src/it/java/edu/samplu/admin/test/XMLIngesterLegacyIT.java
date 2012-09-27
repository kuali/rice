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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;

/**
 * tests uploads of new users
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XMLIngesterLegacyIT extends AdminMenuLegacyITBase {

    private File loadUsersFile;
    
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Override
    protected String getLinkLocator() {
        return "XML Ingester";
    }

    @Override
    public void testCreateNewCancel() throws Exception  {}

    @Override
    public void testEditCancel() throws Exception {}

    @Override
    public void setUp() throws Exception {
       super.setUp();
        // generated load users resource for repeated testing
       generateLoadUsersFile(10, ITUtil.DTS);
    }

    /**
     * Generates a temporary file for given number of users and prefix
     * 
     * @param numberOfUsers
     * @param prefix
     * @throws Exception
     */
    private void generateLoadUsersFile(int numberOfUsers, String prefix) throws Exception {
        loadUsersFile = folder.newFile("loadtest-users.xml");
        java.util.Date date= new java.util.Date();

        FileWriter writer = new FileWriter(loadUsersFile);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<data xmlns=\"ns:workflow\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"ns:workflow resource:WorkflowData\">\n");
        writer.write("\t<users xmlns=\"ns:workflow/User\" xsi:schemaLocation=\"ns:workflow/User resource:User\">\n");
        for(int i = 0; i < numberOfUsers; i++) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\t\t<user><principalId>lt" + prefix + i + "</principalId>");
            stringBuffer.append("<emplId>lt" + prefix + i + "emplid</emplId>");
            stringBuffer.append("<principalName>loadtester" + prefix + i + "</principalName>");
            stringBuffer.append("<givenName>Tester</givenName>");
            stringBuffer.append("<lastName>McLoady" + prefix + i + "</lastName>");
            stringBuffer.append("<emailAddress>loadtester" + prefix + i + "@kuali.org</emailAddress>");
            stringBuffer.append("</user>\n");
            writer.write(stringBuffer.toString());
        }
        writer.write("\t</users>\n</data>\n");
        writer.close();
    }

    @Test
    public void testXMLIngesterUpload() throws Exception {
        gotoMenuLinkLocator();
        String path = loadUsersFile.getAbsolutePath().toString();
        driver.findElement(By.name("file[0]")).sendKeys(path);
        waitAndClickByXpath("//*[@id='imageField']");
        assertTextPresent("Ingested xml doc");
    }

}
