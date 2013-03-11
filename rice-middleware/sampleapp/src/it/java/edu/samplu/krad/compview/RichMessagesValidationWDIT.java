package edu.samplu.krad.compview;


import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RichMessagesValidationWDIT extends WebDriverLegacyITBase {
	
	@Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=RichMessagesView&methodToCall=start";
    }

	@Test
	public void testRichMessagesValidationIT() throws Exception {
		
		checkForIncidentReport(getTestUrl());
        Thread.sleep(9000);
//        selectWindow("Kuali :: Rich Messages");
		
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
