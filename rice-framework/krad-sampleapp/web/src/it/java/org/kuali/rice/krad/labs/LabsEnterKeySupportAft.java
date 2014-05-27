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
public class LabsEnterKeySupportAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-LayoutTest-EnterKey
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-LayoutTest-EnterKey";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Enter Key Support");
    }

    protected void testDemoEnterKeySupport() throws InterruptedException {
    	waitAndTypeByName("inputField12","");
    	pressEnterByName("inputField12");
    	acceptAlert();
    	waitAndTypeByName("inputField14","");
    	pressEnterByName("inputField14");
        acceptAlert();
    	waitAndTypeByName("inputField16","");
    	pressEnterByName("inputField16");
        acceptAlert();
    	waitAndTypeByName("inputField18","");
    	pressEnterByName("inputField18");
        acceptAlert();
    	waitAndTypeByName("inputField20","");
    	pressEnterByName("inputField20");
        acceptAlert();
    }

    @Test
    public void testDemoEnterKeySupportBookmark() throws Exception {
    	testDemoEnterKeySupport();
        passed();
    }

    @Test
    public void testDemoEnterKeySupportNav() throws Exception {
    	testDemoEnterKeySupport();
        passed();
    }
}
