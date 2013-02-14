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
import java.util.List;

/**
 * tests the Parameter section in Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParameterWDIT extends WebDriverLegacyITBase {
   
    String docId;
    String parameterName;
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Parameter&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
    
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    @Test
    public void testParameter() throws Exception {
        
        // Create New
        selectFrame("iframeportlet");
        super.waitAndCreateNew();
        List<String> params;
        params=super.testCreateNewParameter(docId, parameterName);
       
        //Lookup
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        params=super.testLookUpParameter(params.get(0), params.get(1));
   
        //edit
        params=super.testEditParameter(params.get(0), params.get(1));
        
        //Verify if its edited
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        params=super.testVerifyEditedParameter(params.get(0), params.get(1));
  
        //copy
        params=super.testCopyParameter(params.get(0), params.get(1));
        
        //Verify if its copied
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        super.testVerifyCopyParameter(params.get(0), params.get(1));
    }

    
}
