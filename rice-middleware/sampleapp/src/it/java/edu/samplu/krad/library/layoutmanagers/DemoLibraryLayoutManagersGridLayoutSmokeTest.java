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

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryLayoutManagersGridLayoutSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-GridLayoutManager-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-GridLayoutManager-View&methodToCall=start";

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
       assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout']/tbody/tr/th/span");
       assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout']/tbody/tr/td/div/input");
       assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout']/tbody/tr[2]/th/span");
       assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout']/tbody/tr[2]/td/div/input");
       assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout']/tbody/tr[3]/th/span");
       assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout']/tbody/tr[3]/td/div/input");
    }
    
    protected void testLayoutManagersGridLayouMultipleColumns() throws Exception {
        selectByName("exampleShown","# of Columns");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/th/span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/td/div/input[@name='inputField4']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/th[2]/span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example2']/table/tbody/tr/td[2]/div/input[@name='inputField5']");
    }
    
    protected void testLayoutManagersGridLayoutColumnSpan() throws Exception {
        selectByName("exampleShown","Column Span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/th[@colspan='2']/span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/td[@colspan='2']/div/input[@name='inputField8']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/th[@colspan='1']/span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/td[@colspan='1']/div/input[@name='inputField9']");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/th[@colspan='1'][2]/span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example3']/table/tbody/tr/td[@colspan='1'][2]/div/input[@name='inputField10']");
    }
    
    protected void testLayoutManagersGridLayoutRenderTHColumn() throws Exception {
        selectByName("exampleShown","Render TH Column");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example4']/table/tbody/tr/td/span");
        assertElementPresentByXpath("//div[@data-parent='Demo-GridLayoutManager-Example4']/table/tbody/tr/td/div/input[@name='inputField12']");
    }
    
    @Test
    public void testLayoutManagersGridLayoutBookmark() throws Exception {
        testLayoutManagersGridLayout();
        testLayoutManagersGridLayouMultipleColumns();
        testLayoutManagersGridLayoutColumnSpan();
        testLayoutManagersGridLayoutRenderTHColumn();
        passed();
    }

    @Test
    public void testLayoutManagersGridLayoutNav() throws Exception {
        testLayoutManagersGridLayout();
        testLayoutManagersGridLayouMultipleColumns();
        testLayoutManagersGridLayoutColumnSpan();
        testLayoutManagersGridLayoutRenderTHColumn();
        passed();
    }  
}