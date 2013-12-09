/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.library.containers;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoContainerGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-GroupView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-GroupView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Group");
    }

    protected void testLibraryContainerGroupBasic() throws Exception {
        selectByName("exampleShown","Basic");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-verticalBoxLayout clearfix']/div/input");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div[2]/table");
    
    }
    
    protected void testLibraryContainerGroupSections() throws Exception {
        selectByName("exampleShown","Sections");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/h3");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/h4");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-verticalBoxLayout clearfix']/div[2]/div/div/h4");
    }
    
    protected void testLibraryContainerGroupDisclosure() throws Exception {
        selectByName("exampleShown","Disclosure");
        waitAndClickByLinkText("Disclosure Section");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@data-role='disclosureContent' and @style='display: none;']");
        waitAndClickByLinkText("Disclosure Section");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@data-role='disclosureContent' and @style='display: block;']");
        waitAndClickByLinkText("Predefined Disclosure Section");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div[2]/div[@data-role='disclosureContent' and @style='display: none;']");
        waitAndClickByLinkText("Predefined Disclosure Section");
        assertElementPresentByXpath("//div[@id='Demo-Group-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div[2]/div[@data-role='disclosureContent' and @style='display: block;']");
    }
    
    protected void testLibraryContainerGroupScrollpane() throws Exception {
        selectByName("exampleShown","Scrollpane");
        assertElementPresentByXpath("//div[@style='height: 100px;overflow: auto;']");
    }

    private void testAllGroups() throws Exception {
        testLibraryContainerGroupBasic();
        testLibraryContainerGroupSections();
        testLibraryContainerGroupDisclosure();
        testLibraryContainerGroupScrollpane();
    }

    @Test
    public void testContainerGroupBookmark() throws Exception {
        testAllGroups();
        passed();
    }

    @Test
    public void testContainerGroupNav() throws Exception {
        testAllGroups();
        passed();
    }  
}
