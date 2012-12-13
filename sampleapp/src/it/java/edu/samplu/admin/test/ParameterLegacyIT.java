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

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * tests the Parameter section in Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParameterLegacyIT extends AdminMenuLegacyITBase {
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuLegacyITBase#getLinkLocator()
     */
    String docId;
    String parameterName;
    @Override
    protected String getLinkLocator() {
        return "Parameter";
    }
    @Test
    public void testParameter() throws Exception {
        
        super.gotoCreateNew();
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndTypeByName("document.newMaintainableObject.value", "Y");
        waitAndTypeByName("document.newMaintainableObject.description", "for testing");
        selectOptionByName("document.newMaintainableObject.parameterTypeCode", "HELP");
        waitAndClickByXpath("//input[@name='document.newMaintainableObject.evaluationOperatorCode' and @value='A']");
    
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]", "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("--------------------------------New Parameter Created-------------------------");
        
        //Lookup
        super.gotoMenuLinkLocator();
        waitAndTypeByName("name", parameterName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");        
        Thread.sleep(2000);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("Y", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        System.out.println("--------------------------------Lookup And View Successful-------------------------");
        
        //edit
        selectFrame("iframeportlet");
        waitAndClickByLinkText("edit");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
	    clearTextByName("document.newMaintainableObject.value");
	    waitAndTypeByName("document.newMaintainableObject.value", "N");
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]", "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Edited-------------------------");
        
        //Verify if its edited
        super.gotoMenuLinkLocator();
        waitAndTypeByName("name", parameterName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
        
        
        //copy
        selectFrame("iframeportlet");
        waitAndClickByLinkText("copy");
        waitForPageToLoad();
        docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + ITUtil.DTS_TWO;
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        waitAndClickByXpath("//input[@name='methodToCall.route' and @alt='submit']");
        waitForPageToLoad();
        assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]", "Document is not submitted successfully");
        selectTopFrame();
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a"));
        assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        selectTopFrame();
        System.out.println("-----------------------------------Parameter Edited-------------------------");
        
        //Verify if its copied
        super.gotoMenuLinkLocator();
        waitAndTypeByName("name", parameterName);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        isElementPresentByLinkText(parameterName);
        waitAndClickByLinkText(parameterName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickByXpath("//*[@title='close this window']");
        switchToWindow("null");
    }

    
}
