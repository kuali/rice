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
public class DemoContainerPageGroupAft extends WebDriverLegacyITBase {

    /**
     * http://env14.rice.kuali.org/kr-krad/kradsampleapp?viewId=Demo-PageGroupView
     */
    public static final String BOOKMARK_URL = "http://env14.rice.kuali.org/kr-krad/kradsampleapp?viewId=Demo-PageGroupView";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Page Group");
    }

    protected void testLibraryContainerPageGroup() throws Exception {
        waitForElementPresentByXpath("//div[@id='Demo-PageGroup-Page1']/label");
        waitForElementPresentByXpath("//div[@id='Demo-PageGroup-Page2']/label");
        waitForElementPresentByXpath("//div[@id='Demo-PageGroup-Page3']/label");
    }
    
    @Test
    public void testContainerPageGroupBookmark() throws Exception {
        testLibraryContainerPageGroup();
        passed();
    }

    @Test
    public void testContainerPageGroupNav() throws Exception {
        testLibraryContainerPageGroup();
        passed();
    }  
}
