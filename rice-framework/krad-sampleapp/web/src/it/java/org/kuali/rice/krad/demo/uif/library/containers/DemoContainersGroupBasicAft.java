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

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoContainersGroupBasicAft extends DemoLibraryBase {

    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=ComponentLibraryHome";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
    }

    protected void navigationMenu() throws Exception {
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Group");
    }

    @Test
    public void testBasicGroupNav() throws Exception {
        testBasicGroupBookmark();
    }

    /**
     * Asserts basic group elements are present: header, validation messages,
     * instructional text, and the actual items
     *
     * @throws Exception
     */
    @Test
    public void testBasicGroupBookmark() throws Exception {
        navigationMenu();
        waitForElementPresentByXpath("//div[@id='Demo-Group-Example1']/div/h3");
        waitForElementPresentByXpath("//div[@id='Demo-Group-Example1']/span[@class='uif-instructionalMessage']");
        assertElementPresentByXpath("//input[@name='inputField1']");
        assertElementPresentByXpath("//input[@name='inputField2']");
        passed();
    }
}
