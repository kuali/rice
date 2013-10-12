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
package edu.samplu.krad.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.SmokeTestBase;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsLabelSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Label-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-Label-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Label");
    }

    protected void testLibraryElementsLabel() throws Exception {
        assertElementPresentByXpath("//div[@id='Demo-Label-Example1']/div[@class='uif-verticalBoxLayout clearfix']/span/label");
        assertTextPresent("Default Label:");
    }

    protected void testLibraryElementsLabelNoColon() throws Exception {
        waitAndClickByLinkText("No Colon Label");
        assertElementPresentByXpath("//div[@id='Demo-Label-Example2']/div[@class='uif-verticalBoxLayout clearfix']/span/label");
        assertTextPresent("No Colon Label");
        assertTextNotPresent("No Colon Label:");
    }

    protected void testLibraryElementsLabelRequiredText() throws Exception {
        waitAndClickByLinkText("Required Message");
        assertElementPresentByXpath("//div[@id='Demo-Label-Example2']/div[@class='uif-verticalBoxLayout clearfix']/span/label");
        assertTextPresent("This is required");
    }

    @Test
    public void testLibraryElementsLabelBookmark() throws Exception {
        testLibraryElementsLabel();
        testLibraryElementsLabelNoColon();
        testLibraryElementsLabelRequiredText();
        passed();
    }

    @Test
    public void testLibraryElementsLabelNav() throws Exception {
        testLibraryElementsLabel();
        testLibraryElementsLabelNoColon();
        testLibraryElementsLabelRequiredText();
        passed();
    }  
}