/**
 * Copyright 2005-2018 The Kuali Foundation
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
public class LabsDialogWithExplanationReadOnlyAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-DialogReadOnly
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-DialogReadOnly";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    public String getUserName() {
        return "guest";
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Dialog with Explanation in ReadOnly mode");
    }

    protected void testDemoDialogWithExplanationReadOnly() throws InterruptedException {
    	waitAndClickByXpath("//button[@data-confirmdialogid='Lab-DialogEx']");
    	waitAndTypeByXpath("//div[@data-parent='Lab-DialogEx']/textarea","");
    	waitAndClickByXpath("//div[@data-parent='Lab-DialogEx']/button[contains(text(),'OK')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Required')]");
    	waitAndTypeByXpath("//div[@data-parent='Lab-DialogEx']/textarea","a");
    	waitAndClickByXpath("//div[@data-parent='Lab-DialogEx']/button[contains(text(),'OK')]");
    	waitAndClickByXpath("//button[@data-confirmdialogid='Lab-DialogExReadOnly']");
    	waitAndTypeByXpath("//div[@data-parent='Lab-DialogExReadOnly']/textarea","");
    	waitAndClickByXpath("//div[@data-parent='Lab-DialogExReadOnly']/button[contains(text(),'OK')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Required')]");
    	waitAndTypeByXpath("//div[@data-parent='Lab-DialogExReadOnly']/textarea","a");
    	waitAndClickByXpath("//div[@data-parent='Lab-DialogExReadOnly']/button[contains(text(),'OK')]");
    	//Read Only Explanation is also having textarea. This functionality is not working properly.
    }

    @Test
    public void testDemoDialogWithExplanationReadOnlyBookmark() throws Exception {
    	testDemoDialogWithExplanationReadOnly();
        passed();
    }

    @Test
    public void testDemoDialogWithExplanationReadOnlyNav() throws Exception {
    	testDemoDialogWithExplanationReadOnly();
        passed();
    }
}
