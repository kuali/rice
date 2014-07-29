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
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Spinner");
    }

    protected void testLibraryControlSpinnerDefault() throws Exception {
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/input");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/a[contains(@class,'ui-spinner-up')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/a[contains(@class,'ui-spinner-down')]");

        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/input[@value='']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/a[contains(@class,'ui-spinner-up')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/input[@aria-valuenow='1']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/a[contains(@class,'ui-spinner-down')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example1']/span/input[@aria-valuenow='0']");
    }
    
    protected void testLibraryControlSpinnerCurrency() throws Exception {
        waitAndClickByLinkText("Currency option");

        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/input[@aria-valuenow='24']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/a[contains(@class,'ui-spinner-up')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/input[@aria-valuenow='55']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/a[contains(@class,'ui-spinner-down')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example2']/span/input[@aria-valuenow='24']");
    }
    
    protected void testLibraryControlSpinnerDecimal() throws Exception {
        waitAndClickByLinkText("Decimal option");

        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/input[@value='']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/a[contains(@class,'ui-spinner-up')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/input[@aria-valuenow='0.01']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/a[contains(@class,'ui-spinner-down')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example3']/span/input[@aria-valuenow='0']");
    }
    
    protected void testLibraryControlSpinnerWidgetInput() throws Exception {
        waitAndClickByLinkText("Widget Input Only");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/input[@readonly='readonly']");

        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/input[@value='']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/a[contains(@class,'ui-spinner-up')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/input[@aria-valuenow='1']");
        waitAndClickByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/a[contains(@class,'ui-spinner-down')]");
        assertElementPresentByXpath("//div[@data-parent='Demo-SpinnerControl-Example4']/span/input[@aria-valuenow='0']");
    }
    
    @Test
    public void testControlSpinnerBookmark() throws Exception {
        testLibraryControlSpinnerDefault();
        testLibraryControlSpinnerDecimal();
        testLibraryControlSpinnerWidgetInput();
        testLibraryControlSpinnerCurrency();
        passed();
    }

    @Test
    public void testControlSpinnerNav() throws Exception {
        testLibraryControlSpinnerCurrency();
        testLibraryControlSpinnerDefault();
        testLibraryControlSpinnerDecimal();
        testLibraryControlSpinnerWidgetInput();
        passed();
    }  
}
