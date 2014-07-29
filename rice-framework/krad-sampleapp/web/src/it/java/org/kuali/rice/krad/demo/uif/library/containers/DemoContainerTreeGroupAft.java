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
public class DemoContainerTreeGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TreeGroupView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TreeGroupView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Tree Group");
    }

    protected void testLibraryContainerTreeGroup() throws Exception {
       waitAndClickByXpath("//div[@data-parent='Demo-TreeGroup-Example1']/div/ul/li[@class='jstree-closed']/ins");
       waitForElementPresentByXpath("//div[@data-parent='Demo-TreeGroup-Example1']/div/ul/li[@class='jstree-open']");
       waitAndClickByLinkText("Tree With Data Group");
       assertElementPresentByXpath("//div[@data-parent='Demo-TreeGroup-Example2']/div/ul/li[@class='jstree-closed']/div[@class='uif-verticalBoxGroup']");
    }
    
    @Test
    public void testContainerTreeGroupBookmark() throws Exception {
        testLibraryContainerTreeGroup();
        passed();
    }

    @Test
    public void testContainerTreeGroupNav() throws Exception {
        testLibraryContainerTreeGroup();
        passed();
    }  
}
