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
package org.kuali.rice.krad.demo.uif.library.general;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoGeneralFeaturesStickyHeaderAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-StickyHeaderView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-StickyHeaderView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Sticky Header Options");
    }

    protected void testGeneralFeaturesExample1() throws Exception {
        selectByName("exampleShown", "Sticky View Header");
        waitAndClickByXpath("//section[@id='Demo-StickyHeader-Example1']/a");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//header[@id='Uif-ApplicationHeader-Wrapper']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample2() throws Exception {
        selectByName("exampleShown", "Sticky Application Header and View Header");
        waitAndClickByXpath("//section[@id='Demo-StickyHeader-Example2']/a");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//header[@id='Uif-ApplicationHeader-Wrapper' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample3() throws Exception {
        selectByName("exampleShown", "Sticky Breadcrumbs, Application Header and View Header");
        waitAndClickByXpath("//section[@id='Demo-StickyHeader-Example3']/a");
        switchToWindow("Kuali :: View Header");
        assertElementPresentByXpath("//header[@data-header_for='Demo-StickyHeaderFooter-View3' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample4() throws Exception {
        selectByName("exampleShown", "Sticky Application Header, Top Group and View Header");
        waitAndClickByXpath("//section[@id='Demo-StickyHeader-Example4']/a");
        switchToWindow("Kuali :: View Header");
        assertElementPresentByXpath("//header[@data-header_for='Demo-StickyHeaderFooter-View4' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample5() throws Exception {
        selectByName("exampleShown", "Sticky Everything");
        waitAndClickByLinkText("All header content sticky");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//header[@id='Uif-ApplicationHeader-Wrapper' and @data-sticky='true']");
        assertElementPresentByXpath("//header[@data-header_for='Demo-StickyHeaderFooter-View5' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader1Bookmark() throws Exception{
    	testGeneralFeaturesExample1();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader1Nav() throws Exception{
    	testGeneralFeaturesExample1();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader2Bookmark() throws Exception{
    	testGeneralFeaturesExample2();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader2Nav() throws Exception{
    	testGeneralFeaturesExample2();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader3Bookmark() throws Exception{
    	testGeneralFeaturesExample3();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader3Nav() throws Exception{
    	testGeneralFeaturesExample3();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader4Bookmark() throws Exception{
    	testGeneralFeaturesExample4();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader4Nav() throws Exception{
    	testGeneralFeaturesExample4();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader5Bookmark() throws Exception{
    	testGeneralFeaturesExample5();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeader5Nav() throws Exception{
    	testGeneralFeaturesExample5();
        passed();
    }
}
