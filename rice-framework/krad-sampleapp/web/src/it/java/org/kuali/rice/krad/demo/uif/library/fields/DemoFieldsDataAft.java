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

import org.junit.Ignore;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.kuali.rice.testtools.common.JiraAwareFailureUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kuali.rice.krad.uif.UifConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoFieldsDataAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DataFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DataFieldView&methodToCall=start";
    public static final String DIV_DATA_LABEL_DATA_FIELD_1 = "div[data-label='DataField 1']";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Data Field");
    }

    protected void testDataFieldDefault() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example1");
        WebElement field = findElement(By.cssSelector(DIV_DATA_LABEL_DATA_FIELD_1), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("label[for='" + controlId + "']"), field);
        if (!label.getText().contains("DataField 1:")) {
            jiraAwareFail("Label text does not match");
        }

        assertTextPresent("1001", "#" + fieldId, "DataField value not correct");

        // validate that the value comes after the label
        findElement(By.xpath("//div/label[@data-label_for='" + fieldId + "']"));
    }

    protected void testDataFieldLabelTop() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example2");
        WebElement field = findElement(By.cssSelector(DIV_DATA_LABEL_DATA_FIELD_1), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("label[for='" + controlId + "']"), field);
        if (!label.getText().contains("DataField 1:")) {
            jiraAwareFail("Label text does not match");
        }
    }

    protected void testDataFieldLabelRight() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example3");
        WebElement field = findElement(By.cssSelector(DIV_DATA_LABEL_DATA_FIELD_1), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertTextPresent("1001", "#" + fieldId, "DataField value not correct");

        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("[for='" + controlId + "']"), field);
        if (!label.getText().contains("DataField 1")) {
            jiraAwareFail("Label text does not match");
        }

        // validate that the label comes after the value
        findElement(By.xpath("//div/label[@data-label_for='" + fieldId + "']"));
    }

    protected void testDataFieldDefaultValue() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example4", "DataField 2");
        if (!valueText.contains("2012")) {
            jiraAwareFail("Fields Data Field Default Value 2012 not displayed");
        }
    }
    
    protected void testDataFieldDefaultValueFinderClass() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example12", "DataField 2");
        if (!valueText.contains("Value returned from org.kuali.rice.krad.demo.uif.library.DemoValuesFinder")) {
            jiraAwareFail("Fields Default Value Finder Field not displayed");
        }
    }

    protected void testDataFieldAppendProperty() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example5", "DataField 1");
        assertTrue(valueText.endsWith("ID Val"));
    }

    protected void testDataFieldReplaceProperty() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example6", "DataField 1");
        assertTrue(valueText.contains("ID Val"));
    }

    protected void testDataFieldReplacePropertyWithField() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example7", "DataField 1");
        assertTrue(valueText.contains("My Book Title"));
    }

    protected void testDataFieldAppendPropertyWithField() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example8", "DataField 1");
        assertTrue(valueText.contains("1001 *-* My Book Title"));
    }

    protected void testDataFieldApplyFullMask() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example9", "DataField 1");
        assertTrue("1001 not masked to *********", valueText.contains("*********"));
    }

    protected void testDataFieldApplyPartialMask() throws Exception {
        String valueText = textValueUnderTest("Demo-DataField-Example10", "DataField 1");
        assertTrue("1001 not masked to **01", valueText.contains("**01"));
    }

    protected void testDataFieldHideProperty() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example11");

        if (findElements(By.cssSelector(DIV_DATA_LABEL_DATA_FIELD_1), exampleDiv).size() > 0) {
            jiraAwareFail(DIV_DATA_LABEL_DATA_FIELD_1 + " not hidden");
        }
    }

    private String textValueUnderTest(String example, String testLabel) throws Exception {
        WebElement exampleDiv = navigateToExample(example);
        WebElement field = findElement(By.cssSelector("div[data-label='" + testLabel + "']"), exampleDiv);

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");
        WebElement label = findElement(By.cssSelector("[for='" + controlId + "']"), field);
        if (!label.getText().contains(testLabel)) {
            jiraAwareFail("Label text does not match");
        }

        return findElement(By.id(fieldId)).getText();
    }

    protected void testDataFieldExamples() throws Exception {
        testDataFieldDefault();
//        testDataFieldLabelTop(); // removed form demo
//        testDataFieldLabelRight(); // removed from demo
        testDataFieldDefaultValue();
        testDataFieldDefaultValueFinderClass();
        testDataFieldAppendProperty();
        testDataFieldReplaceProperty();
        testDataFieldReplacePropertyWithField();
        testDataFieldAppendPropertyWithField();
        testDataFieldApplyFullMask();
        testDataFieldApplyPartialMask();
        testDataFieldHideProperty();
    }

    @Test
    @Ignore // just run the individual tests
    public void testDataFieldExamplesBookmark() throws Exception {
        testDataFieldExamples();
        passed();
    }

    @Test
    @Ignore // just run the individual tests
    public void testDataFieldExamplesNav() throws Exception {
        testDataFieldExamples();
        passed();
    }

    @Test
    public void testDataFieldDefaultBookmark() throws Exception {
        testDataFieldDefault();
        passed();
    }

    @Test
    public void testDataFieldDefaultNav() throws Exception {
        testDataFieldDefault();
        passed();
    }

    @Test
    public void testDataFieldDefaultValueBookmark() throws Exception {
        testDataFieldDefaultValue();
        passed();
    }

    @Test
    public void testDataFieldDefaultValueNav() throws Exception {
        testDataFieldDefaultValue();
        passed();
    }
    
    @Test
    public void testDataFieldDefaultValueFinderClassBookmark() throws Exception {
        testDataFieldDefaultValueFinderClass();
        passed();
    }

    @Test
    public void testDataFieldDefaultValueFinderClassNav() throws Exception {
        testDataFieldDefaultValueFinderClass();
        passed();
    }

    @Test
    public void testDataFieldAppendPropertyBookmark() throws Exception {
        testDataFieldAppendProperty();
        passed();
    }

    @Test
    public void testDataFieldAppendPropertyNav() throws Exception {
        testDataFieldAppendProperty();
        passed();
    }

    @Test
    public void testDataFieldReplacePropertyBookmark() throws Exception {
        testDataFieldReplaceProperty();
        passed();
    }

    @Test
    public void testDataFieldReplacePropertyNav() throws Exception {
        testDataFieldReplaceProperty();
        passed();
    }

    @Test
    public void testDataFieldReplacePropertyWithFieldBookmark() throws Exception {
        testDataFieldReplacePropertyWithField();
        passed();
    }

    @Test
    public void testDataFieldReplacePropertyWithFieldNav() throws Exception {
        testDataFieldReplacePropertyWithField();
        passed();
    }

    @Test
    public void testDataFieldAppendPropertyWithFieldBookmark() throws Exception {
        testDataFieldAppendPropertyWithField();
        passed();
    }

    @Test
    public void testDataFieldAppendPropertyWithFieldNav() throws Exception {
        testDataFieldAppendPropertyWithField();
        passed();
    }

    @Test
    public void testDataFieldApplyFullMaskBookmark() throws Exception {
        testDataFieldApplyFullMask();
        passed();
    }

    @Test
    public void testDataFieldApplyFullMaskNav() throws Exception {
        testDataFieldApplyFullMask();
        passed();
    }

    @Test
    public void testDataFieldApplyPartialMaskBookmark() throws Exception {
        testDataFieldApplyPartialMask();
        passed();
    }

    @Test
    public void testDataFieldApplyPartialMaskNav() throws Exception {
        testDataFieldApplyPartialMask();
        passed();
    }

    @Test
    public void testDataFieldHidePropertyBookmark() throws Exception {
        testDataFieldHideProperty();
        passed();
    }

    @Test
    public void testDataFieldHidePropertyNav() throws Exception {
        testDataFieldHideProperty();
        passed();
    }
}
