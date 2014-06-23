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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsOtherFieldsAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page2#UifCompView-Page2
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page2#UifCompView-Page2";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Other Fields");
	}
	
    @Test
    public void testAttributeSecurityBookmark() throws Exception {
        testAttributeSecurity();
        passed();
    }

    @Test
    public void testAttributeSecurityNav() throws Exception {
        testAttributeSecurity();
        passed();
    }

    @Test
    public void testOtherFieldsBookmark() throws Exception {
        testOtherFields();
        passed();
    }

    @Test
    public void testOtherFieldsNav() throws Exception {
        testOtherFields();
        passed();
    }
    
    protected void testOtherFields() throws InterruptedException {
    	//Field Group
    	waitForElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutVerticalItem pull-left clearfix']/label[contains(text(),'Field 1:')]");
    	assertElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutVerticalItem pull-left clearfix']/input");
    	waitForElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutVerticalItem pull-left clearfix']/label[contains(text(),'Field 2:')]");
    	waitForElementPresentByXpath("//div[@id='UifCompView-FieldGroup2']/fieldset/div[@class='uif-horizontalBoxGroup clearfix']");
    	waitForElementPresentByXpath("//div[@id='UifCompView-FieldGroup3']/fieldset/div/div/input");
    	waitForElementPresentByXpath("//div[@id='UifCompView-FieldGroup3']/fieldset/div/button");
    	
    	//Message View
    	waitForElementPresentByXpath("//p[@id='UifCompView-MessageField1']");
    	waitForElementPresentByXpath("//p[@id='UifCompView-MessageFieldWithExpression']");
    	
    	//Syntax Highliter
    	fireMouseOverEventByXpath("//div[@id='UifCompView-SyntaxHighlighter1']/div[@class='uif-syntaxHighlighter']");
    	waitForElementPresentByXpath("//a[@id='UifCompView-SyntaxHighlighter1_syntaxHighlightCopy']");
    	fireMouseOverEventByXpath("//div[@id='UifCompView-SyntaxHighlighter2']/div[@class='uif-syntaxHighlighter']");
    	waitForElementPresentByXpath("//a[@id='UifCompView-SyntaxHighlighter2_syntaxHighlightCopy']");
    	acceptAlertIfPresent();
    	fireMouseOverEventByXpath("//div[@id='UifCompView-SyntaxHighlighter3']/div[@class='uif-syntaxHighlighter']");
    	if(isElementPresentByXpath("//a[@id='UifCompView-SyntaxHighlighter3_syntaxHighlightCopy']")) {
    		fail("Copy is allowed.");
    	}

    	//Security Fields
    	waitForTextPresent("*********");
    	waitForElementPresentByXpath("*****tInfo111");
    	
        //testAttributeSecurity(); // currently failing commented out till fixed and the attribute security test methods removed

        //Image Fields
    	waitForElementPresentByXpath("//img[@alt='pdf image']");
    	assertTextPresent("Image cutline text here ");
    	waitForElementPresentByXpath("//div[@id='UifCompView-ImageField2' and @title='computer programming']");
    	
    	//Action Fields
    	waitAndClickByXpath("//button[@id='submitButton1']");
    	waitForElementPresentByXpath("//div[@id='UifCompView-PopoverContent-1' and @style='margin-bottom: 0px; padding-left: 10px; display: block;']");
    	waitForElementPresentByXpath("//a[@id='UifCompView-ActionField9']/img[@class='actionImage rightActionImage uif-image']");
    	waitForElementPresentByXpath("//a[@id='UifCompView-ActionField11']/img[@class='actionImage leftActionImage uif-image']");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField presubmit call true')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField presubmit call false')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField ajaxSubmit call false')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField sucessCallBack')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField validate')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField errorCallBack')]");
    	waitForElementPresentByXpath("//a[contains(text(),'ActionLinkField ajax redirect')]");
    	waitForElementPresentByXpath("//input[@type='image' and @id='UifCompView-ActionField13']");
    	waitForElementPresentByXpath("//button[@id='UifCompView-ActionField14']");
    	waitForElementPresentByXpath("//button[@id='UifCompView-ActionField15']/span/img[@class='actionImage bottomActionImage uif-image']");
    	waitForElementPresentByXpath("//button[@id='UifCompView-ActionField16']/span/img[@class='actionImage topActionImage uif-image']");
    	waitForElementPresentByXpath("//button[@id='UifCompView-ActionField17']/img[@class='actionImage leftActionImage uif-image']");
    	waitForElementPresentByXpath("//button[@id='UifCompView-ActionField18']/img[@class='actionImage rightActionImage uif-image']");
    	waitForElementPresentByXpath("//button[@id='UifCompView-ActionField20' and @disabled]");
    	waitForElementPresentByXpath("//button/img[@alt='Image Only button']");
    	
    	//Link Fields
    	waitForElementPresentByXpath("//a[@href='http://www.kuali.org' and @target='_self']");
    	waitForElementPresentByXpath("//a[@href='http://www.kuali.org' and @target='_blank']");
    	waitAndClickByXpath("//div[@id='UifCompView-LinkField3']/a[@id='UifCompView-LinkField4']");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//div[@id='UifCompView-LinkField3']/a[@id='UifCompView-LinkField5']");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//div[@id='UifCompView-LinkField3']/a[@id='UifCompView-LinkField6']");
    	acceptAlertIfPresent();
    	
    	//Miscellaneous Fields and Groups
    	waitForElementPresentByXpath("//ul[@id='UifCompView-Accordion1_accordList']/li/a");
    	waitForElementPresentByXpath("//div[@id='UifCompView-SubList1' and @style='display: none;']");
    	waitAndClickByXpath("//ul[@id='UifCompView-Accordion1_accordList']/li/a");
    	waitForElementPresentByXpath("//div[@id='UifCompView-SubList1' and @style='display: block;']");
    	waitForElementPresentByXpath("//div[@id='UifCompView-List1']/ul");
    	waitForElementPresentByXpath("//div[@id='UifCompView-List2']/ul");
    	
    	//Tabs
    	waitForElementPresentByXpath("//input[@placeholder and @size='30' and @maxlength='40']");
    	waitForElementPresentByXpath("//input[@size='60']");
    	waitAndClickByXpath("//a[@title='Expand']");
    	waitForElementPresentByXpath("//textarea[@id='textarea_popout_control']");
    	waitAndClickByXpath("//input[@id='done_btn']");
    	waitForElementPresentByXpath("//input[@disabled and @name='field109']");
    	waitForElementPresentByXpath("//input[@style='text-transform: uppercase;']");
    	waitForElementPresentByXpath("//ul[@id='UifCompView-OtherField1_tabList']/li/a[contains(text(),'Text Control Options')]");
    	waitForElementPresentByXpath("//ul[@id='UifCompView-OtherField1_tabList']/li/a[contains(text(),'TextArea Control Options')]");
    
    	//Basic String Data tree and Tree with Data Group
    	waitForElementPresentByXpath("//div[@id='UifCompView-OtherField2_tree']/ul/li/a/span[contains(text(),'Item 1 ')]");
    	waitForElementPresentByXpath("//div[@id='UifCompView-OtherField3_tree']/ul/li/div/div[@class='uif-verticalBoxLayout clearfix']");
    	waitForElementPresentByXpath("//iframe[@src='http://www.kuali.org']");
    
    	//Scrollable Groups
    	waitForElementPresentByXpath("//div[@style='height: 100px;overflow: auto;']");
    }

    private void testAttributeSecurity() throws InterruptedException { //Attribute Security
        boolean fullMaskPassed = false;
        String fullMaskedError = "";
        waitForTextPresent("*********"); // The Input Fields page use SecretInfo555 as a data key, so wait to make sure the Other Fields page has loaded
        if (isTextPresent("SecretInfo555")) {
            fullMaskedError = "SecretInfo555 not masked!";
        } else {
            fullMaskPassed = true;
        }

        if (!isTextPresent("*********")) {
            fullMaskPassed = false;
            fullMaskedError = fullMaskedError + " SecretInfo555 not masked with stars";
        }

        boolean partialMaskPassed = false;
        String partialMaskError = "";
        if (isTextPresent("SecretInfo111")) {
            partialMaskError = "SecretInfo111 not masked!";
        } else {
            partialMaskPassed = true;
        }

        if (!isTextPresent("*****tInfo111")) {
            partialMaskPassed = false;
            partialMaskError = partialMaskError + " SecretInfo111 not partially masked with stars";
        }

        if (!fullMaskPassed || !partialMaskPassed) {
            jiraAwareFail("LabsOtherFieldsAft attribute security fail " + fullMaskedError + " " + partialMaskError);
        }
    }
}
