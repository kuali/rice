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
import edu.samplu.common.ITUtil;
import org.apache.commons.lang.RandomStringUtils;

import static org.junit.Assert.assertTrue;

/**
 * tests that user 'admin', on blanket approving a new Country maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationCountryBlanketAppIT extends AdminMenuBlanketAppITBase {

    @Override
    public String getLinkLocator() {
        return "link=Country";
    }

    @Override
    public String blanketApprove() {
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        String countryName = "Validation Test Country " + ITUtil.DTS;
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        selenium.type("//input[@id='document.documentHeader.documentDescription']", countryName);
        selenium.type("//input[@id='document.newMaintainableObject.code']",RandomStringUtils.randomAlphabetic(2).toUpperCase());
       
        selenium.type("//input[@id='document.newMaintainableObject.name']", countryName);
        selenium.type("//input[@id='document.newMaintainableObject.alternateCode']", "VTC");
        return docId;
    }

}
 