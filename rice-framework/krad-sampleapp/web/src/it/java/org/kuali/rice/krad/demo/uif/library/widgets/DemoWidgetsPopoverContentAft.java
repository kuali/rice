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
public class DemoWidgetsPopoverContentAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-PopoverContentView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-PopoverContentView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Popover Content");
    }

    protected void testWidgetsPopover() throws Exception {
    	waitAndClickByXpath("//section[@id='Demo-PopoverContent-Example1']/button[contains(text(),'Popover Form Action')]");
    	waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group']/button");
    	acceptAlertIfPresent();
    }

    protected void testWidgetPopoverOptions() throws Exception 
    {	waitAndClickByLinkText("Options");
    	waitAndClickByXpath("//section[@id='Demo-PopoverContent-Example2']/button[contains(text(),'Popover Form Action')]");
    	waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group2']/button");
    	acceptAlertIfPresent();
    }
 
    private void testAllLightBox() throws Exception {
    	testWidgetsPopover();
    	testWidgetPopoverOptions();
	    passed();
    }

    @Test
    public void testWidgetsLightBoxBookmark() throws Exception {
    	testAllLightBox();
    }

    @Test
    public void testWidgetsLightBoxNav() throws Exception {
    	testAllLightBox();
    }
}
