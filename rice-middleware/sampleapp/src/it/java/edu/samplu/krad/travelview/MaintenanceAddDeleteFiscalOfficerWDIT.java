/*
 * Copyright 2006-2012 The Kuali Foundation
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
package edu.samplu.krad.travelview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceAddDeleteFiscalOfficerWDIT extends WebDriverLegacyITBase {
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Travel%20Account%20Maintenance%20(New)&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&viewName=ks_maintenance&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    @Override
    public String getTestUrl() {
        return TEST_URL;
    }

    @Test
    /**
     * Verify a fiscal officer line can be added and deleted
     */
    public void testVerifyAddDeleteFiscalOfficerLegacy() throws Exception {
        super.testVerifyAddDeleteFiscalOfficerLegacy();
    }

}
