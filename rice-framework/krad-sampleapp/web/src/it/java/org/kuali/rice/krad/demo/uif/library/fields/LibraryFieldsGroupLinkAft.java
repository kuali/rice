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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryFieldsGroupLinkAft extends LibraryFieldsKimLinkBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-GroupLinkFieldView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-GroupLinkFieldView&methodToCall=start";

    public static final String GROUP_ID1 = "2005";
    public static final String GROUP_ID2 = "2001";
    public static final String GROUP_ID3 = "2000";
    public static final String GROUP_ID4 = "2005";
    public static final String GROUP_ID5 = "2005";
    public static final String GROUP_NAMESPACECODE1 = "KUALI";
    public static final String GROUP_NAMESPACECODE1_DROPDOWN = "KKKKKKKKKKKK";
    public static final String GROUP_NAMESPACECODE2 = "KUALI";
    public static final String GROUP_NAMESPACECODE3 = "KR-WKFLW";
    public static final String GROUP_NAMESPACECODE4 = "KUALI";
    public static final String GROUP_NAMESPACECODE5 = "KUALI";
    public static final String GROUP_NAME1 = "Group1";
    public static final String GROUP_NAME2 = "TestGroup1";
    public static final String GROUP_NAME3 = "NotificationAdmin";
    public static final String GROUP_NAME4 = "Group1";
    public static final String GROUP_NAME5 = "Group1";
    public static final String GROUP_LINKTEXT1 = "KUALI Group1";
    public static final String GROUP_LINKTEXT2 = "KUALI TestGroup1";
    public static final String GROUP_LINKTEXT3 = "KR-WKFLW NotificationAdmin";
    public static final String GROUP_LINKTEXT4 = "KUALI Group1";
    public static final String GROUP_LINKTEXT5 = "KUALI Group1";
    public static final String GROUP_INQUIRY_KEY_ID1 = "id=2005";
    public static final String GROUP_INQUIRY_KEY_ID2 = "id=2001";
    public static final String GROUP_INQUIRY_KEY_ID3 = "id=2000";
    public static final String GROUP_INQUIRY_KEY_ID4 = "id=2005";
    public static final String GROUP_INQUIRY_KEY_ID5 = "id=2005";
    public static final String GROUP_INQUIRY_KEY_NAME1 = "name=Group1";
    public static final String GROUP_INQUIRY_KEY_NAME2 = "name=TestGroup1";
    public static final String GROUP_INQUIRY_KEY_NAME3 = "name=NotificationAdmin";
    public static final String GROUP_INQUIRY_KEY_NAME4 = "name=Group1";
    public static final String GROUP_INQUIRY_KEY_NAME5 = "name=Group1";
    public static final String GROUP_INQUIRY_KEY_NAMESPACECODE1 = "namespaceCode=KUALI";
    public static final String GROUP_INQUIRY_KEY_NAMESPACECODE2 = "namespaceCode=KUALI";
    public static final String GROUP_INQUIRY_KEY_NAMESPACECODE3 = "namespaceCode=KR-WKFLW";
    public static final String GROUP_INQUIRY_KEY_NAMESPACECODE4 = "namespaceCode=KUALI";
    public static final String GROUP_INQUIRY_KEY_NAMESPACECODE5 = "namespaceCode=KUALI";

    public static final String GROUP_ICON_CLASSNAME = "icon-users";
    public static final String LABEL_GROUP_NAME = "Group Name";
    public static final String LABEL_NAME = "Name";
    public static final String LABEL_GROUP_LINK = "Group Link";
    public static final String LABEL_GROUP_LINK1 = "Group Link1";
    public static final String LABEL_GROUP_LINK2 = "Group Link2";
    public static final String LABEL_NAMESPACECODE = "NamespaceCode";
    public static final String LABEL_GROUPNAME = "GroupName";
    public static final String LABEL_DISABLED_GROUP_LINK = "Disabled Group Link";
    public static final String LABEL_GROUP_ID = "Group Id";

    public static final String DEMO_PAGE_ID1 = "Demo-GroupLinkField-Example1";
    public static final String DEMO_PAGE_ID2 = "Demo-GroupLinkField-Example2";
    public static final String DEMO_PAGE_ID3 = "Demo-GroupLinkField-Example3";
    public static final String DEMO_PAGE_ID4 = "Demo-GroupLinkField-Example4";
    public static final String DEMO_PAGE_ID5 = "Demo-GroupLinkField-Example5";
    public static final String DEMO_PAGE_ID6 = "Demo-GroupLinkField-Example6";
    public static final String DEMO_PAGE_ID7 = "Demo-GroupLinkField-Example7";
    public static final String DEMO_PAGE_ID8 = "Demo-GroupLinkField-Example8";
    public static final String DEMO_PAGE_HEADER1 = "Default";
    public static final String DEMO_PAGE_HEADER2 = "Group Link Field with Label";
    public static final String DEMO_PAGE_HEADER3 = "Group Link Field with Link Disabled";
    public static final String DEMO_PAGE_HEADER4 = "Inquiry without Lightbox";
    public static final String DEMO_PAGE_HEADER5 = "Group Link refreshed by Field changes";
    public static final String DEMO_PAGE_HEADER6 = "Group Link as Input Field Addon";
    public static final String DEMO_PAGE_HEADER7 = "Group Link as Icon";
    public static final String DEMO_PAGE_HEADER8 = "Group Link with LinkText namespaceCode suppressed";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Group Link Field");
    }

    @Override
    public WebElement navigateToExample(String exampleId) throws Exception {
        //navigate to base page ensures starting point for each navigateToExample
        navigateToLibraryDemo("Fields", "Group Link Field");
        return super.navigateToExample(exampleId);
    }

    protected void testGroupLinkFieldDefault() throws Exception {
        jGrowl("-- testGroupLinkFieldDefault");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID1);
        assertTextPresent(DEMO_PAGE_HEADER1);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        List<WebElement> links = exampleDiv.findElements(By.className(UIF_LINK_CLASSNAME));
        assertEquals("Number of expected group field links not found on page.", 2, links.size());

        for ( WebElement link : links) {
            String href =  link.getAttribute(HREF_ATTRIBUTE);
            if ( !StringUtils.contains(href, INQUIRY)) {
                fail("Inquiry not found in link.");
            }

            if ( !(StringUtils.contains(href, GROUP_INQUIRY_KEY_ID1) ||
                    (StringUtils.contains(href, GROUP_INQUIRY_KEY_NAMESPACECODE2) &&
                     StringUtils.contains(href, GROUP_INQUIRY_KEY_NAME2)))) {
                fail("Group inquiry keys not found in href.");
            }

            String linkText = link.getText();
            if ( !(StringUtils.contains(linkText, GROUP_LINKTEXT1) ||
                   StringUtils.contains(linkText, GROUP_LINKTEXT2))) {
                fail("Group names in linkText not found");
            }
        }

        field = findElement(By.linkText(GROUP_LINKTEXT1), exampleDiv);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME1);

        field = findElement(By.linkText(GROUP_LINKTEXT2), exampleDiv);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME2);
    }

    protected void testGroupLinkFieldLabel() throws Exception {
        jGrowl("-- testGroupLinkFieldLabel");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID2);
        assertTextPresent(DEMO_PAGE_HEADER2);

        assertLabelWithTextPresent(LABEL_GROUP_LINK1);
        assertLabelWithTextPresent(LABEL_GROUP_LINK2);

        WebElement field = findElement(By.linkText(GROUP_LINKTEXT1), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_ID1);
        verifyLinkText(field, GROUP_LINKTEXT1);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME1);

        field = findElement(By.linkText(GROUP_LINKTEXT2),exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAMESPACECODE2);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAME2);
        verifyLinkText(field, GROUP_LINKTEXT2);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME2);
    }

    protected void testGroupLinkFieldLinkDisabled() throws Exception {
        jGrowl("-- testGroupLinkFieldLinkDisabled");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID3);
        assertTextPresent(DEMO_PAGE_HEADER3);

        assertLabelWithTextPresent(LABEL_GROUP_LINK1);
        assertLabelWithTextPresent(LABEL_GROUP_LINK2);

        WebElement disabledLink = findElement(By.xpath("//span[contains( text(),'" + GROUP_LINKTEXT1 + "')]"), exampleDiv);
        assertEquals("Disabled Link text not found", GROUP_LINKTEXT1, disabledLink.getText());

        disabledLink = findElement(By.xpath("//span[contains( text(),'" + GROUP_LINKTEXT2 + "')]"), exampleDiv);
        assertEquals("Disabled Link text not found", GROUP_LINKTEXT2, disabledLink.getText());
    }

    protected void testGroupLinkFieldNoLightbox() throws Exception {
        jGrowl("-- testGroupLinkFieldNoLightbox");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID4);
        assertTextPresent(DEMO_PAGE_HEADER4);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);

        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_ID1);
        verifyLinkText(field, GROUP_LINKTEXT1);

        String dataOnClickScript = field.getAttribute("data-onclick");
        if ( StringUtils.contains(dataOnClickScript,"openLinkInDialog(jQuery(this), \"\");")) {
            fail("Lightbox not suppressed for Inquiry.");
        }

        waitAndClickByLinkText(field.getText());
        waitAndClickButtonByText("Back", "Unable to find Back button");
    }

    protected void testGroupLinkFieldRefresh() throws Exception {
        jGrowl("-- testGroupLinkFieldRefresh");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID5);
        assertTextPresent(DEMO_PAGE_HEADER5);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        assertLabelWithTextPresent(LABEL_GROUP_LINK);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAMESPACECODE3);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAME3);
        verifyLinkText(field, GROUP_LINKTEXT3);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME3);

        waitForExampleElementById(DEMO_PAGE_ID5);
        waitAndClickQuickFinderButton("QuickFinder not found.");
        gotoLightBox();
        // brittle test due to the fact if number of namespaces changez dropdown key strokes change
        waitAndTypeByName("lookupCriteria[namespaceCode]", GROUP_NAMESPACECODE1_DROPDOWN);
        waitAndTypeByName("lookupCriteria[name]", GROUP_NAME1);
        // alternate Search parameter with same results
        //waitAndTypeByName("lookupCriteria[id]", GROUP_ID1);

        waitAndClickButtonByExactText("Search");
        waitAndClickByLinkText("return value");

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID5);
        field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkText(field, GROUP_LINKTEXT1);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAMESPACECODE1);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAME1);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID5);
        field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME1);
        waitForExampleElementById(DEMO_PAGE_ID5);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testGroupLinkFieldAddon() throws Exception {
        jGrowl("-- testGroupLinkFieldAddon");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID6);
        assertTextPresent(DEMO_PAGE_HEADER6);

        WebElement field = findElement(By.cssSelector(UIF_LINK_CSS_SELECTOR), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAMESPACECODE4);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAME4);
        verifyLinkText(field, GROUP_LINKTEXT4);

        clearTextByName("testGroupName4");
        waitAndTypeByName("testGroupName4",GROUP_NAME2);
        typeTab();
        Thread.sleep(2000);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID6);
        field = findElement(By.className(UIF_LINK_CLASSNAME), exampleDiv);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME2);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testGroupLinkFieldIconLinks() throws Exception {
        jGrowl("-- testGroupLinkFieldIconLinks");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID7);
        assertTextPresent(DEMO_PAGE_HEADER7);

        WebElement field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_GROUP_LINK1 + "']/a"), exampleDiv);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_ID5);
        verifyLinkIcon(field, GROUP_ICON_CLASSNAME);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME5);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID7);
        field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_GROUP_LINK2 + "']/span"), exampleDiv);
        verifyLinkIcon(field, GROUP_ICON_CLASSNAME);
    }

    protected void testGroupLinkFieldRefreshIconLinks() throws Exception {
        jGrowl("-- testGroupLinkFieldRefreshIconLinks");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID7);
        assertTextPresent(DEMO_PAGE_HEADER7);

        WebElement field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" +
                LABEL_GROUPNAME + "']/div/div/div/a[contains(@class,'" + GROUP_ICON_CLASSNAME + "')]"), exampleDiv);
        verifyLinkIsInquiry(field);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME5);

        clearTextByName("testGroupNamespaceCode5");
        waitAndTypeByName("testGroupNamespaceCode5", GROUP_NAMESPACECODE3);
        typeTab();
        clearTextByName("testGroupName5");
        waitAndTypeByName("testGroupName5", GROUP_NAME3);
        typeTab();
        Thread.sleep(2000);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID7);
        field = findElement(By.xpath("//section[@id='" + DEMO_PAGE_ID7 + "']/div[@data-label='" + LABEL_GROUPNAME +
                "']/div/div/div/a[contains(@class,'" + GROUP_ICON_CLASSNAME + "')]"), exampleDiv);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME3);

        // clean up change page alert for next test navigation
        waitAndClickLibraryLink();
        acceptAlertIfPresent();
    }

    protected void testGroupLinkFieldSuppressedNcLink() throws Exception {
        jGrowl("-- testGroupLinkFieldSuppressedNcLink");
        WebElement exampleDiv = navigateToExample(DEMO_PAGE_ID8);
        assertTextPresent(DEMO_PAGE_HEADER8);

        WebElement field = findElement(By.linkText(GROUP_NAME1), exampleDiv);
        assertLabelWithTextPresent(LABEL_GROUP_LINK1);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_ID1);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME1);

        exampleDiv = waitForExampleElementById(DEMO_PAGE_ID8);
        field = findElement(By.linkText(GROUP_NAME2), exampleDiv);
        assertLabelWithTextPresent(LABEL_GROUP_LINK2);
        verifyLinkIsInquiry(field);
        verifyKeyInInquiryHref(field, GROUP_INQUIRY_KEY_NAME2);
        verifyLinkDataItem(field, LABEL_GROUP_NAME, GROUP_NAME2);
    }

    protected void testGroupLinkFieldExamples() throws Exception {
        testGroupLinkFieldDefault();
        testGroupLinkFieldLabel();
        testGroupLinkFieldLinkDisabled();
        testGroupLinkFieldNoLightbox();
        testGroupLinkFieldRefresh();
        testGroupLinkFieldIconLinks();
        testGroupLinkFieldSuppressedNcLink();
    }

    @Test
    public void testGroupLinkFieldExamplesBookmark() throws Exception {
        testGroupLinkFieldExamples();
        passed();
    }

    @Test
    public void testGroupLinkFieldRefreshExamplesBookmark() throws Exception {
        testGroupLinkFieldAddon();
        testGroupLinkFieldRefreshIconLinks();
        passed();
    }

    @Test
    public void testGroupLinkFieldExamplesNav() throws Exception {
        testGroupLinkFieldExamples();
        passed();
    }

    @Test
    public void testGroupLinkFieldRefreshExamplesNav() throws Exception {
        testGroupLinkFieldAddon();
        testGroupLinkFieldRefreshIconLinks();
        passed();
    }
}
