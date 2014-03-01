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
package org.kuali.rice.krad.demo.uif.library.containers;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoContainerAccordionAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AccordionGroupView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AccordionGroupView&methodToCall=start";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Accordion Group");
    }

    protected void testLibraryContainerAccordion() throws Exception {
        waitAndClickByLinkText("Accordion Item 1");
        assertElementPresentByXpath("//section[@id='Demo-AccordionGroup-SubList1' and @style='display: block;']");
        waitAndClickByLinkText("Accordion Item 1");
        assertElementPresentByXpath("//section[@id='Demo-AccordionGroup-SubList1' and @style='display: none;']");
        waitAndClickByLinkText("Accordion Item 2");
        assertElementPresentByXpath("//section[@id='Demo-AccordionGroup-SubList2' and @style='display: block;']");
        waitAndClickByLinkText("Accordion Item 2");
        assertElementPresentByXpath("//section[@id='Demo-AccordionGroup-SubList2' and @style='display: none;']");
    
    }
    
    @Test
    public void testContainerAccordionBookmark() throws Exception {
        testLibraryContainerAccordion();
        passed();
    }

    @Test
    public void testContainerAccordionNav() throws Exception {
        testLibraryContainerAccordion();
        passed();
    }  
}
