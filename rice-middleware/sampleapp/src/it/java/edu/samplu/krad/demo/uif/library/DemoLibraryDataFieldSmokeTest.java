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
package edu.samplu.krad.demo.uif.library;

import edu.samplu.common.Failable;
import org.junit.Test;
import org.kuali.rice.krad.uif.UifConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryDataFieldSmokeTest extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DataField-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DataField-View&methodToCall=start";

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
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");

        WebElement label = field.findElement(By.cssSelector("label[for='" + controlId + "']"));
        if(!label.getText().contains("DataField 1:")){
            fail("Label text does not match");
        }

        assertIsVisible("#" + controlId);

        assertTextPresent("1001", "#" + controlId, "DataField value not correct");

        WebElement afterFieldElement = field.findElement(By.cssSelector("span[data-label_for='" + fieldId + "'] + span"));
        if (!(afterFieldElement.getText().contains("1001"))) {
            fail("Ordering of DataField (label, value) is incorrect");
        }
    }

    protected void testDataFieldLabelTop() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example2");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");

        WebElement label = field.findElement(By.cssSelector("label[for='" + controlId + "']"));
        if(!label.getText().contains("DataField 1:")){
            fail("Label text does not match");
        }

        WebElement labelspan = field.findElement(By.cssSelector("span[data-label_for='" + fieldId + "']"));
        if(!labelspan.getAttribute("class").contains("uif-labelBlock")){
            fail("Label span does not contain the appropriate class expected");
        }

        assertIsVisible("#" + controlId);
    }

    protected void testDataFieldRight() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example3");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[for='" + controlId + "']");

        WebElement label = field.findElement(By.cssSelector("label[for='" + controlId + "']"));
        if(!label.getText().contains("DataField 1")){
            fail("Label text does not match");
        }

        assertTextPresent("1001", "#" + controlId, "DataField value not correct");

        assertIsVisible("#" + controlId);

        WebElement afterControlElementLabel = field.findElement(By.cssSelector("span#" + controlId + " + span > label"));
        if (!(afterControlElementLabel.getText().contains("DataField 1"))) {
            fail("Ordering of DataField (value, label) is incorrect");
        }
    }

    protected void testDataFieldDefaultValue() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example4");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 2']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertTextPresent("2012", "#" + controlId, "DataField default value not correct");
    }

    protected void testDataFieldAppendProperty() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example5");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertTextPresent("1001 *-* ID Val", "#" + controlId, "DataField appended property not correct");
    }

    protected void testDataFieldReplaceProperty() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example6");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertTextNotPresent("1001 *-*", "DataField property not replaced correctly");
        assertTextPresent("ID Val", "#" + controlId, "DataField replaced property not correct");
    }

    protected void testDataFieldReplacePropertyWithField() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example7");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertTextNotPresent("1001 *-*", "DataField property not replaced correctly");
        assertTextPresent("My Book Title", "#" + controlId, "DataField replaced property not correct");
    }

    protected void testDataFieldAppendPropertyWithField() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-DataField-Example8");
        WebElement field = exampleDiv.findElement(By.cssSelector("div[data-label='DataField 1']"));

        String fieldId = field.getAttribute("id");
        String controlId = fieldId + UifConstants.IdSuffixes.CONTROL;

        assertTextPresent("1001 *-* My Book Title", "#" + controlId, "DataField appended property not correct");
    }

    protected void testDataFieldExamples() throws Exception{
        testDataFieldDefault();
        testDataFieldLabelTop();
        testDataFieldRight();
        testDataFieldDefaultValue();
        testDataFieldAppendProperty();
        testDataFieldReplaceProperty();
        testDataFieldReplacePropertyWithField();
        testDataFieldAppendPropertyWithField();
    }

    public void testDataFieldNav(Failable failable) throws Exception{
        testDataFieldExamples();
        passed();
    }

    public void testDataFieldBookmark(Failable failable) throws Exception{
        testDataFieldExamples();
        passed();
    }

    @Test
    public void testDataFieldBookmark() throws Exception {
        testDataFieldBookmark(this);
    }

    @Test
    public void testDataFieldNav() throws Exception {
        testDataFieldNav(this);
    }
}
