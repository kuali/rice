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

import edu.samplu.common.AdminMenuBlanketAppITBase;
import edu.samplu.common.AdminMenuITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.UpgradedSeleniumITBase;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * tests that user 'admin', on blanket approving a new Person maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityPersonBlanketAppIT extends AdminMenuBlanketAppITBase {
          @Override
         protected String getLinkLocator() {
             return "link=Person";
         }

         @Override
        public String blanketApprove() throws Exception {

        ITUtil.waitForElement(selenium, AdminMenuITBase.DOC_ID_LOCATOR);
        String docId = selenium.getText(AdminMenuITBase.DOC_ID_LOCATOR);
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Validation Test Person");
        selenium.type("//input[@id='document.principalName']", "principal "+ RandomStringUtils.randomAlphabetic(3).toLowerCase());
        selenium.select("newAffln.affiliationTypeCode", "label=Affiliate");
        selenium.select("newAffln.campusCode", "label=BX - BLGTN OFF CAMPUS");
        selenium.select("newAffln.campusCode", "label=BL - BLOOMINGTON");
        selenium.click("newAffln.dflt");
        selenium.click("methodToCall.addAffln.anchor");
        selenium.waitForPageToLoad("30000");
        selenium.click("tab-Contact-imageToggle");
        selenium.select("newName.namePrefix", "label=Mr");
        selenium.type("newName.firstName", "First");
        selenium.type("newName.lastName", "Last");
        selenium.select("newName.nameSuffix", "label=Mr");
        selenium.click("newName.dflt");
        selenium.click("methodToCall.addName.anchor");
        selenium.waitForPageToLoad("30000");

        return docId;
    }
    
}
 