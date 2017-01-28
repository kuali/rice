/**
 * Copyright 2005-2017 The Kuali Foundation
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
package edu.sampleu.main;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermLookUpAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Term%20Lookup&channelUrl="
     *  + WebDriverUtils.getBaseUrlString() + ITUtil.KRAD_LOOKUP_METHOD
     *  + "org.kuali.rice.krms.impl.repository.TermBo"
     *  + ITUtil.SHOW_MAINTENANCE_LINKS
     *  + "&returnLocation=" + ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Term%20Lookup&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
            + "org.kuali.rice.krms.impl.repository.TermBo"
            + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS
            + "&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }
    /**
     * {@inheritDoc}
     * Term Lookup
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Term Lookup";
    }

    public void testTermLookUpBookmark(JiraAwareFailable failable) throws Exception {
        testTermLookUp();
        passed();
    }
    public void testTermLookUpNav(JiraAwareFailable failable) throws Exception {
        testTermLookUp();
        passed();
    }
}
