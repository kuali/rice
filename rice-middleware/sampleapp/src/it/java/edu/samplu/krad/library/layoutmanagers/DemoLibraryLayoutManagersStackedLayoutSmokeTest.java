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
package edu.samplu.krad.library.layoutmanagers;

import org.junit.Test;

import edu.samplu.common.SmokeTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryLayoutManagersStackedLayoutSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-StackedLayoutManager-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-StackedLayoutManager-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("Stacked Layout");
    }

    protected void testLayoutManagersStackedLayout() throws Exception {
       waitForElementPresentByXpath("//div[@class='uif-collectionItem uif-gridCollectionItem uif-collectionAddItem']/div[@class='uif-footer']/div/button[contains(text(),'add')]");
       assertElementPresentByXpath("//div[@class='uif-collectionItem uif-gridCollectionItem']/div[@class='uif-footer']/div/button[contains(text(),'delete')]");
       assertElementPresentByXpath("//div[@class='uif-collectionItem uif-gridCollectionItem'][35]/div[@class='uif-footer']/div/button[contains(text(),'delete')]");
    }
    
    protected void testLayoutManagersStackedWithTableSubCollectionLayout() throws Exception {
        selectByName("exampleShown","Stacked Collection With Table Sub-Collection");
        assertElementPresentByXpath("//div[@class='uif-stackedCollectionLayout']/div[@class='uif-collectionItem uif-gridCollectionItem']/table/tbody/tr/td/div/input[@name='collection4[0].field1']");
        assertElementPresentByXpath("//div[@class='uif-stackedCollectionLayout']/div[@class='uif-collectionItem uif-gridCollectionItem']/div[@class='uif-footer']/div/button[contains(text(),'Update Official')]");
    }
    
    protected void testLayoutManagersStackedWithStackedSubCollectionLayout() throws Exception {
        selectByName("exampleShown","Stacked Collection with a Stacked Sub-Collection");
        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection3_disclosureContent']/div/div/table/tbody/tr[3]/td/div/input");
        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-SubCollection2']/div[@class='uif-stackedCollectionLayout']/div/table");
    }
    
    protected void testLayoutManagersStackedCollectionWithServersidePaging() throws Exception {
        selectByName("exampleShown","Stacked Collection with server-side paging");
        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='pagination']/ul/li[4]/a");
    }
    
    protected void testLayoutManagersStackedCollectionAjaxDisclosures() throws Exception {
        selectByName("exampleShown","Stacked Collection Ajax Disclosures");
        if(isElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-stackedCollectionLayout']/div[@class='uif-collectionItem uif-gridCollectionItem']/div[@class='uif-disclosureContent']/table"))
        {
            fail("Ajax Disclosure Not working!");
        }
        waitAndClickByLinkText("Item 100");
        Thread.sleep(3000);
        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-stackedCollectionLayout']/div[@class='uif-collectionItem uif-gridCollectionItem']/div[@class='uif-disclosureContent']/table");
    }
    
    @Test
    public void testLayoutManagersStackedLayoutBookmark() throws Exception {
        testLayoutManagersStackedLayout();
        testLayoutManagersStackedWithTableSubCollectionLayout();
        testLayoutManagersStackedWithStackedSubCollectionLayout();
        testLayoutManagersStackedCollectionWithServersidePaging();
        testLayoutManagersStackedCollectionAjaxDisclosures();
        passed();
    }

    @Test
    public void testLayoutManagersStackedLayoutNav() throws Exception {
        testLayoutManagersStackedLayout();
        testLayoutManagersStackedWithTableSubCollectionLayout();
        testLayoutManagersStackedWithStackedSubCollectionLayout();
        testLayoutManagersStackedCollectionWithServersidePaging();
        testLayoutManagersStackedCollectionAjaxDisclosures();
        passed();
    }  
}