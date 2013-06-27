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
package edu.samplu.krad.compview;

import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RichMessagesValidationWDIT extends WebDriverLegacyITBase {

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=RichMessagesView&methodToCall=start";
    }

	@Test
	public void testRichMessagesValidationIT() throws Exception {
		
		checkForIncidentReport(getTestUrl());
        Thread.sleep(9000);
//        selectWindow(RICH_MESSAGES_WINDOW_TITLE);
		
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
