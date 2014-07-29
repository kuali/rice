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
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoGeneralFeaturesFocusAndJumpToAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-FocusNJumpToView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-FocusNJumpToView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Focus And JumpTo");
    }

    protected void testGeneralFeaturesFocusAndJumpTo() throws Exception {
        waitAndClickButtonByExactText("Refresh Group");
        waitForTextPresent("Loading...");
        waitAndClickButtonByExactText("Save");
        waitForTextPresent("Loading...");
        waitAndClickButtonByExactText("Refresh Field but with Server Errors");
        waitForTextPresent("Loading...");
        waitForElementPresentByXpath("//div[@id='input4' and @class='uif-inputField uif-boxLayoutVerticalItem pull-left clearfix uif-hasError']/input");
        waitAndClickButtonByExactText("Refresh Page");
        waitForTextPresent("Loading...");
    }
    
    protected void testGeneralFeaturesFocusAndJumpToFocusAndCollections() throws Exception {
    	selectByName("exampleShown","Focus and Collections");
    	waitAndClickButtonByExactText("Add");
    	waitForTextPresent("Adding Line...");
    }
    
    protected void testGeneralFeaturesFocusAndJumpToAndJumpToIdAfterSubmit() throws Exception {
    	selectByName("exampleShown","JumpTo and JumpToIdAfterSubmit");
    	waitAndClickButtonByExactText("Jump to Field 20");
    	waitAndClickButtonByExactText("Jump to Top");
    	waitAndClickButtonByExactText("Jump to Bottom");
    }
    
    @Test
    public void testGeneralFeaturesFocusAndJumpToBookmark() throws Exception {
    	testGeneralFeaturesFocusAndJumpTo();
    	testGeneralFeaturesFocusAndJumpToFocusAndCollections();
    	testGeneralFeaturesFocusAndJumpToAndJumpToIdAfterSubmit();
        passed();
    }

//    @Test
    public void testGeneralFeaturesFocusAndJumpToNav() throws Exception {
    	testGeneralFeaturesFocusAndJumpTo();
    	testGeneralFeaturesFocusAndJumpToFocusAndCollections();
    	testGeneralFeaturesFocusAndJumpToAndJumpToIdAfterSubmit();
        passed();
    }  
}
