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
public class LabsEncryptionUtilityAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/encryption?viewId=Lab-Encryption
     */
    public static final String BOOKMARK_URL = "/kr-krad/encryption?viewId=Lab-Encryption";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Encryption Utility");
    }

    protected void testDemoEncryptionUtility() throws InterruptedException {
    	waitAndTypeByXpath("//input[@name='input']","admin");
    	waitAndClickByXpath("//button[contains(text(),'Encrypt')]");
    	Thread.sleep(3000);
    	assertTextPresent("kV12AH2uD5s=");
    	assertTextPresent("admin");
    }

    @Test
    public void testDemoEncryptionUtilityBookmark() throws Exception {
    	testDemoEncryptionUtility();
        passed();
    }

    @Test
    public void testDemoEncryptionUtilityNav() throws Exception {
    	testDemoEncryptionUtility();
        passed();
    }
}
