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

import org.junit.Test;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigurationTestViewAbstractSmokeTestBase extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView_KNS&methodToCall=start";
     */
    public static final String BOOKMARK_URL = "/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView_KNS&methodToCall=start";
    
    /** bean id prefix in used in view */
    private String idPrefix = "ConfigurationTestView-ProgressiveRender-";

    /** bean id suffix for add line controls */
    String addLineIdSuffix = "InputField_add_control";


    protected void bookmark() {
        open(ITUtil.getBaseUrlString() + BOOKMARK_URL);
    }

    /**
     * Nav tests start at {@link edu.samplu.common.ITUtil#PORTAL}.
     * Bookmark Tests should override and return {@link edu.samplu.travel.krad.test.ConfigurationTestViewAbstractSmokeTestBase#BOOKMARK_URL}
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath("(//a[text()='Configuration Test View'])[2]");
        switchToWindow(CONFIGURATION_VIEW_WINDOW_TITLE);
        waitForTitleToEqualKualiPortalIndex();   
    }

    protected void testConfigurationTestViewNav(Failable failable) throws Exception {     
        navigation();
        testConfigurationTestView();
        navigation();
        testAddLineWithSpecificTime();
        navigation();
        testAddLineWithAllDay();
        navigation();
        testAddLineAllDay();
    }

    protected void testConfigurationTestViewBookmark(Failable failable) throws Exception {
        testConfigurationTestView();
        testAddLineWithSpecificTime();
        testAddLineWithAllDay();
        testAddLineAllDay();
        passed();
    }    
    
    /**
     * test for text input field label - style setting and refreshWhenChanged for components not in collection
     */
    public void testConfigurationTestView() throws Exception {
        testConfigurationTestView(idPrefix);
        passed();
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    public void testAddLineWithSpecificTime() throws Exception{
        testAddLineWithSpecificTime(idPrefix, addLineIdSuffix);
        passed();
    }

    /**
     * test adding a line to a collection which has the property refreshWhenChangedPropertyNames set
     * on more than one component.
     */
    public void testAddLineWithAllDay() throws Exception {
        testAddLineWithAllDay(idPrefix, addLineIdSuffix);
        passed();
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    public void testAddLineAllDay() throws Exception{
        testAddLineAllDay(idPrefix, addLineIdSuffix);
        passed();
    }
}
