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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsQuickFinderAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-QuickFinderView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-QuickFinderView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "QuickFinder");
    }

    protected void testWidgetsQuickFinderLookUp() throws Exception {
    	waitAndClickByXpath("//section[@id='Demo-QuickFinder-Example1']/div/div/div/button");
    	gotoLightBox();
    	waitAndClickButtonByExactText("Search");
    	waitAndClickByLinkText("return value");
    }

    protected void testWidgetDirectQuickFinderLookUpReturnByScript() throws Exception {	
    	waitAndClickByLinkText("Lookup (Return by script)");
    	waitAndClickByXpath("//section[@id='Demo-QuickFinder-Example2']/div/div/div/button");
    	gotoLightBox();
    	waitAndClickButtonByExactText("Search");
    	waitAndClickByLinkText("return value");
    	selectTopFrame();
    }
    
    protected void testWidgetDirectQuickFinderLookUpOverriddenLinkt() throws Exception {	
    	waitAndClickByLinkText("Lookup (Overridden link)");
    	waitAndTypeByName("inputField14","fred");
    }
    
    protected void testWidgetDirectQuickFinderLookUpAditionalParameter() throws Exception {
    	waitAndClickByLinkText("Lookup (with additional parameters)");
    	waitAndClickByXpath("//section[@id='Demo-QuickFinder-Example4']/div/div/div/button");
    	gotoLightBox();
    	waitAndClickButtonByExactText("Search");
    	waitAndClickByLinkText("return value");
    }
 
    private void testAllQuickFinder() throws Exception {
    	testWidgetsQuickFinderLookUp();
    	testWidgetDirectQuickFinderLookUpReturnByScript();
    	testWidgetDirectQuickFinderLookUpOverriddenLinkt();
    	testWidgetDirectQuickFinderLookUpAditionalParameter();
	    passed();
    }

    @Test
    public void testWidgetsQuickFinderBookmark() throws Exception {
    	testAllQuickFinder();
    }

    @Test
    public void testWidgetsQuickFinderNav() throws Exception {
    	testAllQuickFinder();
    }
}
