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
public class DemoLibraryLayoutManagersCssGridLayoutSmokeTest extends SmokeTestBase {

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

    protected void testLayoutManagersCssGridLayoutFixedExample1() throws Exception {
       assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField1']");
       assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3'][2]/div/input[@name='inputField2']");
       assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3'][3]/div/input[@name='inputField3']");
       assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[2]/div[@class='span4']/div/input[@name='inputField4']");
       assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[2]/div[@class='span5']/div/input[@name='inputField5']");
    }
    
    protected void testLayoutManagersCssGridLayoutFixedExample2() throws Exception {
        selectByName("exampleShown", "Fixed Example 2");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span9']/div/input[@name='inputField6']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span6']/div/input[@name='inputField7']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField8']");
     }
    
    protected void testLayoutManagersCssGridLayoutFluidExample1() throws Exception {
        selectByName("exampleShown", "Fluid Example 1");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField9']");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField10']");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField11']");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div[2]/div[@class='span4']/div/input[@name='inputField12']");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div[2]/div[@class='span5']/div/input[@name='inputField13']");
     }
    
    protected void testLayoutManagersCssGridLayoutFluidExample2() throws Exception {
        selectByName("exampleShown", "Fluid Example 2");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div/div[@class='span9']/div/input[@name='inputField14']");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div/div[@class='span6']/div/input[@name='inputField15']");
        assertElementPresentByXpath("//div[@class='uif-fluidCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField16']");
     }
    
    protected void testLayoutManagersCssGridLayoutRowCss() throws Exception {
        selectByName("exampleShown", "Row Css");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[@class='row demo-odd demo-border']/div[@class='span3']/div/input[@name='inputField17']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[@class='row demo-odd demo-border']/div[@class='span3']/div/input[@name='inputField18']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[@class='row demo-odd demo-border']/div[@class='span3']/div/input[@name='inputField19']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[@class='row demo-even demo-border']/div[@class='span4']/div/input[@name='inputField20']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div[@class='row demo-even demo-border']/div[@class='span5']/div/input[@name='inputField21']");
     }
    
    protected void testLayoutManagersCssGridLayoutDefaultColSpan() throws Exception {
        selectByName("exampleShown", "Default ColSpan");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField22']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField23']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField24']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField25']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField26']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div/input[@name='inputField27']");
     }
    
    protected void testLayoutManagersCssGridLayoutGroupLayout() throws Exception {
        selectByName("exampleShown", "Group Layout");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span3']/div[@class='uif-boxSection']");
        assertElementPresentByXpath("//div[@class='span9 uif-fixedCssGridLayout']/div/div[@class='span6']/div[@class='uif-boxSection']");
     }
    
    @Test
    public void testLayoutManagersCssGridLayoutBookmark() throws Exception {
        testLayoutManagersCssGridLayoutFixedExample1();
        testLayoutManagersCssGridLayoutFixedExample2();
        testLayoutManagersCssGridLayoutFluidExample1();
        testLayoutManagersCssGridLayoutFluidExample2();
        testLayoutManagersCssGridLayoutRowCss();
        testLayoutManagersCssGridLayoutDefaultColSpan();
        testLayoutManagersCssGridLayoutGroupLayout();
        passed();
    }

    @Test
    public void testLayoutManagersCssGridLayoutNav() throws Exception {
        testLayoutManagersCssGridLayoutFixedExample1();
        testLayoutManagersCssGridLayoutFixedExample2();
        testLayoutManagersCssGridLayoutFluidExample1();
        testLayoutManagersCssGridLayoutFluidExample2();
        testLayoutManagersCssGridLayoutRowCss();
        testLayoutManagersCssGridLayoutDefaultColSpan();
        testLayoutManagersCssGridLayoutGroupLayout();
        passed();
    }  
}