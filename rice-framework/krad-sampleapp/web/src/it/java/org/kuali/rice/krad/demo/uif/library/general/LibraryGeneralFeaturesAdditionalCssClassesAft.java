/**
 * Copyright 2005-2017 The Kuali Foundation
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
public class LibraryGeneralFeaturesAdditionalCssClassesAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AdditionalCssClassesView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AdditionalCssClassesView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Additional CSS Classes");
    }

    protected void testGeneralFeaturesAdditionalCssClassesFormView() throws Exception {
        waitForElementPresentByXpath("//div[@class='uif-verticalBoxGroup' and @style='background-color: lightBlue; width: 100%;']");
    }
    
    protected void testGeneralFeaturesAdditionalCssClassesMessage() throws Exception {
    	selectByName("exampleShown","Message");
    	waitForElementPresentByXpath("//p[@class='uif-message uif-boxLayoutVerticalItem clearfix demo-demoGroup tweet_text']");
    }
    
    @Test
    public void testGeneralFeaturesAdditionalCssClassesBookmark() throws Exception {
    	testGeneralFeaturesAdditionalCssClassesFormView();
    	testGeneralFeaturesAdditionalCssClassesMessage();
        passed();
    }

    @Test
    public void testGeneralFeaturesAdditionalCssClassesNav() throws Exception {
    	testGeneralFeaturesAdditionalCssClassesFormView();
    	testGeneralFeaturesAdditionalCssClassesMessage();
        passed();
    }  
}
