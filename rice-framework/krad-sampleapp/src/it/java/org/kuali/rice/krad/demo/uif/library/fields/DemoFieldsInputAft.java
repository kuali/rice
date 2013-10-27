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
     * /kr-krad/kradsampleapp?viewId=Demo-InputField-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-InputField-View&methodToCall=start";

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
        findElement(By.cssSelector("span[data-label_for='" + fieldId + "'] + input[id='" + controlId + "']"),
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
        findElement(By.cssSelector("span[data-label_for='" + fieldId + "'] + textarea[id='" + controlId + "']"),
                exampleDiv);
    }

    protected void testInputFieldInstructionalText() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example3");
        WebElement field = findElement(By.cssSelector("div[data-label='InputField 3']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String instructionalTextId = fieldId + UifConstants.IdSuffixes.INSTRUCTIONAL + UifConstants.IdSuffixes.SPAN;
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + instructionalTextId);
        assertTextPresent("Instructions for this field", "#" + instructionalTextId, "InputField value not correct");

        // validate that the instructional text comes after the label
        findElement(By.cssSelector("span[data-label_for='" + fieldId + "'] + span[id='" + instructionalTextId + "']"),
                exampleDiv);

        // validate that the value comes after the instructional text
        findElement(By.cssSelector("span[id='" + instructionalTextId + "'] + input[id='" + controlId + "']"),
                exampleDiv);
    }

    protected void testInputFieldConstraintText() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example4");
        WebElement field = findElement(By.cssSelector("div[data-label='InputField 4']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String constraintTextId = fieldId + UifConstants.IdSuffixes.CONSTRAINT + UifConstants.IdSuffixes.SPAN;

        assertIsVisible("#" + constraintTextId);
        assertTextPresent("Text to tell users about constraints this field may have", "#" + constraintTextId,
                "InputField value not correct");

        // validate that the value comes after the label
        findElement(By.cssSelector("span[data-label_for='" + fieldId + "'] + input[id='" + controlId + "']"),
                exampleDiv);

        // validate that the constraint text comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] ~ span[id='" + constraintTextId + "']"), exampleDiv);
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

        WebElement labelSpan = findElement(By.cssSelector("span[data-label_for='" + fieldId + "']"), field);
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
        findElement(By.cssSelector("input[id='" + controlId + "'] + span[data-label_for='" + fieldId + "']"),
                exampleDiv);
    }

    protected void testInputFieldQuickfinder() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example7");
        WebElement field = findElement(By.cssSelector("div[data-label='Quickfinder Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String quickfinderId = findElement(By.cssSelector(".uif-actionImage"), field).getAttribute("id");

        // validate that the quickfinder comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] + input[id='" + quickfinderId + "']"), exampleDiv);

        assertIsVisible("#" + quickfinderId);

        waitAndClickById(quickfinderId);

        Thread.sleep(2000);

        driver.switchTo().frame(driver.findElement(By.cssSelector(".fancybox-iframe")));

        WebElement travelAccountNumberField = driver.findElement(By.cssSelector(
                "div[data-label='Travel Account Number']"));

        String travelAccountNumberFieldId = travelAccountNumberField.getAttribute("id");
        String travelAccountNumberControlId = travelAccountNumberFieldId + UifConstants.IdSuffixes.CONTROL;

        findElement(By.cssSelector("#" + travelAccountNumberControlId), travelAccountNumberField).sendKeys("a1");
        waitAndClickSearch3();
        waitAndClickReturnValue();

        selectTopFrame();

        assertIsVisible("#" + controlId);
        assertTextPresent("a1", "Control text did not appear");
    }

    protected void testInputFieldInquiry() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-InputField-Example9");
        WebElement field = findElement(By.cssSelector("div[data-label='Inquiry Field']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;
        String inquiryId = field.findElement(By.cssSelector(".uif-actionImage")).getAttribute("id");

        // validate that the inquiry comes after the value
        findElement(By.cssSelector("input[id='" + controlId + "'] + input[id='" + inquiryId + "']"), exampleDiv);

        assertIsVisible("#" + inquiryId);

        waitAndClickById(inquiryId);

        Thread.sleep(2000);

        driver.switchTo().frame(driver.findElement(By.cssSelector(".fancybox-iframe")));
        checkForIncidentReport("Travel Account Inquiry");
        assertTextPresent("Travel Account");
        selectTopFrame();
        gotoIframeByXpath("//iframe[@class='fancybox-iframe']");

        waitAndClickButtonByText("Close");
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
        waitAndClick(By.cssSelector("#" + fieldId));
        fireMouseOverEventByName("inputField10");
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

    protected void testInputFieldExamples() throws Exception {
        testInputFieldDefault();
        testInputFieldAltControl();
        testInputFieldInstructionalText();
        testInputFieldConstraintText();
        testInputFieldLabelTop();
        testInputFieldLabelRight();
        testInputFieldQuickfinder();
        testInputFieldInquiry();
        testInputFieldRequired();
        testInputFieldUppercase();
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

    @Test
    public void testInputFieldDefaultBookmark() throws Exception {
        testInputFieldDefault();
        passed();
    }

    @Test
    public void testInputFieldDefaultNav() throws Exception {
        testInputFieldDefault();
        passed();
    }

    @Test
    public void testInputFieldAltControlBookmark() throws Exception {
        testInputFieldAltControl();
        passed();
    }

    @Test
    public void testInputFieldAltControlNav() throws Exception {
        testInputFieldAltControl();
        passed();
    }

    @Test
    public void testInputFieldInstructionalTextBookmark() throws Exception {
        testInputFieldInstructionalText();
        passed();
    }

    @Test
    public void testInputFieldInstructionalTextNav() throws Exception {
        testInputFieldInstructionalText();
        passed();
    }

    @Test
    public void testInputFieldConstraintTextBookmark() throws Exception {
        testInputFieldConstraintText();
        passed();
    }

    @Test
    public void testInputFieldConstraintTextNav() throws Exception {
        testInputFieldConstraintText();
        passed();
    }

    @Test
    public void testInputFieldLabelTopBookmark() throws Exception {
        testInputFieldLabelTop();
        passed();
    }

    @Test
    public void testInputFieldLabelTopNav() throws Exception {
        testInputFieldLabelTop();
        passed();
    }

    @Test
    public void testInputFieldLabelRightBookmark() throws Exception {
        testInputFieldLabelRight();
        passed();
    }

    @Test
    public void testInputFieldLabelRightNav() throws Exception {
        testInputFieldLabelRight();
        passed();
    }

    @Test
    public void testInputFieldQuickfinderBookmark() throws Exception {
        testInputFieldQuickfinder();
        passed();
    }

    @Test
    public void testInputFieldQuickfinderNav() throws Exception {
        testInputFieldQuickfinder();
        passed();
    }

    @Test
    public void testInputFieldInquiryBookmark() throws Exception {
        testInputFieldInquiry();
        passed();
    }

    @Test
    public void testInputFieldInquiryNav() throws Exception {
        testInputFieldInquiry();
        passed();
    }

    @Test
    public void testInputFieldRequiredBookmark() throws Exception {
        testInputFieldRequired();
        passed();
    }

    @Test
    public void testInputFieldRequiredNav() throws Exception {
        testInputFieldRequired();
        passed();
    }

    @Test
    public void testInputFieldUppercaseBookmark() throws Exception {
        testInputFieldUppercase();
        passed();
    }

    @Test
    public void testInputFieldUppercaseNav() throws Exception {
        testInputFieldUppercase();
        passed();
    }
}
