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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoClientResponsivenessEnterKeySupportAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-EnterKeySupport
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-EnterKeySupport";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Enter Key Support");
    }

    protected void testClientResponsivenessEnterKeySupportDefaultViewKey() throws Exception {
    	waitAndClickByXpath("//section[@id='Demo-EnterKeySupport-Example1']/a");
    	switchToWindow("Kuali :: Default View Key");
    	pressEnterByName("field1");
    	acceptAlertIfPresent();
    	switchToWindow("Kuali ::");
    }
    
    protected void testClientResponsivenessEnterKeySupportDefaultPageKey() throws Exception {
    	waitAndClickByXpath("//a[@id='Demo-EnterKeySupport-Example2_tab']");
    	waitAndClickByXpath("//section[@id='Demo-EnterKeySupport-Example2']/a");
    	switchToWindow("Kuali :: Default Page Key");
    	pressEnterByName("field1");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//a[@name='Demo-EnterKeySupport-Example2-Page2']");
    	pressEnterByName("field2");
    	acceptAlertIfPresent();
    	switchToWindow("Kuali ::");
    }
    
    protected void testClientResponsivenessEnterKeySupportDefaultGroupKey() throws Exception {
    	waitAndClickByXpath("//a[@id='Demo-EnterKeySupport-Example3_tab']");
    	pressEnterByName("inputField1");
    	acceptAlertIfPresent();
    	pressEnterByName("inputField2");
    	acceptAlertIfPresent();
    	pressEnterByName("inputField3");
    	acceptAlertIfPresent();
    }
 
 	protected void testClientResponsivenessEnterKeySupportCollectionLineEnterKey() throws Exception {
 		waitAndClickByXpath("//a[@id='Demo-EnterKeySupport-Example4_tab']");
    	pressEnterByXpath("//tbody[@role='alert']/tr/td/div/input");
    	waitForTextPresent("Adding Line...");
 	}
    
    @Test
    public void testClientResponsivenessEnterKeySupportBookmark() throws Exception {
    	testClientResponsivenessEnterKeySupportAll();
    }

    @Test
    public void testClientResponsivenessEnterKeySupportNav() throws Exception {
    	testClientResponsivenessEnterKeySupportAll();
    }  
    
    private void testClientResponsivenessEnterKeySupportAll() throws Exception {
    	testClientResponsivenessEnterKeySupportDefaultViewKey();
    	testClientResponsivenessEnterKeySupportDefaultPageKey();
    	testClientResponsivenessEnterKeySupportDefaultGroupKey();
    	testClientResponsivenessEnterKeySupportCollectionLineEnterKey();
    	passed();
    }
}
