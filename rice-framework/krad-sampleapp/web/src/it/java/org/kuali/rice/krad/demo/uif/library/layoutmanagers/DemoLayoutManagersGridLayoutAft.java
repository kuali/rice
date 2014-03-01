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
public class DemoLayoutManagersGridLayoutAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-GridLayoutManagerView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-GridLayoutManagerView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("Grid Layout");
    }

    protected void testLayoutManagersGridLayout() throws Exception {
       assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example1']/table/tbody/tr/th/label");
       assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example1']/table/tbody/tr/td/div/input");
       assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example1']/table/tbody/tr[2]/th/label");
       assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example1']/table/tbody/tr[2]/td/div/input");
       assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example1']/table/tbody/tr[3]/th/label");
       assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example1']/table/tbody/tr[3]/td/div/input");
    }
    
    protected void testLayoutManagersGridLayouMultipleColumns() throws Exception {
        selectByName("exampleShown","# of Columns");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/th/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/td/div/input[@name='inputField4']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/th[2]/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/td[2]/div/input[@name='inputField5']");
    }
    
    protected void testLayoutManagersGridLayoutColumnSpan() throws Exception {
        selectByName("exampleShown","Column Span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/th[@colspan='2']/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/td[@colspan='2']/div/input[@name='inputField8']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/th[@colspan='1']/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/td[@colspan='1']/div/input[@name='inputField9']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/th[@colspan='1'][2]/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/td[@colspan='1'][2]/div/input[@name='inputField10']");
    }
    
    protected void testLayoutManagersGridLayoutRenderTHColumn() throws Exception {
        selectByName("exampleShown","Render TH Column");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example4']/table/tbody/tr/td/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example4']/table/tbody/tr/td/div/input[@name='inputField12']");
    }
    
    protected void testLayoutManagersGridLayoutRowSpan() throws Exception {
        selectByName("exampleShown","Row Span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example5']/table/tbody/tr/td[@rowspan='2']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example5']/table/tbody/tr/td[@rowspan='1']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example5']/table/tbody/tr/td[@rowspan='3']");
    }
    
    protected void testLayoutManagersGridLayoutFieldGroup() throws Exception {
        selectByName("exampleShown","Field Group");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example6']/table/tbody/tr/td/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example6']/table/tbody/tr/td/div[@class='uif-horizontalFieldGroup']");
    }
    
    protected void testLayoutManagersGridLayoutNestedLayout() throws Exception {
        selectByName("exampleShown","Nested Layout");
        assertElementPresentByXpath("//section[@id='Demo-GridLayoutManager-Example7']/div/table/tbody/tr/th[@class='uif-gridLayoutCell']/section/table");
        assertElementPresentByXpath("//section[@id='Demo-GridLayoutManager-Example7']/div/table/tbody/tr/td[@class='uif-gridLayoutCell']/section/table");
    }
    
    @Test
    public void testLayoutManagersGridLayoutBookmark() throws Exception {
    	testLayoutManagersGridLayoutAll();
    }

    @Test
    public void testLayoutManagersGridLayoutNav() throws Exception {
    	testLayoutManagersGridLayoutAll();
    }  
    
    private void testLayoutManagersGridLayoutAll() throws Exception {
    	testLayoutManagersGridLayout();
        testLayoutManagersGridLayouMultipleColumns();
        testLayoutManagersGridLayoutColumnSpan();
        testLayoutManagersGridLayoutRenderTHColumn();
        testLayoutManagersGridLayoutRowSpan();
        testLayoutManagersGridLayoutFieldGroup();
        testLayoutManagersGridLayoutNestedLayout();
        passed();
    }
}
