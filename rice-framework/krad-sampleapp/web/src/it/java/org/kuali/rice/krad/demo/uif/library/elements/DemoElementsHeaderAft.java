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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsHeaderAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Header-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-HeaderView";

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
        waitAndClickByLinkText("Base Header");
        assertElementPresentByXpath("//header[@data-header_for='Demo-Header-Example1']/h3/span");
    }
    
    protected void testLibraryElementsHeader1() throws Exception {
        waitAndClickByLinkText("Header 1");
        assertElementPresentByXpath("//h1/span");
    }
    
    protected void testLibraryElementsHeader2() throws Exception {
        waitAndClickByLinkText("Header 2");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example3']/header[2]/h2/span");
    }
    
    protected void testLibraryElementsHeader3() throws Exception {
        waitAndClickByLinkText("Header 3");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example4']/header[2]/h3/span");
    }
    
    protected void testLibraryElementsHeader4() throws Exception {
        waitAndClickByLinkText("Header 4");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example5']/header[2]/h4/span");
    }
    
    protected void testLibraryElementsHeader5() throws Exception {
        waitAndClickByLinkText("Header 5");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example6']/header[2]/h5/span");
    }
    
    protected void testLibraryElementsHeader6() throws Exception {
        waitAndClickByLinkText("Header 6");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example7']/header[2]/h6/span");
    }
    
    protected void testLibraryElementsHeaderEditableHeader() throws Exception {
        waitAndClickByLinkText("EditablePage Header");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example12']/header[2]/h2/span");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example12']/header[2]/div/div/button");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example12']/header[2]/div/div/button[2]");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example12']/p");
    }
    
    protected void testLibraryElementsHeaderDisclosureHeader() throws Exception {
        waitAndClickByLinkText("Disclosure Header");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example13']/header[2]/h2/span");
    }

    protected void testLibraryElementsHeaderImageCaptionHeader() throws Exception {
        waitAndClickByLinkText("ImageCaption Header");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example14']/header[2]/h4/span");
    }
   
    protected void testLibraryElementsHeaderGroupsHeader() throws Exception {
        waitAndClickByLinkText("Header Groups");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example15']/section/header/div/p");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example15']/section/header/div[2]/h3");
        assertElementPresentByXpath("//section[@id='Demo-Header-Example15']/section/header/div[3]/p");
    }

    private void testAllHeaders() throws Exception {
        testLibraryElementsHeaderBaseHeader();
        testLibraryElementsHeader1();
        testLibraryElementsHeader2();
        testLibraryElementsHeader3();
        testLibraryElementsHeader4();
        testLibraryElementsHeader5();
        testLibraryElementsHeader6();
        testLibraryElementsHeaderEditableHeader();
        testLibraryElementsHeaderDisclosureHeader();
        testLibraryElementsHeaderImageCaptionHeader();
        testLibraryElementsHeaderGroupsHeader();
    }

    @Test
    public void testElementsHeaderNav() throws Exception {
        testAllHeaders();
        passed();
    }  
    
    @Test
    public void testElementsHeaderBookmark() throws Exception {
        testAllHeaders();
        passed();
    }  
}
