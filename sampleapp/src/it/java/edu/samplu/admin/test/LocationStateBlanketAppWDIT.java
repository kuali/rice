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

import edu.samplu.common.AdminMenuBlanketAppLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * tests that user 'admin', on blanket approving a new State maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LocationStateBlanketAppWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=State&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.location.impl.state.StateBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
   
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    @Test
   public void testLocationStateBlanketApprove() throws Exception {
      super.testLocationStateBlanketApprove();     
    }
}
 