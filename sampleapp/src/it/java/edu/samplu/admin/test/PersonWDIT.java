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

import java.util.List;

import org.junit.Test;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersonWDIT extends WebDriverLegacyITBase {

    String docId;
    String personName;

    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Person&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
    
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    @Test
    public void testPerson() throws Exception {
        //Create New Permission
        selectFrame("iframeportlet");
        super.waitAndCreateNew();
        List<String> params;
        params=super.testCreateNewPerson(docId, personName);
        
        //LookUp Person
        selectTopFrame();
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        params=super.testLookUpPerson(params.get(0), params.get(1));

        //Verify Person
        super.testVerifyPerson(params.get(0), params.get(1));
        
    }

}