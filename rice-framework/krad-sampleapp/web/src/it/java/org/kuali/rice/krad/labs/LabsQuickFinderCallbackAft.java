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
public class LabsQuickFinderCallbackAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/quickfinder?viewId=Lab-QuickFinderCallback
     */
    public static final String BOOKMARK_URL = "/kr-krad/quickfinder?viewId=Lab-QuickFinderCallback";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("QuickFinder Callback");
    }

    protected void testDemoQuickFinderCallback() throws Exception {
    	waitAndClickByXpath("//section[@id='Lab-QuickFinder-Table4']/div/table/tbody/tr/td[3]/div/div/div/button");
    	testLookUpSearchReturn();
    	waitAndClickByXpath("//section[@id='Lab-QuickFinder-Table5']/div/table/tbody/tr/td[3]/div/div/div/button");
    	testLookUpSearchReturn();
    	waitAndClickByXpath("//section[@id='Lab-QuickFinder-Table6']/div/table/tbody/tr/td[3]/div/div/div/button");
    	testLookUpSearchReturn();
    	waitAndClickByXpath("//section[@id='Lab-QuickFinder-Table3']/table/tbody/tr/td/div/div/div/button");
    	testLookUpSearchReturn();
    	waitAndClickByXpath("//section[@id='Lab-QuickFinder-Table']/div/table/tbody/tr/td[3]/div/div/div/button");
    	testLookUpSearchReturn();
    	waitAndClickByXpath("//section[@id='Lab-QuickFinder-Table2']/div/table/tbody/tr/td[3]/div/div/div/button");
    	testLookUpSearchReturn();
    }
    
    private void testLookUpSearchReturn() throws Exception {
    	gotoLightBox();
    	waitAndClickButtonByExactText("Search");
    	waitAndClickByLinkText("return value");
    }

    @Test
    public void testDemoQuickFinderCallbackBookmark() throws Exception {
    	testDemoQuickFinderCallback();
        passed();
    }

    @Test
    public void testDemoQuickFinderCallbackNav() throws Exception {
    	testDemoQuickFinderCallback();
        passed();
    }
}
