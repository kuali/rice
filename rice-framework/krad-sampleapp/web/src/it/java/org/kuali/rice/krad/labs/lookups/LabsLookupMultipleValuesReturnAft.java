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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupMultipleValuesReturnAft extends LabsLookupBase {

    /**
     * /kr-krad/labs?methodToCall=start&viewId=LabsLookup-MultipleValuesReturnView
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?methodToCall=start&viewId=LabsLookup-MultipleValuesReturnView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Multiple Values Return");
    }

    @Test
    public void testLabsLookupMultipleValuesReturnBookmark() throws Exception {
        testLabsLookupMultipleValuesReturn();
        passed();
    }

    @Test
    public void testLabsLookupMultipleValuesReturnNav() throws Exception {
        testLabsLookupMultipleValuesReturn();
        passed();
    }
    
    protected void testLabsLookupMultipleValuesReturn()throws Exception {
    	waitAndClickByLinkText("Lookup/Add Multiple Lines");
    	gotoIframeByXpath("//iframe[@class='fancybox-iframe']");
        waitAndTypeByName("lookupCriteria[name]","*");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","eri*");
        waitAndClickSearch3();
        assertResultCount("6");
        waitAndClickByXpath("//input[@type='checkbox' and @value='CAT:Travel Account 10:a10']");
        waitAndClickByXpath("//input[@type='checkbox' and @value='EAT:Travel Account 11:a11']");
        waitAndClickButtonByText("return selected");
        waitForTextPresent("CAT");
        waitForTextPresent("Travel Account 10");
        waitForTextPresent("a10");
        waitForTextPresent("EAT");
        waitForTextPresent("Travel Account 11");
        waitForTextPresent("a11");
    }
}
