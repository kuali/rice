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
package org.kuali.rice.krad.demo.uif.library.controls;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoControlKimGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-KIMGroupControlView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-KIMGroupControlView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("KIM Group");
    }

    protected void testLibraryControlKIMGroupDefault() throws Exception {
        waitAndTypeByName("inputField1","TestGroup1");
    }
    
    protected void testLibraryControlKIMGroupWidgetInputOnly() throws Exception {
        waitAndClickByLinkText("Widget Input Only");
        waitAndClickByXpath("//div[@data-label='Name' and @data-parent='Demo-KIMGroupControl-Example2']/div/button");
        gotoIframeByXpath("//iframe[@class='fancybox-iframe']");
        waitAndClickSearchByText();
        waitAndClickByLinkText("return value");
    }
    
    @Test
    public void testControlKIMGroupBookmark() throws Exception {
        testLibraryControlKIMGroupDefault();
        testLibraryControlKIMGroupWidgetInputOnly();
        passed();
    }

    @Test
    public void testControlKIMGroupNav() throws Exception {
        testLibraryControlKIMGroupDefault();
        testLibraryControlKIMGroupWidgetInputOnly();
        passed();
    }  
}
