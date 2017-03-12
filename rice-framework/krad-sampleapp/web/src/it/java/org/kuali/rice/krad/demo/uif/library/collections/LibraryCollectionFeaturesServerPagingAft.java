/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryCollectionFeaturesServerPagingAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/serverpaging
     */
    public static final String BOOKMARK_URL = "/kr-krad/serverpaging";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Server Paging");
    }

    protected void testCollectionFeaturesServerPagingRichTableCollection() throws Exception {
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr[12]")) {
            fail("More than 10 Elements Present.");
        }
        selectByXpath("//div[@class='dataTables_length']/label/select", "25");
        waitForElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr[12]");
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr[27]")) {
            fail("More than 25 Elements Present.");
        }
        waitAndClickByLinkText("2");
        assertTextPresent("50");
    }
    
    protected void testCollectionFeaturesServerPagingStackedCollection() throws Exception {
        selectByName("exampleShown","Stacked Collection with server-side paging");
        if(isElementPresentByXpath("//div[@class='uif-stackedCollectionLayout']/div[7]")) {
            fail("More than 6 Stack present.");
        }
        if(isElementPresentByXpath("//input[@name='collection2[9].field1']")) {
            fail("Element for second page is present.");
        }
        waitAndClickByLinkText("»");
        waitForElementPresentByXpath("//input[@name='collection2[9].field1']");
     }
    
    protected void testCollectionFeaturesServerPagingBasicTableCollection() throws Exception {
        selectByName("exampleShown","Table Collection with server-side Paging (Basic)");
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine']/tbody/tr[12]")) {
           fail("More than 10 Rows present.");
        }
        waitAndClickByLinkText("Last");
        assertTextPresent("999");
    }

    protected void testCollectionFeaturesStackedCollectionWithNextPager() throws Exception {
        selectByName("exampleShown","Stacked Collection with NextPager");
        waitAndTypeByName("newCollectionLines['collection4'].field1","Z");
        waitAndTypeByName("newCollectionLines['collection4'].field2","999");
        waitAndTypeByName("newCollectionLines['collection4'].field3","999");
        waitAndTypeByName("newCollectionLines['collection4'].field4","999");
        waitAndClickByXpath("//div[@data-parent='Demo-CollectionServerPaging-Example4']/section/div/button[contains(text(),'Add')]");
        waitForElementPresentByXpath("//input[@value='Z']");
        waitForElementPresentByXpath("//input[@value='999']");
        waitForElementPresentByXpath("//li[@class='previous disabled']");
        waitAndClickByXpath("//a[contains(text(),'Next →')]");
        waitForElementPresentByXpath("//li[@class='previous']");
        waitAndClickByXpath("//a[contains(text(),'← Previous')]");
        waitForElementPresentByXpath("//li[@class='previous disabled']");
    }
    
    @Test
    public void testCollectionFeaturesServerPagingBookmark() throws Exception {
        testCollectionFeaturesServerPagingRichTableCollection();
        testCollectionFeaturesServerPagingStackedCollection();
        testCollectionFeaturesServerPagingBasicTableCollection();
        testCollectionFeaturesStackedCollectionWithNextPager();
        passed();
    }

    @Test
    public void testCollectionFeaturesServerPagingNav() throws Exception {
        testCollectionFeaturesServerPagingRichTableCollection();
        testCollectionFeaturesServerPagingStackedCollection();
        testCollectionFeaturesServerPagingBasicTableCollection();
        testCollectionFeaturesStackedCollectionWithNextPager();
        passed();
    }  
}
