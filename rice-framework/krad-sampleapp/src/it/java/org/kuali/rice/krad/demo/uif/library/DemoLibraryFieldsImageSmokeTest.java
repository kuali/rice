package org.kuali.rice.krad.demo.uif.library;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryFieldsImageSmokeTest extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ImageField-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ImageField-View&methodToCall=start";

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
        findElement(By.cssSelector("span[data-label_for='" + fieldId + "'] + img[src='/krad/images/pdf.png']"),
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
