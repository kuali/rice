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
package org.kuali.rice.krad.library.controls;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.SmokeTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryControlSelectSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-SelectControl-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-SelectControl-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Select");
    }

    protected void testLibraryControlSelectDefault() throws Exception {
        assertElementPresentByXpath("//select[@name='inputField1']");
    }
    
    protected void testLibraryControlSelectMultiSelect() throws Exception {
        waitAndClickByLinkText("MultiSelect");
        assertElementPresentByXpath("//select[@name='inputField2' and @multiple='multiple']");
    }
    
    protected void testLibraryControlSelectDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        assertElementPresentByXpath("//select[@name='inputField1' and @disabled='disabled']");
    }
    
    protected void testLibraryControlSelectNavigation() throws Exception {
        waitAndClickByXpath("//li[@data-tabfor='Demo-SelectControl-Example4']/a");
        assertElementPresentByXpath("//div[@data-parent='Demo-SelectControl-Example4']/select/option[@data-location='http://www.kuali.org']");
    }
    
    @Test
    public void testControlSelectBookmark() throws Exception {
        testLibraryControlSelectDefault();
        testLibraryControlSelectMultiSelect();
        testLibraryControlSelectDisabled();
        testLibraryControlSelectNavigation();
        passed();
    }

    @Test
    public void testControlSelectNav() throws Exception {
        testLibraryControlSelectDefault();
        testLibraryControlSelectMultiSelect();
        testLibraryControlSelectDisabled();
        testLibraryControlSelectNavigation();
        passed();
    }  
}