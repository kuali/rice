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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoClientResponsivenessAjaxFieldQueryAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-AjaxFieldQueryView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-AjaxFieldQueryView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("AJAX Field Query");
    }

    protected void testClientResponsivenessAjaxFieldQuery() throws Exception {
    	waitAndClickByXpath("//li[@data-tabFor='Demo-AjaxFieldQuery-Example1']/a[contains(text(),'Ajax Field Query')]");
    	waitForElementPresentByXpath("//input[@name='inputField3' and @value='a1']");
    }
    
    protected void testClientResponsivenessAjaxFieldQueryCustomMethod() throws Exception {
    	waitAndClickByXpath("//li[@data-tabFor='Demo-AjaxFieldQuery-Example2']/a[contains(text(),'Ajax Field Query Custom Method')]");
    	waitForElementPresentByXpath("//input[@name='inputField6' and @value='a2']");
    }
    
    protected void testClientResponsivenessAjaxFieldQueryCustomMethodAndService() throws Exception {
    	waitAndClickByXpath("//li[@data-tabFor='Demo-AjaxFieldQuery-Example3']/a[contains(text(),'Ajax Field Query Custom Method and Service')]");
    	waitForElementPresentByXpath("//input[@name='inputField9' and @value='a3']");
    }
    
    @Test
    public void testClientResponsivenessAjaxFieldQueryBookmark() throws Exception {
        testClientResponsivenessAjaxFieldQuery();
        testClientResponsivenessAjaxFieldQueryCustomMethod();
        testClientResponsivenessAjaxFieldQueryCustomMethodAndService();
        passed();
    }

    @Test
    public void testClientResponsivenessAjaxFieldQueryNav() throws Exception {
        testClientResponsivenessAjaxFieldQuery();
        testClientResponsivenessAjaxFieldQueryCustomMethod();
        testClientResponsivenessAjaxFieldQueryCustomMethodAndService();
        passed();
    }  
}
