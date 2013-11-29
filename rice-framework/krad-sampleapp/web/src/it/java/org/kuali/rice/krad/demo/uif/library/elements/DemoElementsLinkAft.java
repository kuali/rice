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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsLinkAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LinkView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LinkView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Link");
    }

    protected void testLibraryElementsLink() throws Exception {
        assertElementPresentByXpath("//div[@id='Demo-Link-Example1']/div[@class='uif-verticalBoxLayout clearfix']/a[@target='_self']");
    }
    
    protected void testLibraryElementsCustomTarget() throws Exception {
        waitAndClickByLinkText("Custom Target");
        assertElementPresentByXpath("//div[@id='Demo-Link-Example2']/div[@class='uif-verticalBoxLayout clearfix']/a[@target='_blank']");
    }
    
    protected void testLibraryElementsLinkUsingLightbox() throws Exception {
        waitAndClickByLinkText("Link using lightbox");
        assertElementPresentByXpath("//div[@id='Demo-Link-Example3']/div[@class='uif-verticalBoxLayout clearfix']/a");
    }
    
    @Test
    public void testElementsLinkBookmark() throws Exception {
        testLibraryElementsLink();
        testLibraryElementsCustomTarget();
        testLibraryElementsLinkUsingLightbox();
        passed();
    }

    @Test
    public void testElementsLinkNav() throws Exception {
        testLibraryElementsLink();
        testLibraryElementsCustomTarget();
        testLibraryElementsLinkUsingLightbox();
        passed();
    }  
}
