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

import edu.samplu.common.AdminMenuBlanketAppITBase;
import edu.samplu.common.AdminMenuITBase;
import edu.samplu.common.ITUtil;

import java.util.Calendar;
import static org.junit.Assert.assertEquals;

/**
 * tests that user admin's blanket approve of the Parameter maintenance document results in a final document
 * The component is chosen from a lookup screen
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ConfigParameterBlanketAppIT extends AdminMenuBlanketAppITBase {
    @Override
    protected String getLinkLocator() {
        return "link=Parameter";
    }

    @Override
    public String blanketApprove() throws Exception {
 
        ITUtil.waitForElement(selenium, AdminMenuITBase.DOC_ID_LOCATOR);
        String docId = selenium.getText(AdminMenuITBase.DOC_ID_LOCATOR);
        assertEquals("", selenium.getText("methodToCall.cancel"));              
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test Parameter ");
        selenium.select("//select[@id='document.newMaintainableObject.namespaceCode']", "label=KR-NS - Kuali Nervous System");
        
        String componentLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.coreservice.impl.component.ComponentBo!!).(((code:document.newMaintainableObject.componentCode,namespaceCode:document.newMaintainableObject.namespaceCode,))).((`document.newMaintainableObject.componentCode:code,document.newMaintainableObject.namespaceCode:namespaceCode,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString()+ "/kr/lookup.do;::::).anchor4']";
        ITUtil.waitForElement(selenium, componentLookUp);
        
        selenium.click(componentLookUp);
        selenium.waitForPageToLoad("30000");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        
        String parameterName = "Validation Test Parameter"+ITUtil.DTS;
        selenium.type("//input[@id='document.newMaintainableObject.name']", parameterName);
        selenium.type("//textarea[@id='document.newMaintainableObject.description']", "Validation Test Parameter Description" + ITUtil.DTS);
        selenium.select("//select[@id='document.newMaintainableObject.parameterTypeCode']", "label=Document Validation");
        selenium.click("//input[@id='document.newMaintainableObject.evaluationOperatorCodeAllowed']");

        selenium.waitForPageToLoad("30000");

        return docId;
    }    
    
}
