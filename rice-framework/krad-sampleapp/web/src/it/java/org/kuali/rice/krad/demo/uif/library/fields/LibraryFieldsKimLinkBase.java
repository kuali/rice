/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.LibraryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LibraryFieldsKimLinkBase extends LibraryBase {
    public static final String INQUIRY = "inquiry?";
    public static final String HREF_ATTRIBUTE = "href";
    public static final String ID_ATTRIBUTE = "id";
    public static final String SEARCH_ICON_CLASSNAME = "icon-search";
    public static final String UIF_LINK_CLASSNAME = "uif-link";
    public static final String UIF_LINK_CSS_SELECTOR = ".uif-link";

    protected void waitAndClickQuickFinderButton(String message) throws InterruptedException {
        jGrowl("Click Quickfinder button.");
        waitAndClickByXpath("//button[contains(@class,'" + SEARCH_ICON_CLASSNAME + "')]", message);
    }

    protected WebElement waitForExampleElementById(String sectionId) throws Exception {
        waitForElementPresentById(sectionId);
        waitForElementVisibleById(sectionId, "");
        return findElement(By.id(sectionId));
    }

    protected void verifyLinkIsInquiry(WebElement field) {
        jGrowl("verifyLinkIsInquiry");
        String href =  field.getAttribute(HREF_ATTRIBUTE);
        if ( !StringUtils.contains(href, INQUIRY)) {
            fail("Inquiry not found in link");
        }
    }

    protected void verifyKeyInInquiryHref(WebElement field, String keyParameter) {
        jGrowl("verifyKeyInInquiryHref");
        String href =  field.getAttribute(HREF_ATTRIBUTE);
        if ( !StringUtils.contains(href,keyParameter)) {
            fail("Inquiry key " + keyParameter + " not found in href");
        }
    }

    protected void verifyLinkText(WebElement field, String linkText) {
        jGrowl("verifyLinkText");
        String fieldText = field.getText();
        if ( !StringUtils.contains(fieldText,linkText)) {
            fail("Expected linkText not found. Expected " + linkText + " but found " + fieldText  );
        }
    }

    protected void verifyLinkIcon(WebElement field, String iconClassName) {
        jGrowl("verifyLinkIcon");
        String classNames = field.getAttribute("class");
        if ( !StringUtils.contains(classNames,iconClassName)) {
            fail("Expected icon class name not found. Expected "+ iconClassName + " but found " + classNames);
        }
    }

    protected void verifyLink(WebElement field, String onPageString)throws Exception {
        jGrowl("verifyLink");
        waitAndClickByLinkText(field.getText());

        gotoLightBox();
        assertTextPresent(onPageString);
        waitAndClickButtonByText("Close", "Unable to find Close button");
        selectTopFrame();
    }

    protected void verifyLinkDataItem(WebElement field, String inputFieldName, String inputFieldValue)throws Exception {
        jGrowl("verifyLinkDataItem");
        waitAndClickById(field.getAttribute(ID_ATTRIBUTE));
        gotoLightBox();
        WebElement inquiryPage = findElement(By.xpath(("//main[contains(@class,'uif-inquiryPage')]")));
        WebElement inputField = findElement(By.xpath("//td/div[@data-label='"+inputFieldName+"']/span"),inquiryPage);
        String fieldText = inputField.getText();
        if ( !StringUtils.contains(fieldText,inputFieldValue)) {
            fail("Expected value not found on field. Expected " + inputFieldValue + " but found " + fieldText);
        }

        waitAndClickButtonByText("Close", "Unable to find Close button");
        selectTopFrame();
    }
}
