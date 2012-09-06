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
import org.apache.commons.lang.RandomStringUtils;

/**
 * tests that user 'admin', on blanket approving a new Postal Code maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationPostCodeBlanketAppIT extends AdminMenuBlanketAppITBase {
    
    @Override
    protected String getLinkLocator() {
        return "link=Postal Code";
    }

   @Override
   public String blanketApprove() throws Exception {
         
        ITUtil.waitForElement(selenium, AdminMenuITBase.DOC_ID_LOCATOR);
        String docId = selenium.getText(AdminMenuITBase.DOC_ID_LOCATOR);
        
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test Postal Code");        
        
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString()+ "/kr/lookup.do;::::).anchor4']";
        ITUtil.waitAndClick(selenium, countryLookUp);
        selenium.waitForPageToLoad("30000");
        selenium.type("code", "US");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        ITUtil.waitAndClick(selenium, "link=return value");
        selenium.waitForPageToLoad("30000");
        String code = RandomStringUtils.randomNumeric(5);
        selenium.type("//input[@id='document.newMaintainableObject.code']", code);
        
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        ITUtil.waitAndClick(selenium, stateLookUp);
        ITUtil.waitAndClick(selenium, "//input[@name='methodToCall.search' and @value='search']");
        ITUtil.waitAndClick(selenium, "//table[@id='row']/tbody/tr[4]/td[1]/a");
        selenium.waitForPageToLoad("30000");
        String cityName = "Validation Test Postal Code "+code;
        
        selenium.type("//input[@id='document.newMaintainableObject.cityName']", cityName);
        
        return docId;  
    }
}
 