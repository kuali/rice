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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLayoutManagersCssGridLayoutAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CssGridLayoutManagerView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CssGridLayoutManagerView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("Css Grid Layout");
    }

    protected void testLayoutManagersCssGridLayoutDefault() throws Exception {
        selectByName("exampleShown", "Default");
        waitForElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example1']/div[@class='col-md-4']/div/p");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example1']/div[@class='col-md-4']/div/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example1']/div[@class='col-md-4']/div/input");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example1']/div[@class='col-md-6']/div/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example1']/div[@class='col-md-6']/div/input");
    }
    
    protected void testLayoutManagersCssGridLayoutDefaultWidthBehaviour() throws Exception {
        selectByName("exampleShown", "Default width behavior");
        waitForElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example2']/div[@class='col-md-12']");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example2']/div[@class='md-clear-left col-md-8']");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example2']/div[@class='col-md-4']");
     }
    
    protected void testLayoutManagersCssGridLayoutFieldLabelColumns() throws Exception {
        selectByName("exampleShown", "Field Label Columns");
        waitForElementPresentByXpath("//section[@data-parent='Demo-CssGridLayoutManager-Example3']/div[@class='col-md-3 uif-cssGridLabelCol']");
        assertElementPresentByXpath("//section[@data-parent='Demo-CssGridLayoutManager-Example3']/div[@class='col-md-2 uif-cssGridLabelCol']");
        assertElementPresentByXpath("//section[@data-parent='Demo-CssGridLayoutManager-Example3']/div[@class='col-md-4']");
     }
    
    protected void testLayoutManagersCssGridLayoutDefaultColspan() throws Exception {
        selectByName("exampleShown", "Default ColSpan");
        waitForElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example6']/div[@class='col-md-4']");
    }
    
    protected void testLayoutManagersCssGridLayoutGroupLayout() throws Exception {
        selectByName("exampleShown", "Group Layout");
        waitForElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example7']/div[@class='col-md-4']/section[@class='uif-boxSection' and @style='background-color: #00CC66; height: 200px;']");
        assertElementPresentByXpath("//div[@data-parent='Demo-CssGridLayoutManager-Example7']/div[@class='col-md-8']/section[@class='uif-boxSection' and @style='background-color: #00CCFF; height: 200px;']");
     }
    
    @Test
    public void testLayoutManagersCssGridLayoutBookmark() throws Exception {
    	testLayoutManagersCssGridLayoutAll();
    }

    @Test
    public void testLayoutManagersCssGridLayoutNav() throws Exception {
    	testLayoutManagersCssGridLayoutAll();
    }  
    
    private void testLayoutManagersCssGridLayoutAll() throws Exception {
    	testLayoutManagersCssGridLayoutDefault();
        testLayoutManagersCssGridLayoutDefaultWidthBehaviour();
        testLayoutManagersCssGridLayoutFieldLabelColumns();
        testLayoutManagersCssGridLayoutDefaultColspan();
        testLayoutManagersCssGridLayoutGroupLayout();
        passed();
    }
}
