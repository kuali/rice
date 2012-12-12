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
        String docId = waitForDocId();
        waitAndType("//input[@id='document.documentHeader.documentDescription']", "Validation Test Person");
        waitAndType("//input[@id='document.principalName']", "principal "+ RandomStringUtils.randomAlphabetic(3).toLowerCase());
        select("newAffln.affiliationTypeCode", "label=Affiliate");
        select("newAffln.campusCode", "label=BX - BLGTN OFF CAMPUS");
        select("newAffln.campusCode", "label=BL - BLOOMINGTON");
        waitAndClick("newAffln.dflt");
        waitAndClick("methodToCall.addAffln.anchor");
        waitAndClick("tab-Contact-imageToggle");
        select("newName.namePrefix", "label=Mr");
        waitAndType("newName.firstName", "First");
        waitAndType("newName.lastName", "Last");
        select("newName.nameSuffix", "label=Mr");
        waitAndClick("newName.dflt");
        waitAndClick("methodToCall.addName.anchor");
        waitForPageToLoad();
        return docId;
    }
}
 