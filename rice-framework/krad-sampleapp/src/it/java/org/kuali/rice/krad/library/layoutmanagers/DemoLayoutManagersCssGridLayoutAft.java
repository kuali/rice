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
package org.kuali.rice.krad.library.layoutmanagers;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLayoutManagersCssGridLayoutAft extends AutomatedFunctionalTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CssGridLayoutManager-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CssGridLayoutManager-View&methodToCall=start";

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
        waitForElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div/div/div/input");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div/div[2]/div/input");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div/div[3]/div/input");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div[2]/div/div/input");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div[2]/div[2]/div/input");
    }
    
    protected void testLayoutManagersCssGridLayoutDefaultWidthBehaviour() throws Exception {
        selectByName("exampleShown", "Default width behavior");
        waitForElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/div/div/input");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div[2]/div/div/input");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div[2]/div[2]/div/input");
     }
    
    protected void testLayoutManagersCssGridLayoutRowCss() throws Exception {
        selectByName("exampleShown", "Row Css");
        waitForElementPresentByXpath("//div[@class='row demo-odd demo-border']/div/div/input[@name='inputField17']");
        assertElementPresentByXpath("//div[@class='row demo-odd demo-border']/div/div/input[@name='inputField18']");
        assertElementPresentByXpath("//div[@class='row demo-odd demo-border']/div/div/input[@name='inputField19']");
        assertElementPresentByXpath("//div[@class='row demo-even demo-border']/div/div/input[@name='inputField20']");
        assertElementPresentByXpath("//div[@class='row demo-even demo-border']/div/div/input[@name='inputField21']");
     }
    
    protected void testLayoutManagersCssGridLayoutDefaultColspan() throws Exception {
        selectByName("exampleShown", "Default ColSpan");
        waitForElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/div/div/input[@name='inputField22']");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/div/div/input[@name='inputField23']");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/div/div/input[@name='inputField24']");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/div/div/input[@name='inputField25']");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/div/div/div/div/input[@name='inputField26']");
    }
    
    protected void testLayoutManagersCssGridLayoutGroupLayout() throws Exception {
        selectByName("exampleShown", "Group Layout");
        waitForElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div/div/div[@class='uif-boxSection' and @style='background-color: #00CC66; height: 200px;']");
        assertElementPresentByXpath("//div[@id='Demo-CssGridLayoutManager-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-cssGridLayout']/div/div/div[@class='uif-boxSection' and @style='background-color: #00CCFF; height: 200px;']");
     }
    
    @Test
    public void testLayoutManagersCssGridLayoutBookmark() throws Exception {
        testLayoutManagersCssGridLayoutDefault();
        testLayoutManagersCssGridLayoutDefaultWidthBehaviour();
        testLayoutManagersCssGridLayoutRowCss();
        testLayoutManagersCssGridLayoutDefaultColspan();
        testLayoutManagersCssGridLayoutGroupLayout();
        passed();
    }

    @Test
    public void testLayoutManagersCssGridLayoutNav() throws Exception {
        testLayoutManagersCssGridLayoutDefault();
        testLayoutManagersCssGridLayoutDefaultWidthBehaviour();
        testLayoutManagersCssGridLayoutRowCss();
        testLayoutManagersCssGridLayoutDefaultColspan();
        testLayoutManagersCssGridLayoutGroupLayout();
        passed();
    }  
}
