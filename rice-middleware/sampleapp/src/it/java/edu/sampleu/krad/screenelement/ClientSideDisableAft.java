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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ClientSideDisableAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=ClientDisableView&methodToCall=start
     */
    public static final String BOOKMARK_URL ="/kr-krad/uicomponents?viewId=ClientDisableView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Client-side disable");
        switchToWindow("Kuali :: Client-side Disable");
    }
    
    private void testClientSideDisable() throws Exception{
        waitAndClickByXpath("//input[@name='field118' and @value='disable']");
        waitForElementPresentByXpath("//input[@name='field2' and @disabled]");
        waitForElementPresentByXpath("//textarea[@name='field3' and @disabled]");
        waitForElementPresentByXpath("//select[@name='field3' and @disabled]");
        waitForElementPresentByXpath("//select[@name='field114' and @disabled]");
        waitForElementPresentByXpath("//input[@name='field117' and @type='radio' and @disabled]");
        waitForElementPresentByXpath("//input[@name='bField1' and @type='checkbox' and @disabled]");
        waitForElementPresentByXpath("//input[@name='field115' and @type='checkbox' and @disabled]");
        waitForElementPresentByXpath("//button[contains(text(),'Action Button') and @disabled]");
        waitAndClickByXpath("//input[@name='field118' and @value='enable']");
        waitForElementNotPresent(By.xpath("//input[@name='field2' and @disabled]"));
        waitForElementNotPresent(By.xpath("//textarea[@name='field3' and @disabled]"));
        waitForElementNotPresent(By.xpath("//select[@name='field3' and @disabled]"));
        waitForElementNotPresent(By.xpath("//select[@name='field114' and @disabled]"));
        waitForElementNotPresent(By.xpath("//input[@name='field117' and @type='radio' and @disabled]"));
        waitForElementNotPresent(By.xpath("//input[@name='bField1' and @type='checkbox' and @disabled]"));
        waitForElementNotPresent(By.xpath("//input[@name='field115' and @type='checkbox' and @disabled]"));
        waitAndTypeByName("field50","hi");
        waitAndTypeByName("field51","hello");
        waitForElementPresentByXpath("//input[@name='field54' and @disabled]");
        waitAndTypeByName("field52","hello");
        waitForElementPresentByXpath("//input[@name='field54']");
        waitAndTypeByName("field53","disable");
        waitForElementPresentByXpath("//button[contains(text(),'Action Button (change)') and @disabled]");
        waitAndTypeByName("field52","");
        waitForElementPresentByXpath("//button[contains(text(),'Action Button (keyUp)') and @disabled]");
        waitForElementPresentByXpath("//button[@id='Demo-DisableSection4_add' and @disabled]");
        waitForElementPresentByXpath("//input[@name='field12' and @disabled]");
        waitForElementNotPresent(By.xpath("//input[@name='field10' and @disabled]"));
        waitAndClickByXpath("//input[@name='stringList2' and @value='1']");
        waitForElementPresentByXpath("//input[@name='field10' and @disabled]");
        waitForElementNotPresent(By.xpath("//input[@name='field12' and @disabled]"));
        waitAndClickByXpath("//input[@name='stringList2' and @value='2']");
        waitAndClickByXpath("//input[@name='stringList2' and @value='3']");
        waitForElementPresentByXpath("//input[@name='field11' and @disabled]");
    }
    
    @Test
    public void testClientSideDisableBookmark() throws Exception {
        testClientSideDisable();
        passed();
    }

    @Test
    public void testClientSideDisableNav() throws Exception {
        testClientSideDisable();
        passed();
    }
    
}
