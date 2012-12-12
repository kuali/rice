package edu.samplu.krad.compview;


import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RichMessagesValidationIT extends UpgradedSeleniumITBase {
	
	@Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=RichMessagesView&methodToCall=start";
    }

	@Test
	public void testRichMessagesValidationIT() throws Exception {
		
		checkForIncidentReport(getTestUrl());
        Thread.sleep(9000);
        selectWindow("title=Kuali :: Rich Messages");
		
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
	}
	
	private void verifyBasicFunctionality() throws Exception
	{
		Assert.assertTrue(isElementPresent("//input[@type='text' and @name='field1']"));
		Assert.assertTrue(isElementPresent("//a[contains(text(), 'Kuali')]"));
		Assert.assertTrue(isElementPresent("//input[@type='checkbox' and @name='field2']"));
		Thread.sleep(3000);
	}
	
	private void verifyAdvancedFunctionality() throws Exception
	{
		//Color Options
		Assert.assertTrue(isElementPresent("//span[@style='color: green;']"));
		Assert.assertTrue(isElementPresent("//span[@style='color: blue;']"));
				
		//Css class
		Assert.assertTrue(isElementPresent("//span[@class='fl-text-underline fl-text-larger']"));
				
		//Combinations
		Assert.assertTrue(isElementPresent("//input[@type='text' and @name='field3']"));
		Assert.assertTrue(isElementPresent("//select[@name='field4']"));
		Assert.assertTrue(isElementPresent("//button[contains(text(), 'Action Button')]"));
		
		//Rich Message Field
		Assert.assertTrue(isElementPresent("//label[contains(., 'Label With')]/span[contains(., 'Color')]"));
		Assert.assertTrue(isElementPresent("//label[contains(., 'Label With')]/i/b[contains(., 'Html')]"));
		Assert.assertTrue(isElementPresent("//label[contains(., 'Label With')]/img[@class='uif-image inlineBlock']"));
		
		Thread.sleep(3000);
	}

	private void verifyLettersNumbersValidation() throws Exception
	{
		//For letters only Validation
		Assert.assertTrue(isElementPresent("//input[@type='text' and @name='field5']"));
		focusAndType("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","abc");
		Assert.assertFalse(isElementPresent("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
		focusAndType("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","abc12");
		fireEvent("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock']/input[@name= 'field5']","blur");
		Thread.sleep(3000);
		Assert.assertTrue(isElementPresent("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
		focusAndType("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']","abc");
		fireEvent("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']/input[@name= 'field5']","blur");
		
		//For numbers only validation
		focusAndType("//input[@name= 'field6']","123");
		Assert.assertFalse(isElementPresent("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
		focusAndType("//input[@name= 'field6']","123ab");
		fireEvent("//input[@name= 'field6']","blur");
		Thread.sleep(5000);
		Assert.assertTrue(isElementPresent("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
	
		Thread.sleep(3000);
	}
	
	private void verifyRadioAndCheckBoxGroupFunctionality() throws Exception
	{
		//Radio Group
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='1']"));
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='2']"));
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='3']"));
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='4']"));
		
		//Checkbox Group
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='1']"));
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='2']"));
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='3']"));
		Assert.assertTrue(isElementPresent("//fieldset[@class='uif-verticalCheckboxesFieldset']/span/label/div/select[@name='field4']"));
		
		//Checkbox Control
		Assert.assertTrue(isElementPresent("//input[@type='checkbox' and @name='bField1']"));
		Assert.assertTrue(isElementPresent("//input[@type='text' and @name='field103']"));
		
	}
	
	private void verifyLinkDeclarationsFunctionality() throws Exception
	{
		//Testing link tag
		waitAndClick("//div[contains(., 'Testing link tag')]/a");
		Thread.sleep(4000);
		selectWindow("title=Open Source Software | www.kuali.org");
		selectWindow("title=Kuali :: Rich Messages");
		
		//Testing methodToCall Action
	    waitAndClick("//div[contains(., 'Testing methodToCall action')]/a");
		Thread.sleep(3000);
		Assert.assertTrue(isElementPresent("//div[@class='fancybox-wrap fancybox-desktop fancybox-type-html fancybox-opened']"));
		Assert.assertTrue(isElementPresent("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
		Assert.assertTrue(isElementPresent("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Assert.assertTrue(isElementPresent("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		
		//Testing methodToCall action (no client validation check)
		waitAndClick("//div[contains(., 'Testing methodToCall action (no client validation check)')]/a");
		Assert.assertTrue(isElementPresent("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
		Assert.assertTrue(isElementPresent("//div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Assert.assertTrue(isElementPresent("//div[@class='uif-validationMessages uif-groupValidationMessages uif-pageValidationMessages uif-pageValidationMessages-error']"));
		Assert.assertTrue(isElementPresent("//div[@id='Demo-AdvancedMessagesSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Assert.assertTrue(isElementPresent("//div[@id='Demo-RadioCheckboxMessageSection']/div[@class='uif-validationMessages uif-groupValidationMessages']"));
		Thread.sleep(3000);
	}

}
