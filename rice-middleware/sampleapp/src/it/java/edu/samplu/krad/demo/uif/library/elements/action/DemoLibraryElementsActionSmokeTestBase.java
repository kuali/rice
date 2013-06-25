package edu.samplu.krad.demo.uif.library.elements.action;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsActionSmokeTestBase extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Action-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-Action-View&methodToCall=start";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void navigation() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Action");
    }

    protected void testActionDefault() throws Exception {
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Default");
        SeleneseTestBase.assertTrue(getTextByClassName("uif-instructionalMessage").contains(
                "Action with action script"));
        assertElementPresentByLinkText("Action Link");
    }

    protected void testActionPresubmit() throws Exception {
        waitAndClickByLinkText("Presubmit");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Presubmit");
        assertTextPresent("ActionLinkField with presubmit script");
        assertElementPresentByLinkText("Pre submit returning true Link");
        assertElementPresentByLinkText("Pre submit returning false Link");

        waitAndClickByLinkText("Pre submit returning true Link");
        assertTrue(driver.switchTo().alert().getText().contains("Pre submit call was invoked, returning true"));
        driver.switchTo().alert().accept();

        waitAndClickByLinkText("Pre submit returning false Link");
        assertTrue(driver.switchTo().alert().getText().contains("Pre submit call was invoked, returning false"));
        driver.switchTo().alert().accept();
    }

    protected void testActionSuccessCallback() throws Exception {
        waitAndClickByLinkText("Success Callback");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Action Field with a success callback script");
        assertElementPresentByLinkText("Action Link success callback");

        waitAndClickByLinkText("Action Link success callback");
        assertTrue(driver.switchTo().alert().getText().contains("Refresh called successfully"));
        driver.switchTo().alert().accept();
    }

    protected void testActionValidation() throws Exception {
        waitForElementPresentByClassName("uif-page"); // make sure the page is there before we use the driver
        driver.findElement(By.className("uif-page")).findElement(By.linkText("Validation")).click();

        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Action Field with client side validation");
        assertTextPresent("InputField 1");
        assertTextNotPresent("InputField 1: Required"); // no validation error yet

        waitAndClickByLinkText("Action Link with clientside validation");
        assertTextPresent("InputField 1: Required"); // now we have a validation error

        waitAndTypeByName("inputField1", "some text");
        waitAndClickByLinkText("Action Link with clientside validation");
        assertTextNotPresent("InputField 1: Required"); // now the error goes away
    }

    protected void testActionImages() throws Exception {
        waitAndClickByLinkText("Images");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Images");
        assertTextPresent("Action Field with images");

        driver.findElement(By.partialLinkText("Action Link with left image")).findElement(By.className(
                "leftActionImage"));
        driver.findElement(By.partialLinkText("Action Link with right image")).findElement(By.className(
                "rightActionImage"));
    }

    protected void testActionButton() throws Exception {
        waitAndClickByLinkText("Buttons");
        waitForElementPresentByClassName("prettyprint");
        assertTextPresent("Buttons");
        assertTextPresent("Action Field buttons");

//        assertElementPresentByXpath("//button[text()=('button')]");
        driver.findElement(By.xpath("//button[contains(text(),'button')]"));
        driver.findElement(By.xpath("//button[contains(text(),'Image BOTTOM')]")).findElement(By.className(
                "bottomActionImage"));

        // TODO: why doesn't this work?
//        driver.findElement(By.xpath("//button[contains(text(),'Image TOP')]")).findElement(By.className("topActionImage"));
        driver.findElement(By.xpath("//span[contains(text(),'Image TOP')]"));

        // TODO: why doesn't this work?
//        driver.findElement(By.xpath("//button[contains(text(),'Image LEFT')]")).findElement(By.className("leftActionImage"));
        driver.findElement(By.xpath("//span[contains(text(),'Image LEFT')]"));

        driver.findElement(By.xpath("//button[contains(text(),'Image RIGHT')]")).findElement(By.className(
                "rightActionImage"));

        // it's tricky to select the button with no text.  This doesn't work: driver.findElement(By.xpath("//button[not(text())]"))
        // find image in the button before the disabled one
        driver.findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]/preceding-sibling::button/img"));
        driver.findElement(By.xpath("//button/img[contains(@alt,'Image Only button')]"));

        driver.findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]"));

        waitAndClickButtonByText("button");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        waitAndClickButtonByText("Image BOTTOM");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        // TODO: why doesn't this work?
//        waitAndClickButtonByText("Image TOP");
//        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
//        driver.switchTo().alert().accept();

        // TODO: why doesn't this work?
//        waitAndClickButtonByText("Image LEFT");
//        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
//        driver.switchTo().alert().accept();

        waitAndClickButtonByText("Image RIGHT");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();
    }

    private void testAllActionTabs() throws Exception {
        testActionDefault();
        testActionPresubmit();
        testActionSuccessCallback();
        testActionValidation();
        testActionImages();
        testActionButton();
    }

    public void testActionBookmark(Failable failable) throws Exception {
        testAllActionTabs();
        passed();
    }

    public void testActionNav(Failable failable) throws Exception {
        navigation();
        testAllActionTabs();
        passed();
    }

}
