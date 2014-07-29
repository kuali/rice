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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsLabelAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LabelView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LabelView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Label");
    }

    protected void testLibraryElementsLabel() throws Exception {
        waitAndClickByLinkText("Default");
        assertLabelWithTextPresent("Default Label:");
    }

    protected void testLibraryElementsLabelNoColon() throws Exception {
        waitAndClickByLinkText("No Colon Label");
        assertLabelWithTextPresent("No Colon Label");
        assertTextNotPresent("No Colon Label:"); // make sure Colon is not present
    }

    protected void testLibraryElementsLabelRequiredText() throws Exception {
        waitAndClickByLinkText("Required Message");
        assertLabelWithTextPresent("Label with required message:");
        assertTextPresent("This is required.");
    }

    private void testLabels() throws Exception {
        testLibraryElementsLabel();
        testLibraryElementsLabelNoColon();
        testLibraryElementsLabelRequiredText();
    }

    @Test
    public void testLibraryElementsLabelBookmark() throws Exception {
        testLabels();
        passed();
    }

    @Test
    public void testLibraryElementsLabelNav() throws Exception {
        testLabels();
        passed();
    }
}
