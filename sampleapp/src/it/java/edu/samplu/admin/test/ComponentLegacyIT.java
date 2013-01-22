/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;

/**
 * tests the Component section in Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentLegacyIT extends AdminMenuLegacyITBase {
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuLegacyITBase#getLinkLocator()
     */
    String docId;
    String componentName;
    String componentCode;

    @Override
    protected String getLinkLocator() {
        return "Component";
    }

    @Test
    public void testParameter() throws Exception {

        super.gotoCreateNew();
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Component");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-IDM");
        componentCode = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.code", componentCode);
        componentName = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        checkByName("document.newMaintainableObject.active");

        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("--------------------------------New Component Created-------------------------");

        //Lookup
        super.gotoMenuLinkLocator();
        waitAndTypeByName("name", componentName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("--------------------------------Lookup And View Successful-------------------------");

        //edit
        selectFrame("iframeportlet");
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Component");
        clearTextByName("document.newMaintainableObject.name");
        componentName = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Component Edited-------------------------");

        //Verify if its edited
        super.gotoMenuLinkLocator();
        waitAndTypeByName("name", componentName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");

        //copy
        selectFrame("iframeportlet");
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Component");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-IDM");
        componentCode = "test" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.code", componentCode);
        clearTextByName("document.newMaintainableObject.name");
        componentName = "testing" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]",
                "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Component Copied-------------------------");

        //Verify if its copied
        super.gotoMenuLinkLocator();
        waitAndTypeByName("name", componentName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
    }

}
