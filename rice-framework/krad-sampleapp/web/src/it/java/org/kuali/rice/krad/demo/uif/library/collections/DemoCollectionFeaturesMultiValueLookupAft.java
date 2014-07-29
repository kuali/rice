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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesMultiValueLookupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LightTableView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionLookupView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        jGrowl("Click the Library Link");
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Multi-Value Lookup");
    }

    protected void testMultiValueLookup() throws InterruptedException {
        lightBoxLookupAddMultipleLines();

        waitAndTypeByName("lookupCriteria[number]", "a1");
        waitAndClickSearchByText();
        assertButtonDisabledByText("return selected");

        jGrowl("Check Travel Account 1 checkbox");
        waitAndClickByValue("IAT:Travel Account 1:a1");
        assertButtonEnabledByText("return selected");

        jGrowl("Uncheck Travel Account 1 checkbox");
        waitAndClickByValue("IAT:Travel Account 1:a1");
        assertButtonDisabledByText("return selected");

        waitAndClickButtonByText("return selected");
        Thread.sleep(3000);
        assertTextPresent("a1"); // TODO better assertion once NullPointer is resolved
    }

    private void lightBoxLookupAddMultipleLines() throws InterruptedException {
        waitAndClickByLinkText("Lookup/Add Multiple Lines");
        gotoLightBox();
        gotoLightBoxIframe();
    }

    @Test
    public void testMultiValueLookupBookmark() throws InterruptedException {
        testMultiValueLookup();
        passed();
    }

    @Test
    public void testMultiValueLookupNav() throws InterruptedException {
        testMultiValueLookup();
        passed();
    }

    @Test
    public void testMultivalueLookUpSelectThisPageBookmark() throws Exception {
        lightBoxLookupAddMultipleLines();
        testMultiValueSelectAllThisPage();
        passed();
    }

    @Test
    public void testMultivalueLookUpSelectThisPageNav() throws Exception {
        lightBoxLookupAddMultipleLines();
        testMultiValueSelectAllThisPage();
        passed();
    }

    @Test
    public void testMultivalueLookUpSelectAllPagesBookmark() throws Exception {
        lightBoxLookupAddMultipleLines();
        testMultiValueSelectAllPages();
        passed();
    }

    @Test
    public void testMultivalueLookUpSelectAllPagesNav() throws Exception {
        lightBoxLookupAddMultipleLines();
        testMultiValueSelectAllPages();
        passed();
    }
}
