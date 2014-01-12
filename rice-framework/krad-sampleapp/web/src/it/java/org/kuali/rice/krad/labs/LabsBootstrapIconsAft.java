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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsBootstrapIconsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-ActionIconMenu
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-ActionIconMenu";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Bootstrap Icons");
    }

    protected void testDemoBootstrapIcons() throws InterruptedException {
    	waitForElementPresentByXpath("//button[@class='btn btn-default uif-secondaryActionButton uif-boxLayoutVerticalItem clearfix icon-home']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-primaryActionButton uif-boxLayoutVerticalItem clearfix']/span[@class='icon-office']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-primaryActionButton uif-boxLayoutVerticalItem clearfix']/span[@class='icon-music']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-primaryActionButton uif-boxLayoutVerticalItem clearfix']/span[@class='icon-connection']");
    	waitForElementPresentByXpath("//a[@class='uif-link uif-boxLayoutVerticalItem clearfix icon-pencil']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-primaryActionButton uif-boxLayoutVerticalItem clearfix']/span/img[@class='actionImage bottomActionImage uif-image']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-primaryActionButton uif-boxLayoutVerticalItem clearfix']/img[@class='actionImage leftActionImage uif-image']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-primaryActionButton uif-boxLayoutVerticalItem clearfix']/img[@class='actionImage rightActionImage uif-image']");
    }

    @Test
    public void testDemoBootstrapIconsBookmark() throws Exception {
    	testDemoBootstrapIcons();
        passed();
    }

    @Test
    public void testDemoBootstrapIconsNav() throws Exception {
    	testDemoBootstrapIcons();
        passed();
    }
}
