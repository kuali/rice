/**
 * Copyright 2005-2016 The Kuali Foundation
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
package edu.sampleu.krad.reference;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class TypeBaseAft extends WebDriverLegacyITBase {

    protected abstract String[][] getData();

    //Code for KRAD Test Package.
    protected void testEntityType() throws Exception {
        selectFrameIframePortlet();
        waitAndClickClearValues();

        //Search by "Both" Filter in Active Indicator
        clickSearch();
        assertTextPresent(getData());
        waitAndClickClearValues();

        //Search by "Yes" Filter in Active Indicator
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='Y']");
        clickSearch();
        assertTextPresent(getData());
        waitAndClickClearValues();

        //Search by "No" Filter in Active Indicator
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
        clickSearch();
        waitForTextPresent("No values match this search.");
        waitAndClickClearValues();

        //Search by Code Filter
        waitAndTypeByName("lookupCriteria[code]",getData()[0][0]);
        clickSearch();
        assertTextPresent(getData()[0]);
        waitAndClickClearValues();

        //Search by Name Filter
        waitAndTypeByName("lookupCriteria[name]",getData()[0][1]);
        clickSearch();
        assertTextPresent(getData()[0]);
        waitAndClickClearValues();
    }

    protected void clickSearch() throws InterruptedException {
        waitAndClickSearchByText();
        waitForProgressLoading();
    }

    @Test
    public void testTypeBookmark() throws Exception {
        testEntityType();
        passed();
    }

    @Test
    public void testTypeNav() throws Exception {
        testEntityType();
        passed();
    }

}
