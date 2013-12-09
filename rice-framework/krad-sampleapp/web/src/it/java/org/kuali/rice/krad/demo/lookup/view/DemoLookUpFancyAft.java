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
package org.kuali.rice.krad.demo.lookup.view;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpFancyAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=FancyLookupSampleView&hideReturnLink=true";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByXpath("//a[@href='lookup?methodToCall=start&viewId=FancyLookupSampleView']");
    }

    protected void testLookUpFancy() throws InterruptedException {
        waitAndTypeByName("lookupCriteria[name]","*");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","eri*");
    }

    @Test
    public void testLookUpFancyBookmark() throws Exception {
        testLookUpFancy();
        passed();
    }

    @Test
    public void testLookUpFancyNav() throws Exception {
        testLookUpFancy();
        passed();
    }
}
