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
package org.kuali.rice.krad.demo.uif.library.controls;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoControlSpinnerAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-SpinnerControlView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-SpinnerControlView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Spinner");
    }

    protected void testLibraryControlSpinnerDefault() throws Exception {
        assertElementPresentByXpath("//input[@name='inputField1' and @size='10']");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/a[@tabindex='-1']");
    }
    
    protected void testLibraryControlSpinnerCurrency() throws Exception {
        waitAndClickByLinkText("Currency option");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/a[@tabindex='-1']");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/input[@aria-valuenow='25']");
    }
    
    protected void testLibraryControlSpinnerDecimal() throws Exception {
        waitAndClickByLinkText("Decimal option");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/a[@tabindex='-1']");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/input[@aria-valuenow='0.01']");
    }
    
    protected void testLibraryControlSpinnerWidgetInput() throws Exception {
        waitAndClickByLinkText("Widget Input Only");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/a[@tabindex='-1']");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/input[@aria-valuenow='1']");
    }
    
    @Test
    public void testControlSpinnerBookmark() throws Exception {
        testLibraryControlSpinnerDefault();
        testLibraryControlSpinnerCurrency();
        testLibraryControlSpinnerDecimal();
        testLibraryControlSpinnerWidgetInput();
        passed();
    }

    @Test
    public void testControlSpinnerNav() throws Exception {
        testLibraryControlSpinnerDefault();
        testLibraryControlSpinnerCurrency();
        testLibraryControlSpinnerDecimal();
        testLibraryControlSpinnerWidgetInput();
        passed();
    }  
}
