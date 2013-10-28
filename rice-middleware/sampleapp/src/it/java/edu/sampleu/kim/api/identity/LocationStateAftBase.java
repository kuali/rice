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
package edu.sampleu.kim.api.identity;

import edu.sampleu.admin.AdminTmplMthdAftNavBase;
import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LocationStateAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=State&channelUrl=" 
     * + WebDriverUtil.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.location.impl.state.StateBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=State&channelUrl="
            + WebDriverUtil.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.location.impl.state.StateBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS;

    /**
     * {@inheritDoc}
     * State
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "State";
    }
   
    public void testLocationStateBookmark(Failable failable) throws Exception {
        testSearchEditCancel();
        driver.navigate().to(WebDriverUtil.getBaseUrlString() + BOOKMARK_URL);
        testCreateNewCancel();
        passed();
    }

    public void testLocationStateNav(Failable failable) throws Exception {
        testEditCancel();
        navigate();
        testCreateNewCancelNav();
        passed();
    }
}
