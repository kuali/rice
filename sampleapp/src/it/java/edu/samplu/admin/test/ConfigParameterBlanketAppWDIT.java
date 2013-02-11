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

import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * tests that user admin's blanket approve of the Parameter maintenance document results in a final document
 * The component is chosen from a lookup screen
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ConfigParameterBlanketAppWDIT extends WebDriverLegacyITBase {

    public static final String TEST_URL=ITUtil.PORTAL+"?channelTitle=Parameter&channelUrl="+ITUtil.getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation="+ITUtil.PORTAL_URL+"&hideReturnLink=true";
    
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.WebDriverLegacyITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        // TODO dmoteria - THIS METHOD NEEDS JAVADOCS
        return TEST_URL;
    }

    @Test
    public void testConfigParamaterBlanketApprove() throws Exception {
        super.testConfigParamaterBlanketApprove();
    }

}
