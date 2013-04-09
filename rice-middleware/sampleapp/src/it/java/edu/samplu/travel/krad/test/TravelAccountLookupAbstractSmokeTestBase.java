/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.travel.krad.test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import java.util.List;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class TravelAccountLookupAbstractSmokeTestBase extends WebDriverLegacyITBase {

    public static final String BOOKMARK_URL = ITUtil.PORTAL + "?channelTitle=Travel Account&channelUrl=" + ITUtil.getBaseUrlString() +
            "/krad/lookup?methodToCall=start&businessObjectClassName=edu.sampleu.travel.bo.TravelAccount&lookupCriteria['number']=a*&readOnlyFields=number&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";


    protected void bookmark() {
        open(ITUtil.getBaseUrlString() + BOOKMARK_URL);
    }

    /**
     * Bookmark tests should call bookmark(), navigation tests should call navigation()
     * @throws Exception
     */
    protected abstract void gotoTest() throws Exception;

    protected void navigtaion() throws InterruptedException {
    }


}
