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
 *  @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigViewConfiguredPropertiesAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Configuration%20Viewer&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/ksb/ConfigViewer.do"+
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Configuration%20Viewer&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/ksb/ConfigViewer.do";


    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Configuration Viewer
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Configuration Viewer";
    }

    public void testConfigViewConfiguredPropertiesBookmark(JiraAwareFailable failable) throws Exception {
        testConfigViewConfiguredProperties();
        passed();
    }

    public void testConfigViewConfiguredPropertiesNav(JiraAwareFailable failable) throws Exception {
        testConfigViewConfiguredProperties();
        passed();
    }    
    
    public void testConfigViewConfiguredProperties() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByLinkText("Refresh Page");
    }
}
