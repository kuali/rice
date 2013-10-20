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
package org.kuali.rice.krad.demo.uif.library.layoutmanagers;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLayoutManagersStackedLayoutAft extends AutomatedFunctionalTestBase {

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
        waitForElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection2']/div[2]/div[2]/div/table/tbody/tr/td/div/input");
        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection2']/div[2]/div[2]/div[@class='uif-collectionItem uif-gridCollectionItem']/table");
//        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection2']/div[2]/div[3]/table/tbody/tr/td/div/input[@name='collection4[1].field1']");
    }
    
    protected void testLayoutManagersStackedWithStackedSubCollectionLayout() throws Exception {
        selectByName("exampleShown","Stacked Collection with a Stacked Sub-Collection");
        waitForElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection3']/div[2]/div[2]/div[@class='uif-collectionItem uif-gridCollectionItem']/table/tbody/tr/td/div/input");
        assertElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection3']/div[2]/div[2]/div[@class='uif-collectionItem uif-gridCollectionItem']/table/tbody/tr[5]/td/div/fieldset/div/div[3]/div/table");
    }
    
    protected void testLayoutManagersStackedCollectionWithServersidePaging() throws Exception {
        selectByName("exampleShown","Stacked Collection with server-side paging");
        assertElementPresentByXpath("//ul[@class='pagination']");
    }
    
    protected void testLayoutManagersStackedCollectionAjaxDisclosures() throws Exception {
        selectByName("exampleShown","Stacked Collection Ajax Disclosures");
        if(isElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/div[3]/div[@class='uif-collectionItem uif-gridCollectionItem']/div[@class='uif-disclosureContent']/table/tbody/tr/td/div/input[@name='groupedCollection3[0].field1']"))
        {
            fail("Ajax Disclosure Not working!");
        }
        waitAndClickByLinkText("Item 100");
        Thread.sleep(3000);
        waitForElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/div[3]/div[@class='uif-collectionItem uif-gridCollectionItem']/div[@class='uif-disclosureContent']/table/tbody/tr/td/div/input[@name='groupedCollection3[0].field1']");
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
