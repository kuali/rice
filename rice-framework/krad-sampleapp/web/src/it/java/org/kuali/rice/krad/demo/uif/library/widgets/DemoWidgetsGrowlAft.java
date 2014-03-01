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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsGrowlAft extends DemoLibraryBase {

	   /**
     * /kr-krad/kradsampleapp?viewId=Demo-GrowlsView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-GrowlsView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Growls");
    }

    protected void testWidgetsGrowlServerSide() throws Exception {
        selectByName("exampleShown","Server-side Growls");

        //check if growl text is present
        assertTextNotPresent("Sample Message Text. Data passed: none");

        //create growl
        waitAndClickButtonByText("Growl");

        //give it a second to display
        Thread.sleep(1000);

        // get growl text
        String growlText = findElement(By.className("jGrowl-message")).getText();

        //check growl text is present
        assertTrue(growlText.equals("Sample Message Text. Data passed: none"));
    }

    protected void testWidgetsGrowlClientSide() throws Exception {
        selectByName("exampleShown","Client-side Growls");

        //create growl
        waitAndClickByXpath("//section[@id='Demo-Growls-Example2']/button");

        //give it a half second to display
        Thread.sleep(500);

        // get growl text and assert it is what we expect
        String growlText = findElement(By.className("growlUI")).getText();
        assertTrue(growlText.equals("Growl Test\nThis is a test growl message"));
    }

    @Test
    public void testWidgetsGrowlBookmark() throws Exception {
        testWidgetsGrowlClientSide();
        testWidgetsGrowlServerSide();
        passed();
    }

    @Test
    public void testWidgetsGrowlNav() throws Exception {
        testWidgetsGrowlClientSide();
        testWidgetsGrowlServerSide();
        passed();
    }
}
