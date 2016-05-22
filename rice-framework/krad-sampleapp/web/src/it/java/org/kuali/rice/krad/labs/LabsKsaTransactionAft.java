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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.Keys;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsKsaTransactionAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/transaction?viewId=TransactionView
     */
    public static final String BOOKMARK_URL = "/kr-krad/transaction?viewId=TransactionView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("KSA Transaction");
    }

    protected void testKsaTransaction() throws InterruptedException {
        jGrowl("Expanding First row");
        waitAndClickById("rollupList1_detLink_line0");
        waitForTextPresent("Transactions");

        jGrowl("Clicking Delete");
        waitAndClickById("rollupList1_del_line0");
        waitForTextPresent("You have deleted an item from Roll-Up");

        waitAndTypeByName("testField", "a");
        typeTab();
        waitForTextNotPresent("You have deleted an item from Roll-Up");
        
    	//waitAndTypeByName("testField","a");
    	//assertTextPresent("Charges");
        //jGrowl("Click Delete");
        //waitAndClickById("rollupList1_del_line0");
        //waitForTextPresent("You have deleted an item from Roll-Up");
        //jGrowl("Expand first row");
    	//waitAndClickByXpath("//img[@class='actionImage leftActionImage uif-image']");
        //checkForIncidentReport(); // there is a history of getting freemarker exceptions here that don't prevent the test from continuing    	assertTextPresent("Deleting Line");
        //jGrowl("Click Delete");
        //waitAndClickById("rollupList1_detLink_line1");
        //checkForIncidentReport(); // there is a history of getting freemarker exceptions here that don't prevent the test from continuing    	assertTextPresent("Deleting Line");
        //waitForTextPresent("You have deleted an item from Roll-Up");
    }

    @Test
    public void testKsaTransactionBookmark() throws Exception {
    	testKsaTransaction();
        passed();
    }

    @Test
    public void testKsaTransactionNav() throws Exception {
    	testKsaTransaction();
        passed();
    }
}
