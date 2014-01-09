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
public class LabsAutocompleteFieldAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Lab-NativeAutocomplete-DisableField
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Lab-NativeAutocomplete-DisableField";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Native Autocomplete Field Disabled");
    }

    protected void testDemoAutocompleteField() throws InterruptedException {
    	waitForElementPresentByXpath("//input[@autocomplete='off' and @name='field1']");
    	waitForElementPresentByXpath("//input[@autocomplete='off' and @name='field3']");
    	waitForElementPresentByXpath("//input[@autocomplete='off' and @name='field4']");
    	if(isElementPresentByXpath("//input[@autocomplete='off' and @name='field2']"))
    	{
    		fail("Field level Autocomplete disabled not working !");
    	}
    }

    @Test
    public void testDemoAutocompleteFieldBookmark() throws Exception {
    	testDemoAutocompleteField();
        passed();
    }

    @Test
    public void testDemoAutocompleteFieldNav() throws Exception {
    	testDemoAutocompleteField();
        passed();
    }
}
