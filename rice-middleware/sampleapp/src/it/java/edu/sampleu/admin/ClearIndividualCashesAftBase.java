/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.admin;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ClearIndividualCashesAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Cache%20Admin&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr-krad/core/admin/cache?viewId=CacheAdmin-view1&methodToCall=start"+    
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Cache%20Admin&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr-krad/core/admin/cache?viewId=CacheAdmin-view1&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Cache Admin
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Cache Admin";
    }

    public void testClearIndividualCashesBookmark(JiraAwareFailable failable) throws Exception {
        testClearIndividualCashes();
        passed();
    }

    public void testClearIndividualCashesNav(JiraAwareFailable failable) throws Exception {
        testClearIndividualCashes();
        passed();
    }    
    
    public void testClearIndividualCashes() throws Exception
    {
        selectFrameIframePortlet();
        waitAndClickByXpath("//ul[@class='jstree-no-icons']/li[4]/ins");
        waitAndClickByXpath("//ul[@class='jstree-no-icons']/li[4]/ul/li[15]/a/ins");
        waitAndClickButtonByText("Flush");
        Thread.sleep(10000);
        assertTextPresent("Cache: http://rice.kuali.org/krms/v2_0/TermSpecificationType was flushed for the CacheManager: krmsDistributedCacheManager.");
        assertTextPresent("Cache Management");
    }
}
