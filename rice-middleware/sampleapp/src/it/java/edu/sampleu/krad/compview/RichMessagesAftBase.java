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
package edu.sampleu.krad.compview;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class RichMessagesAftBase extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=RichMessagesView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=RichMessagesView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws Exception {
        waitAndClickKRAD();
        waitAndClickByLinkText("Rich Messages");
        switchToWindow(RICH_MESSAGES_WINDOW_TITLE);
        checkForIncidentReport(RICH_MESSAGES_WINDOW_TITLE);
        Thread.sleep(9000);
    }

    protected void testRichMessagesNav(JiraAwareFailable failable) throws Exception {
        navigation();
        //Verify Basic Functionality Section
        super.verifyRichMessagesValidationBasicFunctionality();
                
        //Verify Advanced Functionality Section
        super.verifyRichMessagesValidationAdvancedFunctionality();
                
        //Verify Letters and Numbers Validation
        super.verifyRichMessagesValidationLettersNumbersValidation();
                
        //Verify Radio and Checkbox groups rich messages Section
        super.verifyRichMessagesValidationRadioAndCheckBoxGroupFunctionality();
                
        //Verify Link Declarations Section
        super.verifyRichMessagesValidationLinkDeclarationsFunctionality();
        passed();
    }

    protected void testRichMessagesBookmark(JiraAwareFailable failable) throws Exception {
        checkForIncidentReport(getTestUrl());
        Thread.sleep(9000);
        
        //Verify Basic Functionality Section
        super.verifyRichMessagesValidationBasicFunctionality();
                
        //Verify Advanced Functionality Section
        super.verifyRichMessagesValidationAdvancedFunctionality();
                
        //Verify Letters and Numbers Validation
        super.verifyRichMessagesValidationLettersNumbersValidation();
                
        //Verify Radio and Checkbox groups rich messages Section
        super.verifyRichMessagesValidationRadioAndCheckBoxGroupFunctionality();
                
        //Verify Link Declarations Section
        super.verifyRichMessagesValidationLinkDeclarationsFunctionality();
        passed();
    }
  
}
