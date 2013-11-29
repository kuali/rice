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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;

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

        //check if growl text is present
        assertTextNotPresent("Sample Message Text. Data passed: none");

        //create growl
        waitAndClickButtonByText("Growl");

        //give it a second to display
        Thread.sleep(1000);

        //check growl text is present
        assertTextPresent("Sample Message Text. Data passed: none");
    }

    protected void testWidgetsGrowlClientSide() throws Exception {
        selectByName("exampleShown","Client-side Growls");
        waitAndClickByXpath("//div[@id='Demo-Growls-Example2']/div[@class='uif-verticalBoxLayout clearfix']/button");
        Thread.sleep(500);
        assertTextPresent("This is a test growl message");
    }

    @Test
    public void testWidgetsGrowlBookmark() throws Exception {
        testWidgetsGrowlServerSide();
        testWidgetsGrowlClientSide();
        passed();
    }

    @Test
    public void testWidgetsGrowlNav() throws Exception {
        testWidgetsGrowlServerSide();
        testWidgetsGrowlClientSide();
        passed();
    }
}
