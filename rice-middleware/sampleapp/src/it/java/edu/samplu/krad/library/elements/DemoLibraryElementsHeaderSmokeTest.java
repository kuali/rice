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
package edu.samplu.krad.library.elements;

import org.junit.Test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsHeaderSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Header-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-Header-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Header");
    }

    protected void testLibraryElementsHeaderBaseHeader() throws Exception {
        assertElementPresentByXpath("//div[@data-header_for='Demo-Header-Example1']/h3/span");
    }
    
    protected void testLibraryElementsHeader1() throws Exception {
        waitAndClickByLinkText("Header 1");
        assertElementPresentByXpath("//h1/span");
    }
    
    protected void testLibraryElementsHeader2() throws Exception {
        waitAndClickByLinkText("Header 2");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example3']/div[@class='uif-verticalBoxLayout clearfix']/div/div/h2/span");
    }
    
    protected void testLibraryElementsHeader3() throws Exception {
        waitAndClickByLinkText("Header 3");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example4']/div[@class='uif-verticalBoxLayout clearfix']/div/div/h3/span");
    }
    
    protected void testLibraryElementsHeader4() throws Exception {
        waitAndClickByLinkText("Header 4");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example5']/div[@class='uif-verticalBoxLayout clearfix']/div/div/h4/span");
    }
    
    protected void testLibraryElementsHeader5() throws Exception {
        waitAndClickByLinkText("Header 5");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example6']/div[@class='uif-verticalBoxLayout clearfix']/div/div/h5/span");
    }
    
    protected void testLibraryElementsHeader6() throws Exception {
        waitAndClickByLinkText("Header 6");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example7']/div[@class='uif-verticalBoxLayout clearfix']/div/div/h6/span");
    }
    
    protected void testLibraryElementsHeaderPageHeader() throws Exception {
        waitAndClickByLinkText("Page Header");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example8']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-pageHeader clearfix uif-boxLayoutVerticalItem clearfix']/h2/span");
    }
    
    protected void testLibraryElementsHeaderSectionHeader() throws Exception {
        waitAndClickByLinkText("Section Header");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example9']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-sectionHeader uif-boxLayoutVerticalItem clearfix']/h3/span");
    }
    
    protected void testLibraryElementsHeaderSubSectionHeader() throws Exception {
        waitAndClickByLinkText("SubSection Header");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example10']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-subSectionHeader uif-boxLayoutVerticalItem clearfix']/h4/span");
    }

    protected void testLibraryElementsHeaderSubCollectionHeader() throws Exception {
        waitAndClickByLinkText("SubCollection Header");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example11']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-subCollectionHeader uif-boxLayoutVerticalItem clearfix']/label/span");
    }

    protected void testLibraryElementsHeaderEditableHeader() throws Exception {
        waitAndClickByLinkText("EditablePage Header");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example12']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-pageHeader clearfix uif-boxLayoutVerticalItem clearfix']/h2/span");
    }
    
    protected void testLibraryElementsHeaderReadOnlyPageHeader() throws Exception {
        waitAndClickByLinkText("ReadOnlyPage Header");
        assertElementPresentByXpath("//div[@id='Demo-Header-Example13']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-pageHeader clearfix uif-boxLayoutVerticalItem clearfix']/h2/span");
    }
    
    @Test
    public void testElementsHeaderBookmark() throws Exception {
        testLibraryElementsHeaderBaseHeader();
        testLibraryElementsHeader1();
        testLibraryElementsHeader2();
        testLibraryElementsHeader3();
        testLibraryElementsHeader4();
        testLibraryElementsHeader5();
        testLibraryElementsHeader6();
        testLibraryElementsHeaderPageHeader();
        testLibraryElementsHeaderSectionHeader();
        testLibraryElementsHeaderSubSectionHeader();
        testLibraryElementsHeaderSubCollectionHeader();
        testLibraryElementsHeaderEditableHeader();
        testLibraryElementsHeaderReadOnlyPageHeader();
        passed();
    }

    @Test
    public void testElementsHeaderNav() throws Exception {
        testLibraryElementsHeaderBaseHeader();
        testLibraryElementsHeader1();
        testLibraryElementsHeader2();
        testLibraryElementsHeader3();
        testLibraryElementsHeader4();
        testLibraryElementsHeader5();
        testLibraryElementsHeader6();
        testLibraryElementsHeaderPageHeader();
        testLibraryElementsHeaderSectionHeader();
        testLibraryElementsHeaderSubSectionHeader();
        testLibraryElementsHeaderSubCollectionHeader();
        testLibraryElementsHeaderEditableHeader();
        testLibraryElementsHeaderReadOnlyPageHeader();
        passed();
    }  
}