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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoClientResponsivenessConditionalRefreshAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ConditionalRefreshView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ConditionalRefreshView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Conditional Refresh");
    }

    protected void testClientResponsivenessConditionalRefresh() throws Exception {
       //Scenario-1
      assertIsVisibleByXpath("//input[@name='inputField1']", "Is Visible");
      waitAndClickByName("booleanField1");
      assertIsVisibleByXpath("//input[@name='inputField1']","Visible");
    }
    
    protected void testClientResponsivenessConditionalRefreshWhenChanged() throws Exception {
        waitAndClickByLinkText("Refresh when Changed");
        assertElementPresentByXpath("//input[@name='inputField3']", "element");
        waitAndClickByXpath("//input[@name='inputField2' and @value='input']");
        waitAndTypeByName("inputField3","asd");
        waitAndClickByXpath("//input[@name='inputField2' and @value='readOnly']");
        isTextPresent("asd");
    }
    
    protected void testClientResponsivenessConditionalRefreshRefreshAdvancedExample() throws Exception {
        waitAndClickByLinkText("Advanced Example");
        selectByName("inputField5","Apple");
        selectByName("inputField4","Vegetables");
        waitAndSelectByName("inputField5","Beans");
    }
    
    @Test
    public void testClientResponsivenessConditionalRefreshBookmark() throws Exception {
        testClientResponsivenessConditionalRefresh();
        testClientResponsivenessConditionalRefreshWhenChanged();
        testClientResponsivenessConditionalRefreshRefreshAdvancedExample();
        passed();
    }

    @Test
    public void testClientResponsivenessConditionalRefreshNav() throws Exception {
        testClientResponsivenessConditionalRefresh();
        testClientResponsivenessConditionalRefreshWhenChanged();
        testClientResponsivenessConditionalRefreshRefreshAdvancedExample();
        passed();
    }  
}
