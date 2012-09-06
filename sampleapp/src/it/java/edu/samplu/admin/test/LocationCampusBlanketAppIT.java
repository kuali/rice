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

/**
 * tests that user 'admin', on blanket approving a new Campus maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationCampusBlanketAppIT extends AdminMenuBlanketAppITBase {
    
    @Override
    protected String getLinkLocator() {
        return "link=Campus";
    }

   @Override
   public String blanketApprove() throws Exception {
         
        ITUtil.waitForElement(selenium, AdminMenuITBase.DOC_ID_LOCATOR);
        String docId = selenium.getText(AdminMenuITBase.DOC_ID_LOCATOR); 
        selenium.type("id=document.documentHeader.documentDescription", "Validation Test Campus");
        selenium.type("id=document.newMaintainableObject.code", "VC");
        selenium.type("id=document.newMaintainableObject.name", "Validation Test Campus");
        selenium.type("id=document.newMaintainableObject.shortName", "VTC");
        selenium.select("id=document.newMaintainableObject.campusTypeCode", "label=B - BOTH");
        
        return docId;
        
    }
}
 