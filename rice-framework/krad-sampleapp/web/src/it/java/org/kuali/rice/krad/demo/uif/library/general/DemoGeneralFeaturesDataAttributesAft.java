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
public class DemoGeneralFeaturesDataAttributesAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DataAttributesView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DataAttributesView";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Data Attributes");
    }

    protected void testGeneralFeaturesDataAttributes() throws Exception {
        waitForElementPresentByXpath("//a[@data-icontemplatename and @data-transitions and @data-capitals and @data-intervals and @data-dataroleattribute and @data-datametaattribute and @data-datatypeattribute and @data-performdirtyvalidation and @data-focusid and @data-jumptoid and @data-role]");
    }
    
    @Test
    public void testGeneralFeaturesDataAttributesBookmark() throws Exception {
        testGeneralFeaturesDataAttributes();
        passed();
    }

    @Test
    public void testGeneralFeaturesDataAttributesNav() throws Exception {
        testGeneralFeaturesDataAttributes();
        passed();
    }  
}
