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

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsCustomRuleEventAndDocumentEventMethodInvocationAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/ruleEvent?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount
     */
    public static final String BOOKMARK_URL = "/kr-krad/ruleEvent?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Custom Rule Event and Document Event Method Invocation");
    }

    protected void testDemoCustomRuleEventAndDocumentEventMethodInvocation() throws InterruptedException {
    	 waitAndTypeByName("document.documentHeader.documentDescription","Travel Account Maintenance New Test Document");
         String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
         waitAndTypeByName("document.newMaintainableObject.dataObject.number",randomCode);
         waitAndTypeByName("document.newMaintainableObject.dataObject.name","Test Account Name");
         waitAndClickByName("document.newMaintainableObject.dataObject.accountTypeCode");
         waitAndClickButtonByExactText("Save");
         waitForTextPresent("Document was successfully saved.");
//         waitAndClickByXpath("//button[@data-dismissdialogid='ConfirmBlanketApproveDialog' and contains(text(),'OK')]");
         waitAndClickSubmitByText();
         waitAndClickConfirmationOk();
         failOnErrorMessageItem();
         waitForTextPresent("Document was successfully submitted.", WebDriverUtils.configuredImplicityWait() * 2);
    }

    @Test
    public void testDemoCustomRuleEventAndDocumentEventMethodInvocationBookmark() throws Exception {
    	testDemoCustomRuleEventAndDocumentEventMethodInvocation();
        passed();
    }

    @Test
    public void testDemoCustomRuleEventAndDocumentEventMethodInvocationNav() throws Exception {
    	testDemoCustomRuleEventAndDocumentEventMethodInvocation();
        passed();
    }
}
