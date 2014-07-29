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
public class DemoControlTextAreaAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TextAreaControlView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TextAreaControlView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Controls");
        waitAndClickByLinkText("Text Area");
    }

    protected void testLibraryControlTextAreaDefault() throws Exception {
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example1']/textarea[@name='inputField2' and @rows='3' and @cols='40']");
    }
    
    protected void testLibraryControlTextAreaColsAndRowsSet() throws Exception {
        waitAndClickByLinkText("Cols and Rows set");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example2']/textarea[@name='inputField2' and @rows='5' and @cols='60']");
    }
    
    protected void testLibraryControlTextAreaTextExpand() throws Exception {
        waitAndClickByLinkText("Text expand");
        assertElementPresentByXpath("//textarea[@name='inputField2' and @rows='3' and @cols='40']");
        assertElementPresentByXpath("//a[@title='Expand']");
    }
    
    protected void testLibraryControlTextAreaDisabled() throws Exception {
        waitAndClickByLinkText("Disabled");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example4']/textarea[@name='inputField2' and @rows='3' and @cols='40' and @disabled]");
    }
    
    protected void testLibraryControlTextAreaWatermarkText() throws Exception {
        waitAndClickByLinkText("WatermarkText");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example7']/textarea[@name='inputField2' and @rows='3' and @cols='40' and @placeholder='watermark text ']");
    }
    
    protected void testLibraryControlTextAreaEvaluateDisabledOnKeyUp() throws Exception {
        waitAndClickByLinkText("Evaluate disabled on key up");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example8']/textarea[@name='inputField2' and @rows='3' and @cols='40' and @disabled]");
        waitAndTypeByXpath("//div[@data-parent='Demo-TextAreaControl-Example8']/textarea[@name='inputField1']","a");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example8']/textarea[@name='inputField2' and @rows='3' and @cols='40']");
    }
    
    protected void testLibraryControlTextAreaEnableWhenChanged() throws Exception {
        waitAndClickByLinkText("Enable when changed");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example9']/textarea[@name='inputField4' and @rows='3' and @cols='40' and @disabled]");
        waitAndTypeByXpath("//div[@data-parent='Demo-TextAreaControl-Example9']/textarea[@name='inputField3']","a");
        typeTab();
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example9']/textarea[@name='inputField4' and @rows='3' and @cols='40']");
    }
    
    protected void testLibraryControlTextAreaDisableWhenChanged() throws Exception {
        waitAndClickByLinkText("Disable when changed");
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example10']/textarea[@name='inputField6' and @rows='3' and @cols='40']");
        waitAndTypeByXpath("//div[@data-parent='Demo-TextAreaControl-Example10']/textarea[@name='inputField5']","a");
        fireEvent("inputField5", "blur");
        typeTab();
        assertElementPresentByXpath("//div[@data-parent='Demo-TextAreaControl-Example10']/textarea[@name='inputField6' and @rows='3' and @cols='40' and @disabled]");
    }
    
    @Test
    public void testControlTextAreaBookmark() throws Exception {
        testLibraryControlTextArea();
        passed();
    }

    @Test
    public void testControlTextAreaNav() throws Exception {
        testLibraryControlTextArea();
        passed();
    }

    private void testLibraryControlTextArea() throws Exception {
        testLibraryControlTextAreaDefault();
        testLibraryControlTextAreaColsAndRowsSet();
        testLibraryControlTextAreaTextExpand();
        testLibraryControlTextAreaDisabled();
        testLibraryControlTextAreaWatermarkText();
        testLibraryControlTextAreaEvaluateDisabledOnKeyUp();
        testLibraryControlTextAreaEnableWhenChanged();
        testLibraryControlTextAreaDisableWhenChanged();
    }
}
