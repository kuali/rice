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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoKSATransactionAft extends WebDriverLegacyITBase {

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

    protected void testKSATransaction() throws InterruptedException {
    	waitAndTypeByName("testField","a");
    	assertTextPresent("Charges");
    	waitAndClickButtonByText("delete");
    	assertTextPresent("Deleting Line");
    	waitAndClickByXpath("//img[@class='actionImage leftActionImage uif-image']");
    	//Currently throwing Freemarker error so furthur test cannot be processed.
    }

    @Test
    public void testKSATransactionBookmark() throws Exception {
    	testKSATransaction();
        passed();
    }

//    @Test
    public void testKSATransactionNav() throws Exception {
    	testKSATransaction();
        passed();
    }
}
