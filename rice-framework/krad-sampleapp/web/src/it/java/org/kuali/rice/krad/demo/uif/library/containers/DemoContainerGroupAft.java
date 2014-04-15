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
        selectByName("exampleShown","Basic Grid");
        assertElementPresentByXpath("//input[@name='inputField1']");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example1']/div/table");
    }
    
    protected void testLibraryContainerGroupBasicVerticalBox() throws Exception {
        selectByName("exampleShown","Basic Vertical Box");
        assertElementPresentByXpath("//input[@name='inputField3']");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example2']/div");
    }
    
    protected void testLibraryContainerGroupBasicCssGrid() throws Exception {
        selectByName("exampleShown","Basic CSS Grid");
        assertElementPresentByXpath("//input[@name='inputField5']");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example3']/div");
    }
    
    protected void testLibraryContainerGroupSectionVertical() throws Exception {
        selectByName("exampleShown","Section Vertical");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example4']/header/h3");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example4']/section/header/h3/span[contains(text(),'Section 1')]");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example4']/section/header/h3/span[contains(text(),'Section 2')]");
    }
    
    protected void testLibraryContainerGroupSectionHorizontal() throws Exception {
        selectByName("exampleShown","Section Horizontal");
        assertElementPresentByXpath("//main[@id='Demo-Group-Example5']/header/h2");
        assertElementPresentByXpath("//main[@id='Demo-Group-Example5']/div/div/label[contains(text(),'Field 1:')]");
        assertElementPresentByXpath("//main[@id='Demo-Group-Example5']/div/div[2]/label[contains(text(),'Field 1:')]");
    }
    
    protected void testLibraryContainerGroupSubSection() throws Exception {
        selectByName("exampleShown","Section Horizontal");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example6']/header/h3");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example6']/section/header/h3/span[contains(text(),'Section 1')]");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example6']/section/section/header/h4/span[contains(text(),'SubSection 1')]");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example6']/section/section/header/h4/span[contains(text(),'SubSection 2')]");
        assertElementPresentByXpath("//section[@id='Demo-Group-Example6']/section/header/h3/span[contains(text(),'Section 2')]");
    }
    
    protected void testLibraryContainerGroupDisclosure() throws Exception {
        selectByName("exampleShown","Disclosure");
        waitAndClickByLinkText("Disclosure Section");
        waitForElementPresentByXpath("//section[@id='Demo-Group-Example7']/section/div[@class='uif-disclosureContent' and @style='overflow: hidden; display: none;']");
        waitAndClickByLinkText("Disclosure Section");
        waitForElementPresentByXpath("//section[@id='Demo-Group-Example7']/section/div[@class='uif-disclosureContent' and @style='overflow: hidden; display: block;']");
        waitAndClickByLinkText("Predefined Disclosure Section");
        waitForElementPresentByXpath("//section[@id='Demo-Group-Example7']/section[2]/div[@class='uif-disclosureContent' and @style='overflow: hidden; display: none;']");
        waitAndClickByLinkText("Predefined Disclosure Section");
        waitForElementPresentByXpath("//section[@id='Demo-Group-Example7']/section[2]/div[@class='uif-disclosureContent' and @style='overflow: hidden; display: block;']");
    }
    
    protected void testLibraryContainerGroupScrollpane() throws Exception {
        selectByName("exampleShown","Scrollpane");
        waitForElementPresentByXpath("//section[@id='Demo-Group-Example8']/div[@style='height: 100px;overflow: auto;']");
    }

    private void testAllGroups() throws Exception {
        testLibraryContainerGroupBasic();
        testLibraryContainerGroupBasicVerticalBox();
        testLibraryContainerGroupBasicCssGrid();
        testLibraryContainerGroupSectionVertical();
        testLibraryContainerGroupSectionHorizontal();
        testLibraryContainerGroupSubSection();
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
