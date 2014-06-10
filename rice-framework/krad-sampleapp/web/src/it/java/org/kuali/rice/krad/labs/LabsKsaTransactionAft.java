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
    	waitAndTypeByName("testField","a");
    	assertTextPresent("Charges");
        jGrowl("Click Delete");
        waitAndClickById("rollupList1_del_line0");
    	assertTextPresent("Deleting Line");
    	waitAndClickByXpath("//img[@class='actionImage leftActionImage uif-image']");
        waitAndClickById("rollupList1_detLink_line1");
    	//Currently throwing Freemarker error so furthur test cannot be processed.
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
