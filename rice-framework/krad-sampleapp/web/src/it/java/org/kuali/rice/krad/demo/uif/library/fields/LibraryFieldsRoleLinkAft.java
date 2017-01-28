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
public class LibraryFieldsRoleLinkAft extends LibraryFieldsKimLinkBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-RoleLinkFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-RoleLinkFieldView&methodToCall=start";

    public static final String ROLE_ID1 = "KRSAP10005";
    public static final String ROLE_ID2 = "KR1000";
    public static final String ROLE_ID3 = "67";
    public static final String ROLE_ID4 = "KR1000";
    public static final String ROLE_ID5 = "KR1000";
    public static final String ROLE_NAMESPACECODE1 = "KR-SAP";
    public static final String ROLE_NAMESPACECODE1_DROPDOWN = "KKKKKKKKK";
    public static final String ROLE_NAMESPACECODE2 = "KUALI";
    public static final String ROLE_NAMESPACECODE3 = "KR-WKFLW";
    public static final String ROLE_NAMESPACECODE4 = "KUALI";
    public static final String ROLE_NAMESPACECODE5 = "KUALI";
    public static final String ROLE_NAME1 = "Travel Approver";
    public static final String ROLE_NAME2 = "GuestRole";
    public static final String ROLE_NAME3 = "Router";
    public static final String ROLE_NAME4 = "GuestRole";
    public static final String ROLE_NAME5 = "GuestRole";
    public static final String ROLE_LINKTEXT1 = "KR-SAP Travel Approver";
    public static final String ROLE_LINKTEXT2 = "KUALI GuestRole";
    public static final String ROLE_LINKTEXT3 = "KR-WKFLW Router";
    public static final String ROLE_LINKTEXT4 = "KUALI GuestRole";
    public static final String ROLE_LINKTEXT5 = "KUALI GuestRole";
    public static final String ROLE_INQUIRY_KEY_ID1 = "id=KRSAP10005";
    public static final String ROLE_INQUIRY_KEY_ID2 = "id=KR1000";
    public static final String ROLE_INQUIRY_KEY_ID3 = "id=67";
    public static final String ROLE_INQUIRY_KEY_ID4 = "id=KR1000";
    public static final String ROLE_INQUIRY_KEY_ID5 = "id=KR1000";
    public static final String ROLE_INQUIRY_KEY_NAME1 = "name=Travel+Approver";
    public static final String ROLE_INQUIRY_KEY_NAME2 = "name=GuestRole";
    public static final String ROLE_INQUIRY_KEY_NAME3 = "name=Router";
    public static final String ROLE_INQUIRY_KEY_NAME4 = "name=GuestRole";
    public static final String ROLE_INQUIRY_KEY_NAME5 = "name=GuestRole";
    public static final String ROLE_INQUIRY_KEY_NAMESPACECODE1 = "namespaceCode=KR-SAP";
    public static final String ROLE_INQUIRY_KEY_NAMESPACECODE2 = "namespaceCode=KUALI";
    public static final String ROLE_INQUIRY_KEY_NAMESPACECODE3 = "namespaceCode=KR-WKFLW";
    public static final String ROLE_INQUIRY_KEY_NAMESPACECODE4 = "namespaceCode=KUALI";
    public static final String ROLE_INQUIRY_KEY_NAMESPACECODE5 = "namespaceCode=KUALI";

    public static final String ROLE_ICON_CLASSNAME = "icon-stack";
    public static final String LABEL_ROLE_NAME = "Role Name";
    public static final String LABEL_NAME = "Name";
    public static final String LABEL_ROLE_LINK = "Role Link";
    public static final String LABEL_ROLE_LINK1 = "Role Link1";
    public static final String LABEL_ROLE_LINK2 = "Role Link2";
    public static final String LABEL_NAMESPACECODE = "NamespaceCode";
    public static final String LABEL_ROLENAME = "RoleName";
    public static final String LABEL_DISABLED_ROLE_LINK = "Disabled Role Link";
    public static final String LABEL_ROLE_ID = "Role Id";

    public static final String DEMO_PAGE_ID1 = "Demo-RoleLinkField-Example1";
    public static final String DEMO_PAGE_ID2 = "Demo-RoleLinkField-Example2";
    public static final String DEMO_PAGE_ID3 = "Demo-RoleLinkField-Example3";
    public static final String DEMO_PAGE_ID4 = "Demo-RoleLinkField-Example4";
    public static final String DEMO_PAGE_ID5 = "Demo-RoleLinkField-Example5";
    public static final String DEMO_PAGE_ID6 = "Demo-RoleLinkField-Example6";
    public static final String DEMO_PAGE_ID7 = "Demo-RoleLinkField-Example7";
    public static final String DEMO_PAGE_ID8 = "Demo-RoleLinkField-Example8";
    public static final String DEMO_PAGE_HEADER1 = "Default";
    public static final String DEMO_PAGE_HEADER2 = "Role Link Field with Label";
    public static final String DEMO_PAGE_HEADER3 = "Role Link Field with Link Disabled";
    public static final String DEMO_PAGE_HEADER4 = "Inquiry without Lightbox";
    public static final String DEMO_PAGE_HEADER5 = "Role Link refreshed by Field changes";
    public static final String DEMO_PAGE_HEADER6 = "Role Link as Input Field Addon";
    public static final String DEMO_PAGE_HEADER7 = "Role Link as Icon";
    public static final String DEMO_PAGE_HEADER8 = "Role Link with LinkText namespaceCode suppressed";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Role Link Field");
    }

    @Override
    public WebElement navigateToExample(String exampleId) throws Exception {
        //navigate to base page ensures starting point for each navigateToExample
        navigateToLibraryDemo("Fields", "Role Link Field");
        return super.navigateToExample(exampleId);
    }

    protected void testRoleLinkFieldDefault() throws Exception {
        jGrowl("-- testRoleLinkFieldDefault");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID1);
        assertTextPresent(DEMO_PAGE_HEADER1);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        List<WebElement> links = exampleDiv.findElements(By.className(UIF_LINK_CLASSNAME));
        assertEquals("Number of expected role field links not found on page.", 2, links.size());

        for ( WebElement link : links) {
            String href =  link.getAttribute(HREF_ATTRIBUTE);
            if ( !StringUtils.contains(href, INQUIRY)) {
                fail("Inquiry not found in link.");
            }

            if ( !(StringUtils.contains(href, ROLE_INQUIRY_KEY_ID1) ||
                    (StringUtils.contains(href, ROLE_INQUIRY_KEY_NAMESPACECODE2) &&
                            StringUtils.contains(href, ROLE_INQUIRY_KEY_NAME2)))) {
                fail("Role inquiry keys not found in href.");
            }

            String linkText = link.getText();
            if ( !(StringUtils.contains(linkText, ROLE_LINKTEXT1) ||
                   StringUtils.contains(linkText, ROLE_LINKTEXT2))) {
                fail("Role names in linkText not found");
            }
        }

        field = findElement(By.linkText(ROLE_LINKTEXT1), exampleDiv);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME1);

        field = findElement(By.linkText(ROLE_LINKTEXT2), exampleDiv);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME2);
    }

    protected void testRoleLinkFieldLabel() throws Exception {
        jGrowl("-- testRoleLinkFieldLabel");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID2);
        assertTextPresent(DEMO_PAGE_HEADER2);

        assertLabelWithTextPresent(LABEL_ROLE_LINK1);
        assertLabelWithTextPresent(LABEL_ROLE_LINK2);

        WebElement field = findElement(By.linkText(ROLE_LINKTEXT1), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_ID1);
        verifyLinkText(field, ROLE_LINKTEXT1);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME1);

        field = findElement(By.linkText(ROLE_LINKTEXT2),exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAMESPACECODE2);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAME2);
        verifyLinkText(field, ROLE_LINKTEXT2);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME2);
    }

    protected void testRoleLinkFieldLinkDisabled() throws Exception {
        jGrowl("-- testRoleLinkFieldLinkDisabled");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID3);
        assertTextPresent(DEMO_PAGE_HEADER3);

        assertLabelWithTextPresent(LABEL_ROLE_LINK1);
        assertLabelWithTextPresent(LABEL_ROLE_LINK2);

        WebElement disabledLink = findElement(By.xpath("//span[contains( text(),'" + ROLE_LINKTEXT1 + "')]"), exampleDiv);
        assertEquals("Disabled Link text not found", ROLE_LINKTEXT1, disabledLink.getText());

        disabledLink = findElement(By.xpath("//span[contains( text(),'" + ROLE_LINKTEXT2 + "')]"), exampleDiv);
        assertEquals("Disabled Link text not found", ROLE_LINKTEXT2, disabledLink.getText());
    }

    protected void testRoleLinkFieldNoLightbox() throws Exception {
        jGrowl("-- testRoleLinkFieldNoLightbox");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID4);
        assertTextPresent(DEMO_PAGE_HEADER4);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);

        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_ID1);
        verifyLinkText(field, ROLE_LINKTEXT1);

        String dataOnClickScript = field.getAttribute("data-onclick");
        if ( StringUtils.contains(dataOnClickScript,"openLinkInDialog(jQuery(this), \"\");")) {
            fail("Lightbox not suppressed for Inquiry.");
        }

        waitAndClickByLinkText(field.getText());
        waitAndClickButtonByText("Back", "Unable to find Back button");
    }

    protected void testRoleLinkFieldRefresh() throws Exception {
        jGrowl("-- testRoleLinkFieldRefresh");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID5);
        assertTextPresent(DEMO_PAGE_HEADER5);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        assertLabelWithTextPresent(LABEL_ROLE_LINK);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAMESPACECODE3);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAME3);
        verifyLinkText(field, ROLE_LINKTEXT3);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME3);

        waitForExampleElementById(DEMO_PAGE_ID5);
        waitAndClickQuickFinderButton("QuickFinder not found.");
        gotoLightBox();
        // brittle test due to the fact if number of namespaces changez dropdown key strokes change
        waitAndTypeByName("lookupCriteria[namespaceCode]", ROLE_NAMESPACECODE1_DROPDOWN);
        waitAndTypeByName("lookupCriteria[name]", ROLE_NAME1);
        // alternate Search parameter with same results
        //waitAndTypeByName("lookupCriteria[id]", ROLE_ID1);

        waitAndClickButtonByExactText("Search");
        waitAndClickByLinkText("return value");

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID5);
        field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkText(field, ROLE_LINKTEXT1);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAMESPACECODE1);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAME1);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID5);
        field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME1);
        waitForExampleElementById(DEMO_PAGE_ID5);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testRoleLinkFieldAddon() throws Exception {
        jGrowl("-- testRoleLinkFieldAddon");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID6);
        assertTextPresent(DEMO_PAGE_HEADER6);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAMESPACECODE4);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAME4);
        verifyLinkText(field, ROLE_LINKTEXT4);

        clearTextByName("testRoleName4");
        waitAndTypeByName("testRoleName4",ROLE_NAME2);
        typeTab();
        Thread.sleep(2000);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID6);
        field = findElement(By.className(UIF_LINK_CLASSNAME), exampleDiv);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME2);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testRoleLinkFieldIconLinks() throws Exception {
        jGrowl("-- testRoleLinkFieldIconLinks");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID7);
        assertTextPresent(DEMO_PAGE_HEADER7);

        WebElement field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_ROLE_LINK1 + "']/a"), exampleDiv);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_ID5);
        verifyLinkIcon(field, ROLE_ICON_CLASSNAME);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME5);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID7);
        field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_ROLE_LINK2 + "']/span"), exampleDiv);
        verifyLinkIcon(field, ROLE_ICON_CLASSNAME);
    }

    protected void testRoleLinkFieldRefreshIconLinks() throws Exception {
        jGrowl("-- testRoleLinkFieldRefreshIconLinks");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID7);
        assertTextPresent(DEMO_PAGE_HEADER7);

        WebElement field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_ROLENAME + "']/div/div/div/a[contains(@class,'" + ROLE_ICON_CLASSNAME + "')]"), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME5);

        clearTextByName("testRoleNamespaceCode5");
        waitAndTypeByName("testRoleNamespaceCode5", ROLE_NAMESPACECODE3);
        typeTab();
        clearTextByName("testRoleName5");
        waitAndTypeByName("testRoleName5", ROLE_NAME3);
        typeTab();
        Thread.sleep(2000);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID7);
        field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" + LABEL_ROLENAME +
                "']/div/div/div/a[contains(@class,'" + ROLE_ICON_CLASSNAME + "')]"), exampleDiv);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME3);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testRoleLinkFieldSuppressedNcLink() throws Exception {
        jGrowl("-- testRoleLinkFieldSuppressedNcLink");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID8);
        assertTextPresent(DEMO_PAGE_HEADER8);

        WebElement field = findElement(By.linkText(ROLE_NAME1), exampleDiv);
        assertLabelWithTextPresent(LABEL_ROLE_LINK1);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_ID1);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME1);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID8);
        field = findElement(By.linkText(ROLE_NAME2), exampleDiv);
        assertLabelWithTextPresent(LABEL_ROLE_LINK2);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, ROLE_INQUIRY_KEY_NAME2);
        verifyLinkDataItem(field, LABEL_ROLE_NAME, ROLE_NAME2);
    }

    protected void testRoleLinkFieldExamples() throws Exception {
        testRoleLinkFieldDefault();
        testRoleLinkFieldLabel();
        testRoleLinkFieldLinkDisabled();
        testRoleLinkFieldNoLightbox();
        testRoleLinkFieldRefresh();
        testRoleLinkFieldIconLinks();
        testRoleLinkFieldSuppressedNcLink();
    }

    @Test
    public void testRoleLinkFieldExamplesBookmark() throws Exception {
        testRoleLinkFieldExamples();
        passed();
    }

    @Test
    public void testRoleLinkFieldRefreshExamplesBookmark() throws Exception {
        testRoleLinkFieldAddon();
        testRoleLinkFieldRefreshIconLinks();
        passed();
    }

    @Test
      public void testRoleLinkFieldExamplesNav() throws Exception {
        testRoleLinkFieldExamples();
        passed();
    }

    @Test
    public void testRoleLinkFieldRefreshExamplesNav() throws Exception {
        testRoleLinkFieldAddon();
        testRoleLinkFieldRefreshIconLinks();
        passed();
    }
}
