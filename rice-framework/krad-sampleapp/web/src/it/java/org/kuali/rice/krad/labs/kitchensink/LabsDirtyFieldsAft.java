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
package org.kuali.rice.krad.labs.kitchensink;

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.Test;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsDirtyFieldsAft extends LabsKitchenSinkBase {

    /**
     * "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToKitchenSink("Input Fields");
    }

    @Test
    public void testDirtyFieldsCheckBookmark() throws Exception {
        testDirtyFieldsCheck();
        passed();
    }

    @Test
    public void testDirtyFieldsCheckNav() throws Exception {
        testDirtyFieldsCheck();
        passed();
    }

    protected void testDirtyFieldsCheck() throws Exception {
        waitForPageToLoad();

        waitAndTypeByName("field1", "test 1");
        waitAndTypeByName("field102", "test 2");
        assertCancelConfirmation();

        // testing manually
        waitForElementPresentByName("field100");
        waitAndTypeByName("field100", "here");
        waitAndTypeByName("field103", "there");

        // 'Validation' navigation link
        assertCancelConfirmation();

        // testing manually
        waitForElementPresentByName("field106");

        // //Asserting text-field style to uppercase. This style would display
        // input text in uppercase.
        assertTrue(waitAndGetAttributeByName("field112", "style").contains("text-transform: uppercase;"));
        assertCancelConfirmation();
        waitForElementPresentByName("field101");
        assertEquals("val", waitAndGetAttributeByName("field101", "value"));
        clearTextByName("field101");
        waitAndTypeByName("field101", "1");
        waitAndTypeByName("field104", "");
        SeleneseTestBase.assertEquals("1", waitAndGetAttributeByName("field101", "value"));
        waitAndTypeByName("field104", "2");

        // 'Progressive Disclosure' navigation link
        assertCancelConfirmation();
    }

    private void assertCancelConfirmation() throws InterruptedException {
        waitAndClickCancelByText();
        alertDismiss();
    }

}
