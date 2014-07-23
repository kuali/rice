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
package org.kuali.rice.krad.demo.uif.library.fields;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoFieldsInputAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-InputFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-InputFieldView&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Input Field");
    }

    protected void testInputFieldDefault() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example1");
        WebElement field = findElement(By.cssSelector("div[data-label='InputField 1']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("label[for='" + controlId + "']"), field);
        if (!label.getText().contains("InputField 1:")) {
            fail("Label text does not match");
        }

        assertIsVisible("#" + controlId);

        waitAndType(By.cssSelector("#" + controlId), "Test InputField");

        // validate that the value comes after the label
        findElement(By.cssSelector("label[data-label_for='" + fieldId + "'] + input[id='" + controlId + "']"),
                exampleDiv);
    }

    protected void testInputFieldAltControl() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example2");
        WebElement field = findElement(By.cssSelector("div[data-label='InputField 2']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("label[for='" + controlId + "']"), field);
        if (!label.getText().contains("InputField 2:")) {
            fail("Label text does not match");
        }

        assertIsVisible("#" + controlId);

        waitAndType(By.cssSelector("#" + controlId), "Test InputField");

        // validate that the value comes after the label
        findElement(By.cssSelector("label[data-label_for='" + fieldId + "'] + textarea[id='" + controlId + "']"),
                exampleDiv);
    }

    protected void testInputFieldInstructionalText() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example3");
        WebElement field = findElement(By.cssSelector("div[data-label='InputField 3']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String instructionalTextId = fieldId + UifConstants.IdSuffixes.INSTRUCTIONAL;
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + instructionalTextId);
        assertTextPresent("Instructions for this field", "#" + instructionalTextId, "InputField value not correct");

        // validate that the instructional text comes after the label
        findElement(By.cssSelector("label[data-label_for='" + fieldId + "'] + p[id='" + instructionalTextId + "']"),
                exampleDiv);

        // validate that the value comes after the instructional text
        findElement(By.cssSelector("p[id='" + instructionalTextId + "'] + input[id='" + controlId + "']"),
                exampleDiv);
    }

    protected void testInputFieldConstraintText() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example4");
        WebElement field = findElement(By.cssSelector("div[data-label='InputField 4']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String constraintTextId = fieldId + UifConstants.IdSuffixes.CONSTRAINT;

        assertIsVisible("#" + constraintTextId);
        assertTextPresent("Text to tell users about constraints this field may have", "#" + constraintTextId,
                "InputField value not correct");

        // validate that the value comes after the label
        findElement(By.cssSelector("label[data-label_for='" + fieldId + "'] + input[id='" + controlId + "']"),
                exampleDiv);

        // validate that the constraint text comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] ~ p[id='" + constraintTextId + "']"), exampleDiv);
    }

    protected void testInputFieldLabelTop() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example5");
        WebElement field = findElement(By.cssSelector("div[data-label='Label Top Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("label[for='" + controlId + "']"), field);
        if (!label.getText().contains("Label Top Field")) {
            fail("Label text does not match");
        }

        WebElement labelSpan = findElement(By.cssSelector("label[data-label_for='" + fieldId + "']"), field);
        // top and bottom add the uif-labelBlock class
        if (!labelSpan.getAttribute("class").contains("uif-labelBlock")) {
            fail("Label span does not contain the appropriate class expected");
        }
    }

    protected void testInputFieldLabelRight() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example6");
        WebElement field = findElement(By.cssSelector("div[data-label='Label Right Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + controlId);

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("label[for='" + controlId + "']"), field);
        if (!label.getText().contains("Label Right Field")) {
            fail("Label text does not match");
        }

        // validate that the label comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] + label[data-label_for='" + fieldId + "']"),
                exampleDiv);
    }

    protected void testInputFieldQuickfinder() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example7");
        WebElement field = findElement(By.cssSelector("div[data-label='Quickfinder Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String quickfinderId = findElement(By.xpath("//div[@data-label=\"Quickfinder Field\"]/div/div/button")).getAttribute("id");

        // validate that the quickfinder comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] + div>button[id='" + quickfinderId + "']"), exampleDiv);

        assertIsVisible("#" + quickfinderId);

        waitAndClickById(quickfinderId);

        Thread.sleep(2000);

        gotoLightBox();

        WebElement travelAccountNumberField = driver.findElement(By.cssSelector(
                "div[data-label='Travel Account Number']"));

        String travelAccountNumberFieldId = travelAccountNumberField.getAttribute("id");
        String travelAccountNumberControlId = travelAccountNumberFieldId + UifConstants.IdSuffixes.CONTROL;

        findElement(By.cssSelector("#" + travelAccountNumberControlId), travelAccountNumberField).sendKeys("a1");
        waitAndClickSearch3();
        waitAndClickReturnValue();
//        waitAndClickByLinkText("Quickfinder"); // work around for Quickfinder not loaded on return
        assertElementPresentByXpath("//input[@name='inputField7' and @value='a1']");
    }

    protected void testInputFieldInquiry() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example9");
        WebElement field = findElement(By.cssSelector("div[data-label='Inquiry Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String inquiryId = field.findElement(By.cssSelector(".uif-action")).getAttribute("id");

        // validate that the inquiry comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] + div > button[id='" + inquiryId + "']"), exampleDiv);

        assertIsVisible("#" + inquiryId);

        waitAndClickById(inquiryId);

        Thread.sleep(2000);

        gotoLightBox();
        assertTextPresent("Travel Account");
        gotoLightBoxIframe();

        waitAndClickButtonByText("Close");
        selectTopFrame();
    }

    protected void testInputFieldRequired() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example11");
        WebElement field = findElement(By.cssSelector("div[data-label='Required Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String errorId = fieldId + UifConstants.IdSuffixes.ERRORS;

        WebElement requiredAsterisk = findElement(By.cssSelector("span.uif-requiredMessage"), field);
        if (!requiredAsterisk.getText().contains("*")) {
            fail("Label asterisk for required field does not appear");
        }

        assertIsVisible("#" + controlId);

        waitAndClick(By.cssSelector("#" + controlId));
        Thread.sleep(3000);
        waitAndClick(By.cssSelector("#" + fieldId));
        typeTab();
//        fireMouseOverEventByName("inputField11");
        if (!field.getAttribute("class").contains("uif-hasError")) {
            fail("Control does not show error class");
        }
        assertElementPresent("#" + errorId + " img[src$='/krad/images/validation/error.png']");
    }

    protected void testInputFieldUppercase() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example12");
        WebElement field = findElement(By.cssSelector("div[data-label='Uppercase field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + controlId);

        waitAndType(By.cssSelector("#" + controlId), "Test InputField");

        assertTextNotPresent("TEST INPUTFIELD", "Control text did not appear as uppercase");
    }

    protected void testInputFieldWidgetInputOnlyWithQuickFinder() throws Exception {
        //There is nothing under this section on which tests can be applied.
    	waitAndClickByLinkText("widgetInputOnly with Quickfinder");
    }
    
    protected void testInputFieldWidgetInputOnlyWithInquiry() throws Exception {
    	waitAndClickByLinkText("widgetInputOnly with Inquiry");
    	waitAndClickByXpath("//div[@data-parent='Demo-InputField-Example10']/span/input[@type='image' and @title='Direct Inquiry']");
        gotoLightBoxIframe();
    	waitForTextPresent("a2");
    	waitForTextPresent("Travel Account 2");
    	waitForTextPresent("EAT - Expense");
    	waitForTextPresent("fran");
    	waitAndClickButtonByText("Close");
    	selectTopFrame();
    }
    
    protected void testInputFieldDisableNativeAutocomplete() throws Exception {
    	waitAndClickByLinkText("Disable Native Autocomplete");
    	waitForElementPresentByXpath("//input[@name='inputField13' and @autocomplete='off']");
    }
    
    protected void testInputFieldInputAddons() throws Exception {
    	waitAndClickByLinkText("Input Addons");
    	waitForTextPresent(".00 ");
    	waitForElementPresentByXpath("//div[@data-parent='Demo-InputField-Example14']/div/span/a");
    	waitForElementPresentByXpath("//div[@data-parent='Demo-InputField-Example14']/div/span/a[@class='uif-actionLink icon-facebook3']");
    	waitForElementPresentByXpath("//div[@data-parent='Demo-InputField-Example14']/div/span/a[@class='uif-actionLink icon-twitter3']");
    	waitForElementPresentByXpath("//div[@data-parent='Demo-InputField-Example14']/div/span/a[@class='uif-actionLink icon-youtube']");
    }
    
    protected void testInputFieldExamples() throws Exception {
        testInputFieldDefault();
        testInputFieldAltControl();
        testInputFieldInstructionalText();
        testInputFieldConstraintText();
//        testInputFieldLabelTop(); // removed from example
//        testInputFieldLabelRight(); // removed from example
        testInputFieldWidgetInputOnlyWithQuickFinder();
        testInputFieldUppercase();
        testInputFieldDisableNativeAutocomplete();
        testInputFieldInputAddons();
        testInputFieldRequired();
        testInputFieldInquiry();
        testInputFieldQuickfinder();
    }

    @Test
    public void testInputFieldExamplesBookmark() throws Exception {
        testInputFieldExamples();
        passed();
    }

    @Test
    public void testInputFieldExamplesNav() throws Exception {
        testInputFieldExamples();
        passed();
    }
}
