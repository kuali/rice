/**
 * Copyright 2005-2012 The Kuali Foundation
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

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ConfigNameSpaceWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Namespace&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    /**
     * tests that a maintenance document is created for an edit operation originating from a lookup screen
     */
    @Test
    public void testEditCancel() throws Exception {
        testSearchEditCancel();
    }
    /**
     * tests that a getLinkLocator maintenance document can be cancelled
     */
    @Test
    public void testCreateNewCancel() throws Exception {
        // create new url
        open(ITUtil.getBaseUrlString() + "/kr/maintenance.do?businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&methodToCall=start");
        testCancelConfirmation();
    }
}
