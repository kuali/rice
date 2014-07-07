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
public class DemoWidgetsLightBoxAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LightboxView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LightboxView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Lightbox");
    }

    protected void testWidgetsLightBoxHtml() throws Exception {
    	waitAndClickButtonByExactText("Lightbox from Dynamic HTML");
    	waitForElementPresentByXpath("//div[@class='fancybox-inner']/form/b");
    	waitAndClickByXpath("//a[@class='fancybox-item fancybox-close']");
    }

    protected void testWidgetDirectLightBoxHiddenImage() throws Exception 
    {	waitAndClickByLinkText("Lightbox From a Hidden Image");
    	waitAndClickButtonByExactText("Show Lightbox with Image");
    	waitForElementPresentByXpath("//div[@class='fancybox-inner']/form/img");
    	waitAndClickByXpath("//a[@class='fancybox-item fancybox-close']");
    }
    
    protected void testWidgetDirectLightBoxUrl() throws Exception {
    	waitAndClickByLinkText("Lightbox From a URL");
    	waitAndClickButtonByExactText("Show Lightbox with URL");
    	waitForElementPresentByXpath("//iframe[@src='http://kuali.org']");
    	waitAndClickByXpath("//a[@class='fancybox-item fancybox-close']");
    }
 
    private void testAllLightBox() throws Exception {
    	testWidgetsLightBoxHtml();
    	testWidgetDirectLightBoxHiddenImage();
    	testWidgetDirectLightBoxUrl();
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
