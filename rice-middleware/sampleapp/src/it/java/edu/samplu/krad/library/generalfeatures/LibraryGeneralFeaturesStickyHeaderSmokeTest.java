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
package edu.samplu.krad.library.generalfeatures;

import org.junit.Test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryGeneralFeaturesStickyHeaderSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-StickyHeader-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-StickyHeader-View&methodToCall=start";

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
        waitAndClickByLinkText("Example 1");
       waitAndClickByLinkText("Sticky View header");
       switchToWindow("Kuali :: View Header");
       assertElementPresentByXpath("//div[@class='uif-viewHeader-contentWrapper uif-sticky' and @data-sticky='true']");
       switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample2() throws Exception {
        waitAndClickByLinkText("Example 2");
        waitAndClickByLinkText("Sticky application header and View header");
        switchToWindow("Kuali :: View Header");
        assertElementPresentByXpath("//div[@id='Uif-ApplicationHeader-Wrapper' and @data-sticky='true']");
        assertElementPresentByXpath("//div[@class='uif-viewHeader-contentWrapper uif-sticky' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample3() throws Exception {
        waitAndClickByLinkText("Example 3");
        waitAndClickByLinkText("Sticky application header, breadcrumbs, and View header");
        switchToWindow("Kuali :: View Header");
        assertElementPresentByXpath("//div[@id='Uif-BreadcrumbWrapper' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample4() throws Exception {
        waitAndClickByLinkText("Example 4");
        waitAndClickByLinkText("Sticky application header, top group, and View header");
        switchToWindow("Kuali :: View Header");
        assertElementPresentByXpath("//div[@id='Uif-TopGroupWrapper' and @data-sticky='true']");
        assertElementPresentByXpath("//div[@class='uif-viewHeader-contentWrapper uif-sticky' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    protected void testGeneralFeaturesExample5() throws Exception {
        waitAndClickByLinkText("Example 5");
        waitAndClickByLinkText("All header content sticky");
        switchToWindow("Kuali :: View Header");
        assertElementPresentByXpath("//div[@id='Uif-TopGroupWrapper' and @data-sticky='true']");
        assertElementPresentByXpath("//div[@class='uif-viewHeader-contentWrapper uif-sticky' and @data-sticky='true']");
        assertElementPresentByXpath("//div[@id='Uif-BreadcrumbWrapper' and @data-sticky='true']");
        switchToWindow("Kuali");
    }
    
    @Test
    public void testGeneralFeaturesUnifiedViewHeaderBookmark() throws Exception {
        testGeneralFeaturesExample5();
        testGeneralFeaturesExample4();
        testGeneralFeaturesExample3();
        testGeneralFeaturesExample2();
        testGeneralFeaturesExample1();
        passed();
    }

    @Test
    public void testGeneralFeaturesUnifiedViewHeaderNav() throws Exception {
        testGeneralFeaturesExample5();
        testGeneralFeaturesExample4();
        testGeneralFeaturesExample3();
        testGeneralFeaturesExample2();
        testGeneralFeaturesExample1();
        passed();
    }  
}