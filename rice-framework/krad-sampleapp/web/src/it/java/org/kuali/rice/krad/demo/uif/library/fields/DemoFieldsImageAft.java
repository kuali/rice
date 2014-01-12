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
public class DemoFieldsImageAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ImageFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ImageFieldView&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Image Field");
    }

    protected void testImageFieldDefault() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ImageField-Example1");
        WebElement field = findElement(By.cssSelector("div[data-label='ImageField 1']"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        assertIsVisible("label[data-label_for='" + fieldId + "']");
        WebElement label = findElement(By.cssSelector("label[data-label_for='" + fieldId + "']"), field);
        if (!label.getText().contains("ImageField 1:")) {
            fail("Label text does not match");
        }

        String imgId = label.getAttribute("for");

        assertIsVisible("#" + imgId + "[src='/krad/images/pdf.png']");
        assertIsVisible("#" + imgId + "[alt='']");

        // validate that the image comes after the label
        findElement(By.cssSelector("label[data-label_for='" + fieldId + "'] + img[src='/krad/images/pdf.png']"),
                exampleDiv);
    }

    protected void testImageFieldAlternateText() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ImageField-Example2");
        WebElement field = findElement(By.cssSelector("div[data-label='ImageField 1']"), exampleDiv);

        String fieldId = field.getAttribute("id");

        WebElement label = findElement(By.cssSelector("label[data-label_for='" + fieldId + "']"), field);

        String imgId = label.getAttribute("for");

        assertIsVisible("#" + imgId + "[src='/krad/images/pdf_ne.png']");
        assertIsVisible("#" + imgId + "[alt='pdf']");
    }

    protected void testImageFieldExamples() throws Exception {
        testImageFieldDefault();
        testImageFieldAlternateText();
    }

    @Test
    public void testImageFieldExamplesBookmark() throws Exception {
        testImageFieldExamples();
        passed();
    }

    @Test
    public void testImageFieldExamplesNav() throws Exception {
        testImageFieldExamples();
        passed();
    }

    @Test
    public void testImageFieldDefaultBookmark() throws Exception {
        testImageFieldDefault();
        passed();
    }

    @Test
    public void testImageFieldDefaultNav() throws Exception {
        testImageFieldDefault();
        passed();
    }

    @Test
    public void testImageFieldAlternateTextBookmark() throws Exception {
        testImageFieldAlternateText();
        passed();
    }

    @Test
    public void testImageFieldAlternateTextNav() throws Exception {
        testImageFieldAlternateText();
        passed();
    }
}
