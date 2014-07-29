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
package org.kuali.rice.krad.demo.uif.library.layoutmanagers;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLayoutManagersStackedLayoutAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-StackedLayoutManagerView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-StackedLayoutManagerView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("Stacked Layout");
    }

    protected void testLayoutManagersStackedLayout() throws Exception {
        selectByName("exampleShown","Stacked Layout Manager");
        waitForElementPresentByXpath("//button[@id='Demo-StackedLayoutManager-Collection1_add']");
        assertElementPresentByXpath("//button[@id='Demo-StackedLayoutManager-Collection1_del_line0']");
        assertElementPresentByXpath("//button[@id='Demo-StackedLayoutManager-Collection1_del_line35']");
    }
    
    protected void testLayoutManagersStackedWithTableSubCollectionLayout() throws Exception {
        selectByName("exampleShown","Stacked Collection With Table Sub-Collection");
        waitForElementPresentByXpath("//input[@name='collection4[0].field1' and @value='A']");
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']");
    }
    
    protected void testLayoutManagersStackedWithStackedSubCollectionLayout() throws Exception {
        selectByName("exampleShown","Stacked Collection with a Stacked Sub-Collection");
        waitForElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Collection3']/div[2]/div[2]/table/tbody/tr/td/div/input");
        assertElementPresentByXpath("//section[@data-parent='Demo-StackedLayoutManager-Collection3']/table/tbody/tr[5]/td/div/fieldset/section/div/table");
    }
    
    protected void testLayoutManagersStackedCollectionWithServersidePaging() throws Exception {
        selectByName("exampleShown","Stacked Collection with server-side paging");
        assertElementPresentByXpath("//ul[@class='pagination']");
    }
    
    protected void testLayoutManagersStackedCollectionAjaxDisclosures() throws Exception {
        selectByName("exampleShown","Stacked Collection Ajax Disclosures");
        if(isElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example5']/div[@class='uif-stackedCollectionSection uif-boxLayoutVerticalItem clearfix']/div[3]/div[2]/table/tbody/tr/td/div/input")) {
            jiraAwareFail("Ajax Disclosure Not working!");
        }
        waitAndClickByLinkText("Item 100");
        waitForTextPresent("Loading...");
        waitForElementPresentByXpath("//div[@id='Demo-StackedLayoutManager-Example5']/div[@class='uif-stackedCollectionSection uif-boxLayoutVerticalItem clearfix']/div[3]/div[2]/table/tbody/tr/td/div/input");
    }
    
    protected void testLayoutManagersStackedList() throws Exception {
        selectByName("exampleShown","Stacked List");
        waitForElementPresentByXpath("//input[@name='names[0].field1' and @value='Jack']");
        waitForElementPresentByXpath("//input[@name='names[1].field1' and @value='Joe']");
        waitForElementPresentByXpath("//input[@name='names[2].field1' and @value='John']");
        waitForElementPresentByXpath("//input[@name='names[3].field1' and @value='Jim']");
        waitForElementNotPresent(By.xpath("//input[@name='names[4].field1']"));
        waitAndClickButtonByText("Add Line");
        waitForTextPresent("Loading...");
        waitForElementPresentByXpath("//input[@name='names[4].field1']");
    }
    
    @Test
    public void testLayoutManagersStackedLayoutBookmark() throws Exception {
        testLayoutManagersStackedLayout();
        testLayoutManagersStackedWithTableSubCollectionLayout();
        testLayoutManagersStackedWithStackedSubCollectionLayout();
        testLayoutManagersStackedCollectionWithServersidePaging();
        testLayoutManagersStackedCollectionAjaxDisclosures();
        testLayoutManagersStackedList();
        passed();
    }

    @Test
    public void testLayoutManagersStackedLayoutNav() throws Exception {
        testLayoutManagersStackedLayout();
        testLayoutManagersStackedWithTableSubCollectionLayout();
        testLayoutManagersStackedWithStackedSubCollectionLayout();
        testLayoutManagersStackedCollectionWithServersidePaging();
        testLayoutManagersStackedCollectionAjaxDisclosures();
        testLayoutManagersStackedList();
        passed();
    }  
}
