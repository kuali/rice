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

import static org.junit.Assert.assertTrue;

/**
 * tests that a blanket approval by user admin of a Namespace maintenance document results in a document in state FINAL
 * 
 * To Keep the BlanketAppIT tests separate MenuItBase is extended rather than AdminMenuITBase this only requires the implementation of the getMenuLinkLocator() and getCreateNewLinkLocator so there should probably be another abstract class....
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigNameSpaceBlanketAppIT extends AdminMenuBlanketAppITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Namespace";
    }

    @Override
    public String blanketApprove() throws Exception {
        String docId = waitForDocId();
        waitAndType("//input[@id='document.documentHeader.documentDescription']", "Validation Test Namespace");
        assertElementPresent("methodToCall.cancel");
        waitAndType("//input[@id='document.newMaintainableObject.code']", "VTN" + ITUtil.DTS);
        waitAndType("//input[@id='document.newMaintainableObject.name']", "Validation Test NameSpace " + ITUtil.DTS);
        waitAndType("//input[@id='document.newMaintainableObject.applicationId']", "RICE");
        waitAndClick("//input[@id='document.newMaintainableObject.active']");
        return docId;
    }
}
