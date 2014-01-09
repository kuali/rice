/*
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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsOtherFieldsAft extends LabsKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=cd45c8fc-586b-4174-866f-e2abf5171a49&cacheKey=qlapnzravopc76l0ypny3gjt9b&pageId=UifCompView-Page2#UifCompView-Page2";
    
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
    	assertElementPresentByXpath("//div[@id='UifCompView-FieldGroup1' and @class='uif-verticalFieldGroup uif-boxLayoutVerticalItem clearfix']");
    	assertElementPresentByXpath("//div[@id='UifCompView-FieldGroup2' and @class='uif-horizontalFieldGroup uif-boxLayoutVerticalItem clearfix']");
    	
    	//Message View
    	assertElementPresentByXpath("//span[@id='UifCompView-MessageField1_span']");
    	assertElementPresentByXpath("//span[@id='UifCompView-MessageFieldWithExpression_span']");
    	
    	//Syntax Highliter
    	fireMouseOverEventByXpath("//div[@id='UifCompView-SyntaxHighlighter1']/div[@class='uif-syntaxHighlighter']");
    	assertElementPresentByXpath("//embed[@id='ZeroClipboardMovie_1']");
    	fireMouseOverEventByXpath("//div[@id='UifCompView-SyntaxHighlighter2']/div[@class='uif-syntaxHighlighter']");
    	assertElementPresentByXpath("//embed[@id='ZeroClipboardMovie_2']");
    	fireMouseOverEventByXpath("//div[@id='UifCompView-SyntaxHighlighter3']/div[@class='uif-syntaxHighlighter']");
    	if(isElementPresentByXpath("//embed[@id='ZeroClipboardMovie_3']")) {
    		fail("Copy is allowed.");
    	}

        //testAttributeSecurity(); // currently failing commented out till fixed and the attribute security test methods removed

        //Image Fields
    	assertElementPresentByXpath("//img[@alt='pdf image']");
    	assertTextPresent("Image cutline text here ");
    	assertElementPresentByXpath("//div[@id='UifCompView-ImageField2' and @title='computer programming']");
    	
    	//Action Fields
    	waitAndClickByXpath("//button[@id='submitButton1']");
    	assertElementPresentByXpath("//div[@id='UifCompView-PopoverContent-1' and @style='margin-bottom: 0px; padding-left: 10px; display: block;']");
    	assertElementPresentByXpath("//a[@id='UifCompView-ActionField9']/img[@class='actionImage rightActionImage uif-image']");
    	assertElementPresentByXpath("//a[@id='UifCompView-ActionField11']/img[@class='actionImage leftActionImage uif-image']");
    	assertElementPresentByXpath("//button[@id='UifCompView-ActionField15']/span/img[@class='actionImage bottomActionImage uif-image']");
    	assertElementPresentByXpath("//button[@id='UifCompView-ActionField16']/span/img[@class='actionImage topActionImage uif-image']");
    	assertElementPresentByXpath("//button[@id='UifCompView-ActionField20' and @disabled]");
    	
    	//Link Fields
    	assertElementPresentByXpath("//a[@href='http://www.kuali.org' and @target='_self']");
    	assertElementPresentByXpath("//a[@href='http://www.kuali.org' and @target='_blank']");
    	
    	//Miscellaneous Fields and Groups
    	assertElementPresentByXpath("//ul[@id='UifCompView-Accordion1_accordList']/li/a");
    	
    	//Tabs
    	assertElementPresentByXpath("//ul[@id='UifCompView-OtherField1_tabList']/li/a[contains(text(),'Text Control Options')]");
    	assertElementPresentByXpath("//ul[@id='UifCompView-OtherField1_tabList']/li/a[contains(text(),'TextArea Control Options')]");
    
    	//Basic String Data tree and Tree with Data Group
    	assertElementPresentByXpath("//div[@id='UifCompView-OtherField2_tree']/ul/li/a/span[contains(text(),'Item 1 ')]");
    	assertElementPresentByXpath("//div[@id='UifCompView-OtherField3_tree']/ul/li/div/div[@class='uif-horizontalFieldGroup uif-boxLayoutVerticalItem clearfix']");
    
    	//Scrollable Groups
    	assertElementPresentByXpath("//div[@id='UifCompView-ScrollableGroups']/div[@style='height: 100px;overflow: auto;']");
    	assertElementPresentByXpath("//div[@style='height: 100px;overflow: auto;']");
    }

    private void testAttributeSecurity() {//Attribute Security
        boolean fullMaskPassed = false;
        String fullMaskedError = "";
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
