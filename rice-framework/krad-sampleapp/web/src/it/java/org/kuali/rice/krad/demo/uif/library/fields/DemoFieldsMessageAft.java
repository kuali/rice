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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoFieldsMessageAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-MessageFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-MessageFieldView&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Message Field");
    }

    protected void testMessageFieldDefault() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-MessageField-Example1");
        WebElement field = findElement(By.cssSelector(".uif-message"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertTextPresent("Message Field Text", "#" + fieldId, "MessageField value not correct");
    }

    protected void testMessageFieldExpressionText() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-MessageField-Example2");
        WebElement field = findElement(By.cssSelector(".uif-message"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertTextPresent("Message Field with expression text: 'fruits'", "#" + fieldId,
                "MessageField expression text value not correct");
    }

    protected void testMessageFieldExamples() throws Exception {
        testMessageFieldDefault();
        testMessageFieldExpressionText();
    }

    @Test
    public void testMessageFieldExamplesBookmark() throws Exception {
        testMessageFieldExamples();
        passed();
    }

    @Test
    public void testMessageFieldExamplesNav() throws Exception {
        testMessageFieldExamples();
        passed();
    }

    @Test
    public void testMessageFieldDefaultBookmark() throws Exception {
        testMessageFieldDefault();
        passed();
    }

    @Test
    public void testMessageFieldDefaultNav() throws Exception {
        testMessageFieldDefault();
        passed();
    }

    @Test
    public void testMessageFieldExpressionTextBookmark() throws Exception {
        testMessageFieldExpressionText();
        passed();
    }

    @Test
    public void testMessageFieldExpressionTextNav() throws Exception {
        testMessageFieldExpressionText();
        passed();
    }
}
