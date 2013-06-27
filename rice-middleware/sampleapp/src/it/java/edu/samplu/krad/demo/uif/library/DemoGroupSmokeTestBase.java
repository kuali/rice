package edu.samplu.krad.demo.uif.library;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DemoGroupSmokeTestBase extends WebDriverLegacyITBase {

    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=ComponentLibraryHome";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void navigation() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
    }

    protected void navigationMenu() throws Exception {
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Group");
    }

    public void testBasicGroupNav(Failable failable) throws Exception {
        navigation();

        testBasicGroupBookmark(this);
    }

    /**
     * Asserts basic group elements are present: header, validation messages,
     * instructional text, and the actual items
     *
     * @param failable
     * @throws Exception
     */
    public void testBasicGroupBookmark(Failable failable) throws Exception {
        navigationMenu();

        WebElement element = driver.findElement(By.id("Demo-Group-Example1"));
        element.findElement(By.tagName("h3"));

        getElementByDataAttributeValue(UifConstants.DataAttributes.MESSAGES_FOR, "Demo-Group-Example1");

        element.findElement(By.className("uif-instructionalMessage"));

        List<WebElement> inputFields = element.findElements(By.className("uif-inputField"));
        SeleneseTestBase.assertTrue("group does not contain correct number of items", inputFields.size() == 4);
    }
}
