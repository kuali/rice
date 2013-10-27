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
package edu.sampleu.travel;

import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigurationTestViewAftBase extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView&methodToCall=start";
     */
    public static final String BOOKMARK_URL = "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView&methodToCall=start";
    
    /** bean id prefix in used in view */
    private String idPrefix = "ConfigurationTestView-ProgressiveRender-";

    /** bean id suffix for add line controls */
    String addLineIdSuffix = "InputField_add_control";


    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * Nav tests start at {@link org.kuali.rice.testtools.selenium.ITUtil#PORTAL}.
     * Bookmark Tests should override and return {@link ConfigurationTestViewAftBase#BOOKMARK_URL}
     * {@inheritDoc}
     * @return
     */
    @Override
    protected String getTestUrl() {
        return ITUtil.PORTAL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath("(//a[text()='Configuration Test View'])");
        switchToWindow(CONFIGURATION_VIEW_WINDOW_TITLE);
        waitForTitleToEqualKualiPortalIndex();   
    }

    protected void testConfigurationTestViewNav(Failable failable) throws Exception {     
        navigation();
        testConfigurationTestView(idPrefix);
        testAddLineWithSpecificTime(idPrefix, addLineIdSuffix);
        testAddLineWithAllDay(idPrefix, addLineIdSuffix);
        testAddLineAllDay(idPrefix, addLineIdSuffix);
        passed();
    }

    protected void testConfigurationTestViewBookmark(Failable failable) throws Exception {
        testConfigurationTestView(idPrefix);
        testAddLineWithSpecificTime(idPrefix, addLineIdSuffix);
        testAddLineWithAllDay(idPrefix, addLineIdSuffix);
        testAddLineAllDay(idPrefix, addLineIdSuffix);
        passed();
    }
}
