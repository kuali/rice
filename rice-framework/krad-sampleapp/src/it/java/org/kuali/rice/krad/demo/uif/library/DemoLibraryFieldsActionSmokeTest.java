package org.kuali.rice.krad.demo.uif.library;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryFieldsActionSmokeTest extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ActionField-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ActionField-View&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Action Field");
    }

    protected void testActionFieldDefault() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ActionField-Example1");
        WebElement field = findElement(By.cssSelector(".uif-actionLink"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        waitAndClickByLinkText(field.getText());

        assertTrue(driver.switchTo().alert().getText().contains("You clicked the link"));
        alertAccept();
    }

    protected void testActionFieldPresubmit() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ActionField-Example2");
        List<WebElement> fields = exampleDiv.findElements(By.cssSelector("a.uif-actionLink"));

        assertEquals("Two action links do not exist on the page", 2, fields.size());

        assertElementPresentByLinkText("Pre submit returning true Link");
        waitAndClickByLinkText("Pre submit returning true Link");
        assertTrue(driver.switchTo().alert().getText().contains("Pre submit call was invoked, returning true"));
        driver.switchTo().alert().accept();

        assertElementPresentByLinkText("Pre submit returning false Link");
        waitAndClickByLinkText("Pre submit returning false Link");
        assertTrue(driver.switchTo().alert().getText().contains("Pre submit call was invoked, returning false"));
        driver.switchTo().alert().accept();
    }

    protected void testActionFieldSuccessCallback() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ActionField-Example3");
        WebElement field = findElement(By.cssSelector(".uif-actionLink"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        waitAndClickByLinkText(field.getText());

        assertTrue(driver.switchTo().alert().getText().contains("Refresh called successfully"));
        alertAccept();
    }

    protected void testActionFieldValidation() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ActionField-Example4");
        WebElement field = findElement(By.cssSelector(".uif-actionLink"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        waitAndClickByLinkText(field.getText());

        assertElementPresent(".uif-errorMessageItem a");
    }

    protected void testActionFieldImages() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ActionField-Example5");
        List<WebElement> fields = exampleDiv.findElements(By.cssSelector("a.uif-actionLink"));

        assertEquals(2, fields.size());

        WebElement leftField = fields.get(0);
        WebElement rightField = fields.get(1);

        String leftFieldId = leftField.getAttribute("id");
        String rightFieldId = rightField.getAttribute("id");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        XPath xPathFactory = XPathFactory.newInstance().newXPath();
        Document document = builder.parse(IOUtils.toInputStream(driver.getPageSource()));

        Node leftFieldImg = (Node) xPathFactory.evaluate("//a[@id='" + leftFieldId + "']/img", document,
                XPathConstants.NODE);
        Node leftFieldImgNextSibling = leftFieldImg.getNextSibling();
        if (!leftFieldImgNextSibling.getTextContent().contains("Action Link with left image")) {
            fail("Image is not on the left of the link");
        }

        Node rightFieldText = (Node) xPathFactory.evaluate(
                "//a[@id='" + rightFieldId + "']/text()[contains(., 'Action Link with right image')]", document,
                XPathConstants.NODE);
        Node rightFieldTextNextSibling = rightFieldText.getNextSibling();
        if (!rightFieldTextNextSibling.getNodeName().equals("img")) {
            fail("Image is not on the right of the link");
        }
    }

    protected void testActionFieldButtons() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-ActionField-Example6");
        List<WebElement> fields = exampleDiv.findElements(By.cssSelector("button.uif-primaryActionButton"));

        assertEquals(7, fields.size());

        String buttonFieldId = fields.get(0).getAttribute("id");
        String imageBottomFieldId = fields.get(1).getAttribute("id");
        String imageTopFieldId = fields.get(2).getAttribute("id");
        String imageLeftFieldId = fields.get(3).getAttribute("id");
        String imageRightFieldId = fields.get(4).getAttribute("id");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        XPath xPathFactory = XPathFactory.newInstance().newXPath();
        Document document = builder.parse(IOUtils.toInputStream(driver.getPageSource()));

        assertIsVisible("#" + buttonFieldId);
        waitAndClickButtonByText(fields.get(0).getText());
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        alertAccept();

        assertElementPresent("#" + imageBottomFieldId + " span.topBottomSpan img[src='/krad/images/searchicon.png']");
        Node topFieldText = (Node) xPathFactory.evaluate(
                "//button[@id='" + imageBottomFieldId + "']/text()[contains(., 'Image BOTTOM')]", document,
                XPathConstants.NODE);
        Node topFieldTextNextSibling = topFieldText.getNextSibling();
        if (!topFieldTextNextSibling.getNodeName().equals("span")) {
            fail("Image is not on the bottom of the text");
        }

        assertElementPresent("#" + imageTopFieldId + " span.topBottomSpan img[src='/krad/images/searchicon.png']");
        Node bottomFieldText = (Node) xPathFactory.evaluate(
                "//button[@id='" + imageTopFieldId + "']/text()[contains(., 'Image TOP')]", document,
                XPathConstants.NODE);
        Node bottomFieldImgNextSibling = bottomFieldText.getPreviousSibling();
        if (!bottomFieldImgNextSibling.getNodeName().contains("span")) {
            fail("Image is not on the top of the text");
        }

        Node leftFieldImg = (Node) xPathFactory.evaluate("//button[@id='" + imageLeftFieldId + "']/img", document,
                XPathConstants.NODE);
        Node leftFieldImgNextSibling = leftFieldImg.getNextSibling();
        if (!leftFieldImgNextSibling.getTextContent().contains("Image LEFT")) {
            fail("Image is not on the left of the text");
        }

        Node rightFieldText = (Node) xPathFactory.evaluate(
                "//button[@id='" + imageRightFieldId + "']/text()[contains(., 'Image RIGHT')]", document,
                XPathConstants.NODE);
        Node rightFieldTextNextSibling = rightFieldText.getNextSibling();
        if (!rightFieldTextNextSibling.getNodeName().equals("img")) {
            fail("Image is not on the right of the text");
        }

        driver.findElement(By.xpath(
                "//button[contains(text(),'Disabled Button') and @disabled]/preceding-sibling::button/img"));
        driver.findElement(By.xpath("//button/img[contains(@alt,'Image Only button')]"));

        driver.findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]"));
    }

    protected void testActionFieldExamples() throws Exception {
        testActionFieldDefault();
        testActionFieldPresubmit();
        testActionFieldSuccessCallback();
        testActionFieldValidation();
        testActionFieldImages();
        testActionFieldButtons();
    }

    @Test
    public void testActionFieldExamplesBookmark() throws Exception {
        testActionFieldExamples();
        passed();
    }

    @Test
    public void testActionFieldExamplesNav() throws Exception {
        testActionFieldExamples();
        passed();
    }

    @Test
    public void testActionFieldDefaultBookmark() throws Exception {
        testActionFieldDefault();
        passed();
    }

    @Test
    public void testActionFieldDefaultNav() throws Exception {
        testActionFieldDefault();
        passed();
    }

    @Test
    public void testActionFieldPresubmitBookmark() throws Exception {
        testActionFieldPresubmit();
        passed();
    }

    @Test
    public void testActionFieldPresubmitNav() throws Exception {
        testActionFieldPresubmit();
        passed();
    }

    @Test
    public void testActionFieldSuccessCallbackBookmark() throws Exception {
        testActionFieldSuccessCallback();
        passed();
    }

    @Test
    public void testActionFieldSuccessCallbackNav() throws Exception {
        testActionFieldSuccessCallback();
        passed();
    }

    @Test
    public void testActionFieldValidationBookmark() throws Exception {
        testActionFieldValidation();
        passed();
    }

    @Test
    public void testActionFieldValidationNav() throws Exception {
        testActionFieldValidation();
        passed();
    }

    @Test
    public void testActionFieldImagesBookmark() throws Exception {
        testActionFieldImages();
        passed();
    }

    @Test
    public void testActionFieldImagesNav() throws Exception {
        testActionFieldImages();
        passed();
    }

    @Test
    public void testActionFieldButtonsBookmark() throws Exception {
        testActionFieldButtons();
        passed();
    }

    @Test
    public void testActionFieldButtonsNav() throws Exception {
        testActionFieldButtons();
        passed();
    }
}
