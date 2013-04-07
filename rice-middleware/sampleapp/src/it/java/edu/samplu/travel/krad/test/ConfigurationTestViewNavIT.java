/*
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

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

/**
 * test that configuration test view items work as expected
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigurationTestViewNavIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    /** bean id prefix in used in view */
    private String idPrefix = "ConfigurationTestView-ProgressiveRender-";

    /** bean id suffix for add line controls */
    String addLineIdSuffix = "InputField_add_control";

    /**
     * open the configuration test view page
     */
    protected void openConfigurationTestView() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath("(//a[text()='Configuration Test View'])[2]");
        switchToWindow(CONFIGURATION_VIEW_WINDOW_TITLE);
        waitForTitleToEqualKualiPortalIndex();
    }
    
    	
    /**
     * test for text input field label - style setting and refreshWhenChanged for components not in collection
     */
    @Test
	public void testConfigurationTestView() throws Exception {
        openConfigurationTestView();
        super.testConfigurationTestView(idPrefix);
        passed();
	}

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    @Test    
    public void testAddLineWithSpecificTime() throws Exception{
        openConfigurationTestView();
        super.testAddLineWithSpecificTime(idPrefix, addLineIdSuffix);
        passed();
    }

    /**
     * test adding a line to a collection which has the property refreshWhenChangedPropertyNames set
     * on more than one component.
     */
    @Test
    public void testAddLineWithAllDay() throws Exception {
        openConfigurationTestView();
        super.testAddLineWithAllDay(idPrefix, addLineIdSuffix);
        passed();
    }

    /**
     * test adding a line to a collection which uses an add line that has spring expressions that are evaluated on refresh
     * a specific time is set
     */
    @Test  
    public void testAddLineAllDay() throws Exception{
        openConfigurationTestView();
        super.testAddLineAllDay(idPrefix, addLineIdSuffix);
        passed();
    }
}
