/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryFieldsUserLinkAft extends LibraryFieldsKimLinkBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-UserLinkFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-UserLinkFieldView&methodToCall=start";

    public static final String PRINCIPAL_ID1 = "eric";
    public static final String PRINCIPAL_ID2 = "erin";
    public static final String PRINCIPAL_ID3 = "test1";
    public static final String PRINCIPAL_ID4 = "edna";
    public static final String PRINCIPAL_NAME1 = "eric";
    public static final String PRINCIPAL_NAME2 = "erin";
    public static final String PRINCIPAL_NAME3 = "test1";
    public static final String PRINCIPAL_NAME4 = "edna";
    public static final String PRINCIPAL_LINKTEXT1 = "Employee, Eric";
    public static final String PRINCIPAL_LINKTEXT2 = "Employee, Erin";
    public static final String PRINCIPAL_LINKTEXT3 = "One, Tester";
    public static final String PRINCIPAL_LINKTEXT4 = "Employee, Edna";
    public static final String PRINCIPAL_INQUIRY_KEY_ID1 = "principalId=eric";
    public static final String PRINCIPAL_INQUIRY_KEY_ID2 = "principalId=erin";
    public static final String PRINCIPAL_INQUIRY_KEY_ID3 = "principalId=test1";
    public static final String PRINCIPAL_INQUIRY_KEY_ID4 = "principalId=edna";
    public static final String PRINCIPAL_INQUIRY_KEY_NAME1 = "principalName=eric";
    public static final String PRINCIPAL_INQUIRY_KEY_NAME2 = "principalName=erin";
    public static final String PRINCIPAL_INQUIRY_KEY_NAME3 = "principalName=test1";
    public static final String PRINCIPAL_INQUIRY_KEY_NAME4 = "principalName=edna";

    public static final String USER_ICON_CLASSNAME = "icon-user3";
    public static final String TEXT_PERSON_INQUIRY = "Person Inquiry";
    public static final String LABEL_USER_NAME = "User Name";
    public static final String LABEL_NAME = "Name";
    public static final String LABEL_USER_LINK = "User Link";
    public static final String LABEL_DISABLED_USER_LINK = "Disabled User Link";
    public static final String LABEL_PRINCIPAL_ID = "Principal Id:";
    public static final String DATA_LABEL_PRINCIPAL_ID = "Principal Id";

    public static final String DEMO_PAGE_ID1 = "Demo-UserLinkField-Example1";
    public static final String DEMO_PAGE_ID2 = "Demo-UserLinkField-Example2";
    public static final String DEMO_PAGE_ID3 = "Demo-UserLinkField-Example3";
    public static final String DEMO_PAGE_ID4 = "Demo-UserLinkField-Example4";
    public static final String DEMO_PAGE_ID5 = "Demo-UserLinkField-Example5";
    public static final String DEMO_PAGE_ID6 = "Demo-UserLinkField-Example6";
    public static final String DEMO_PAGE_ID7 = "Demo-UserLinkField-Example7";
    public static final String DEMO_PAGE_HEADER1 = "Default";
    public static final String DEMO_PAGE_HEADER2 = "User Link Field with Label";
    public static final String DEMO_PAGE_HEADER3 = "User Link Field with Link Disabled";
    public static final String DEMO_PAGE_HEADER4 = "User Link Field with Link Disabled";
    public static final String DEMO_PAGE_HEADER5 = "User Link refreshed by Field changes";
    public static final String DEMO_PAGE_HEADER6 = "User Link as Input Field Addon";
    public static final String DEMO_PAGE_HEADER7 = "User Link as Icon";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "User Link Field");
    }

    @Override
    public WebElement navigateToExample(String exampleId) throws Exception {
        //navigate to base page ensures starting point for each navigateToExample
        navigateToLibraryDemo("Fields", "User Link Field");
        return super.navigateToExample(exampleId);
    }

    protected void testUserLinkFieldDefault() throws Exception {
        jGrowl("-- testUserLinkFieldDefault");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID1);
        assertTextPresent(DEMO_PAGE_HEADER1);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        List<WebElement> links = exampleDiv.findElements(By.className(UIF_LINK_CLASSNAME));
        assertEquals("Number of expected user field links not found on page.", 2, links.size());

        for ( WebElement link : links) {
            String href =  link.getAttribute(HREF_ATTRIBUTE);
            if ( !StringUtils.contains(href, INQUIRY)) {
                fail("Inquiry not found in link.");
            }

            if ( !(StringUtils.contains(href,PRINCIPAL_INQUIRY_KEY_ID1) ||
                    StringUtils.contains(href,PRINCIPAL_INQUIRY_KEY_NAME2))) {
                fail("User inquiry keys not found in href.");
            }

            String linkText = link.getText();
            if ( !(StringUtils.contains(linkText,PRINCIPAL_LINKTEXT1) ||
                    StringUtils.contains(linkText,PRINCIPAL_LINKTEXT2))) {
                fail("User names in linkText not found");
            }
        }

        verifyLink(field, TEXT_PERSON_INQUIRY);
    }

    protected void testUserLinkFieldLabel() throws Exception {
        jGrowl("-- testUserLinkFieldLabel");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID2);
        assertTextPresent(DEMO_PAGE_HEADER2);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        assertLabelWithTextPresent(LABEL_USER_NAME);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, PRINCIPAL_INQUIRY_KEY_ID1);
        verifyLinkText(field, PRINCIPAL_LINKTEXT1);

        verifyLink(field, TEXT_PERSON_INQUIRY);
    }

    protected void testUserLinkFieldLinkDisabled() throws Exception {
        jGrowl("-- testUserLinkFieldLinkDisabled");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID3);
        assertTextPresent(DEMO_PAGE_HEADER3);

        assertLabelWithTextPresent(LABEL_USER_NAME);
        WebElement disabledLink = findElement(By.xpath("//span[contains( text(),'" + PRINCIPAL_LINKTEXT1 + "')]"), exampleDiv);
        assertEquals("Disabled Link text not found", PRINCIPAL_LINKTEXT1, disabledLink.getText());
    }

    protected void testUserLinkFieldNoLightbox() throws Exception {
        jGrowl("-- testUserLinkFieldNoLightbox");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID4);
        assertTextPresent(DEMO_PAGE_HEADER4);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);

        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field,PRINCIPAL_INQUIRY_KEY_ID1);
        verifyLinkText(field, PRINCIPAL_LINKTEXT1);

        String dataOnClickScript = field.getAttribute("data-onclick");
        if ( StringUtils.contains(dataOnClickScript,"openLinkInDialog(jQuery(this), \"\");")) {
            fail("Lightbox not suppressed for Inquiry.");
        }

        waitAndClickByLinkText(field.getText());
        waitAndClickButtonByText("Back", "Unable to find Back button");
    }

    protected void testUserLinkFieldRefresh() throws Exception {
        jGrowl("-- testUserLinkFieldRefresh");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID5);
        assertTextPresent(DEMO_PAGE_HEADER5);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        assertLabelWithTextPresent(LABEL_PRINCIPAL_ID);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field,PRINCIPAL_INQUIRY_KEY_ID2);
        verifyLinkText(field, PRINCIPAL_LINKTEXT2);
        verifyLinkDataItem(field, LABEL_NAME, PRINCIPAL_LINKTEXT2);

        waitForExampleElementById(DEMO_PAGE_ID5);
        waitAndClickQuickFinderButton("QuickFinder not found.");
        gotoLightBox();
        waitAndTypeByName("lookupCriteria[principalName]", PRINCIPAL_ID1);
        waitAndClickButtonByExactText("Search");
        waitAndClickByLinkText("return value");

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID5);
        field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkText(field, PRINCIPAL_LINKTEXT1);
        verifyKeyInInquiryHref(field,PRINCIPAL_INQUIRY_KEY_ID1);
        verifyLinkDataItem(field, LABEL_NAME, PRINCIPAL_LINKTEXT1);
        waitForExampleElementById(DEMO_PAGE_ID5);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testUserLinkFieldRefreshAddon() throws Exception {
        jGrowl("-- testUserLinkFieldAddon");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID6);
        assertTextPresent(DEMO_PAGE_HEADER6);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        assertLabelWithTextPresent(LABEL_PRINCIPAL_ID);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, PRINCIPAL_INQUIRY_KEY_ID3);
        verifyLinkText(field, PRINCIPAL_LINKTEXT3);
        //String fieldId = field.getAttribute("id");

        clearTextByName("testPrincipalId3");
        waitAndTypeByName("testPrincipalId3", PRINCIPAL_ID4);
        typeTab();
        Thread.sleep(2000);
        waitAndClickByName("testPrincipalId3");

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID6);
        try {
            field = findElement(By.linkText(PRINCIPAL_LINKTEXT4), exampleDiv);
        } catch (Exception e) {
            fail("LINK did not update.");
        }
        verifyLinkDataItem(field, LABEL_NAME, PRINCIPAL_LINKTEXT4);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testUserLinkFieldIconLinks() throws Exception {
        jGrowl("-- testUserLinkFieldIconLinks");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID7);
        WebElement field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_DISABLED_USER_LINK + "']/span"), exampleDiv);
        verifyLinkIcon(field, USER_ICON_CLASSNAME);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID7);
        field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" + LABEL_USER_LINK +
                "']/a"), exampleDiv);
        verifyKeyInInquiryHref(field,PRINCIPAL_INQUIRY_KEY_ID1);
        verifyLinkIcon(field,USER_ICON_CLASSNAME);
        verifyLinkDataItem(field, LABEL_NAME, PRINCIPAL_LINKTEXT1);
    }

    protected void testUserLinkFieldRefreshIconLinks() throws Exception {
        jGrowl("-- testUserLinkFieldIconLinks");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID7);
        assertTextPresent(DEMO_PAGE_HEADER7);

        WebElement field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                DATA_LABEL_PRINCIPAL_ID + "']/div/div/div/a"), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, PRINCIPAL_INQUIRY_KEY_ID4);

        clearTextByName("testPrincipalId4");
        waitAndTypeByName("testPrincipalId4", PRINCIPAL_ID1);
        typeTab();
        Thread.sleep(2000);
        waitAndClickByName("testPrincipalId4");

        waitForProgressLoading();
        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID7);
        try {
            field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                    DATA_LABEL_PRINCIPAL_ID + "']/div/div/div/a"), exampleDiv);
        } catch (Exception e) {
            fail("LINK did not update.");
        }
        verifyLinkDataItem(field, LABEL_NAME, PRINCIPAL_LINKTEXT1);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testUserLinkFieldExamples() throws Exception {
        testUserLinkFieldDefault();
        testUserLinkFieldLabel();
        testUserLinkFieldLinkDisabled();
        testUserLinkFieldNoLightbox();
        testUserLinkFieldRefresh();
        testUserLinkFieldIconLinks();
    }

    @Test
    public void testUserLinkFieldExamplesBookmark() throws Exception {
        testUserLinkFieldExamples();
        passed();
    }

    @Test
    public void testUserLinkFieldAddonRefreshExamplesBookmark() throws Exception {
        testUserLinkFieldRefreshAddon();
        testUserLinkFieldRefreshIconLinks();
        passed();
    }

    @Test
    public void testUserLinkFieldExamplesNav() throws Exception {
        testUserLinkFieldExamples();
        passed();
    }

    @Test
    public void testUserLinkFieldAddonRefreshExamplesNav() throws Exception {
        testUserLinkFieldRefreshAddon();
        testUserLinkFieldRefreshIconLinks();
        passed();
    }
}
