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
public class DemoGeneralFeaturesStickyFooterAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-StickyFooterView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-StickyFooterView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Sticky Footer Options");
    }

    protected void testGeneralFeaturesExample1() throws Exception {
        selectByName("exampleShown", "Sticky Application Footer");
        waitAndClickByLinkText("Sticky application footer");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//footer[@id='Uif-ApplicationFooter-Wrapper' and @data-sticky_footer='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample2() throws Exception {
        selectByName("exampleShown", "Sticky Page Footer");
        waitAndClickByLinkText("Sticky page footer");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//footer[@id='Uif-ApplicationFooter-Wrapper']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample3() throws Exception {
        selectByName("exampleShown", "Sticky View Footer");
        waitAndClickByLinkText("Sticky view footer");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//div[@class='uif-horizontalBoxGroup clearfix uif-stickyFooter uif-stickyButtonFooter']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample4() throws Exception {
        selectByName("exampleShown", "Sticky View Footer and Page Footer");
        waitAndClickByLinkText("Sticky page and view footer");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//div[@class='uif-horizontalBoxGroup clearfix uif-stickyFooter uif-stickyButtonFooter' and @data-sticky_footer='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample5() throws Exception {
        selectByName("exampleShown", "Sticky Everything");
        waitAndClickByLinkText("All footer content sticky");
        switchToWindow("Kuali :: View Header");
        waitForElementPresentByXpath("//footer[@class='uif-stickyFooter' and @data-sticky_footer='true']");
        waitForElementPresentByXpath("//footer[@id='Uif-ApplicationFooter-Wrapper' and @data-sticky_footer='true']");
        switchToWindow("Kuali");
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter1Bookmark() throws Exception{
    	testGeneralFeaturesExample1();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter1Nav() throws Exception{
    	testGeneralFeaturesExample1();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter2Bookmark() throws Exception{
    	testGeneralFeaturesExample2();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter2Nav() throws Exception{
    	testGeneralFeaturesExample2();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter3Bookmark() throws Exception{
    	testGeneralFeaturesExample3();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter3Nav() throws Exception{
    	testGeneralFeaturesExample3();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter4Bookmark() throws Exception{
    	testGeneralFeaturesExample4();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter4Nav() throws Exception{
    	testGeneralFeaturesExample4();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter5Bookmark() throws Exception{
    	testGeneralFeaturesExample5();
        passed();
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewFooter5Nav() throws Exception{
    	testGeneralFeaturesExample5();
        passed();
    }
}
