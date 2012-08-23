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
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import edu.samplu.common.UpgradedSeleniumITBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * tests that user admin's blanket approve of the Parameter maintenance document results in a final document
 * The component is chosen from a lookup screen
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigParameterBlanketAppIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    public void testParameter() throws Exception {
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Administration");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=Parameter");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//img[@alt='create new']");
        selenium.waitForPageToLoad("30000");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        assertEquals("", selenium.getText("methodToCall.cancel"));
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test Parameter");          
        String componentLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.coreservice.impl.component.ComponentBo!!).(((code:document.newMaintainableObject.componentCode,namespaceCode:document.newMaintainableObject.namespaceCode,))).((`document.newMaintainableObject.componentCode:code,document.newMaintainableObject.namespaceCode:namespaceCode,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString()+ "/kr/lookup.do;::::).anchor4']";
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (selenium.isElementPresent(componentLookUp)) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        selenium.select("//select[@id='document.newMaintainableObject.namespaceCode']", "label=KR-NS - Kuali Nervous System");
        selenium.click(componentLookUp);
        selenium.waitForPageToLoad("30000");
        selenium.click("css=td.infoline > input[name=\"methodToCall.search\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("//a[@title='return valueNamespace Name=KR-NS Component=Document ']");
        selenium.waitForPageToLoad("30000");
        String parameterName = "Validation Test Parameter"+Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        selenium.type("//input[@id='document.newMaintainableObject.name']", parameterName);
        selenium.type("//textarea[@id='document.newMaintainableObject.description']", "Validation Test Parameter Description");
        selenium.select("//select[@id='document.newMaintainableObject.parameterTypeCode']", "label=Document Validation");
        selenium.click("//input[@id='document.newMaintainableObject.evaluationOperatorCodeAllowed']");
        selenium.waitForPageToLoad("30000");
        selenium.click("methodToCall.blanketApprove");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("//img[@alt='doc search']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        docId= "link=" + docId;
        
        assertTrue(selenium.isElementPresent(docId));       
        if(selenium.isElementPresent(docId)){            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }
}
