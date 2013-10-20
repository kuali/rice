package org.kuali.rice.krad.demo.uif.library;

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoFieldsLinkAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LinkField-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LinkField-View&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Fields", "Link Field");
    }

    protected void testLinkFieldDefault() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-LinkField-Example1");
        WebElement field = findElement(By.cssSelector(".uif-link"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        waitAndClickByLinkText(field.getText());

        Thread.sleep(2000);

        assertTextPresent("Kuali Foundation");

        driver.navigate().back();
    }

    protected void testLinkFieldCustomTarget() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-LinkField-Example2");
        WebElement field = findElement(By.cssSelector(".uif-link"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        waitAndClickByLinkText(field.getText());

        Thread.sleep(2000);

        switchToWindow("Kuali Foundation");

        SeleneseTestBase.assertEquals("http://www.kuali.org/", driver.getCurrentUrl());
        driver.close();

        switchToWindow("Kuali");
    }

    protected void testLinkFieldLightbox() throws Exception {
        WebElement exampleDiv = navigateToExample("Demo-LinkField-Example3");
        WebElement field = findElement(By.cssSelector(".uif-link"), exampleDiv);

        String fieldId = field.getAttribute("id");

        assertIsVisible("#" + fieldId);
        waitAndClickByLinkText(field.getText());

        WebElement lightboxIFrame = driver.findElement(By.cssSelector(".fancybox-iframe"));
        if (!lightboxIFrame.getAttribute("src").contains("www.kuali.org")) {
            fail("Lightbox did not appear");
        }
    }

    protected void testLinkFieldExamples() throws Exception {
        testLinkFieldDefault();
        testLinkFieldCustomTarget();
        testLinkFieldLightbox();
    }

    @Test
    public void testLinkFieldExamplesBookmark() throws Exception {
        testLinkFieldExamples();
        passed();
    }

    @Test
    public void testLinkFieldExamplesNav() throws Exception {
        testLinkFieldExamples();
        passed();
    }

    @Test
    public void testLinkFieldDefaultBookmark() throws Exception {
        testLinkFieldDefault();
        passed();
    }

    @Test
    public void testLinkFieldDefaultNav() throws Exception {
        testLinkFieldDefault();
        passed();
    }

    @Test
    public void testLinkFieldCustomTargetBookmark() throws Exception {
        testLinkFieldCustomTarget();
        passed();
    }

    @Test
    public void testLinkFieldCustomTargetNav() throws Exception {
        testLinkFieldCustomTarget();
        passed();
    }

    @Test
    public void testLinkFieldLightboxBookmark() throws Exception {
        testLinkFieldLightbox();
        passed();
    }

    @Test
    public void testLinkFieldLightboxNav() throws Exception {
        testLinkFieldLightbox();
        passed();
    }
}
