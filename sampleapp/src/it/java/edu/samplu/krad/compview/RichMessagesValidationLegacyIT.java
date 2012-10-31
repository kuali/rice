package edu.samplu.krad.compview;


import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RichMessagesValidationLegacyIT extends WebDriverLegacyITBase {
	
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
		this.verifyBasicFunctionality();
				
		//Verify Advanced Functionality Section
		this.verifyAdvancedFunctionality();
				
		//Verify Letters and Numbers Validation
		this.verifyLettersNumbersValidation();
				
		//Verify Radio and Checkbox groups rich messages Section
		this.verifyRadioAndCheckBoxGroupFunctionality();
				
		//Verify Link Declarations Section
		this.verifyLinkDeclarationsFunctionality();
        passed();
	}
	
	private void verifyBasicFunctionality() throws Exception
	{
		Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field1']"));
		Assert.assertTrue(isElementPresentByXpath("//a[contains(text(), 'Kuali')]"));
		Assert.assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='field2']"));
		Thread.sleep(3000);
	}
	
	private void verifyAdvancedFunctionality() throws Exception
	{
		//Color Options
		Assert.assertTrue(isElementPresentByXpath("//span[@style='color: green;']"));
		Assert.assertTrue(isElementPresentByXpath("//span[@style='color: blue;']"));
				
		//Css class
		Assert.assertTrue(isElementPresentByXpath("//span[@class='fl-text-underline fl-text-larger']"));
				
		//Combinations
		Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field3']"));
		Assert.assertTrue(isElementPresentByXpath("//select[@name='field4']"));
		Assert.assertTrue(isElementPresentByXpath("//button[contains(text(), 'Action Button')]"));
		
		//Rich Message Field
		Assert.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/span[contains(., 'Color')]"));
		Assert.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/i/b[contains(., 'Html')]"));
		Assert.assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/img[@class='uif-image inlineBlock']"));
		
		Thread.sleep(3000);
	}

	private void verifyLettersNumbersValidation() throws Exception
	{
		//For letters only Validation
		Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field5']"));
		waitAndTypeByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","abc");
		Assert.assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
		clearTextByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']");
		waitAndTypeByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","abc12");

		waitAndTypeByXpath("//input[@name= 'field6']","");
		Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
		Thread.sleep(3000);
		clearTextByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']");
		waitAndTypeByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']","abc");
		waitAndTypeByXpath("//input[@name= 'field6']","");
		
		
		//For numbers only validation
		waitAndTypeByXpath("//input[@name= 'field6']","123");
		Assert.assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
		clearTextByXpath("//input[@name= 'field6']");
		waitAndTypeByXpath("//input[@name= 'field6']","123ab");
		fireEvent("field6","blur");
		Thread.sleep(5000);
		Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
	
		Thread.sleep(3000);
	}
	
	private void verifyRadioAndCheckBoxGroupFunctionality() throws Exception
	{
		//Radio Group
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='1']"));
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='2']"));
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='3']"));
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='4']"));
		
		//Checkbox Group
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='1']"));
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='2']"));
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='3']"));
		Assert.assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/label/div/select[@name='field4']"));
		
		//Checkbox Control
		Assert.assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='bField1']"));
		Assert.assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field103']"));
		
	}
	
	private void verifyLinkDeclarationsFunctionality() throws Exception
	{
		//Testing link tag
		waitAndClickByXpath("//div[contains(., 'Testing link tag')]/a");
		Thread.sleep(9000);
		switchToWindow("Open Source Software | www.kuali.org");
		switchToWindow("Kuali :: Rich Messages");
		
		//Testing methodToCall Action
	    waitAndClickByXpath("//div[contains(., 'Testing methodToCall action')]/a");
		Thread.sleep(3000);
		Assert.assertTrue(isElementPresentByXpath("//div[@class='fancybox-wrap fancybox-desktop fancybox-type-html fancybox-opened']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		
		//Testing methodToCall action (no client validation check)
		waitAndClickByXpath("//div[contains(., 'Testing methodToCall action (no client validation check)')]/a");
		Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Thread.sleep(3000);
	}

}
