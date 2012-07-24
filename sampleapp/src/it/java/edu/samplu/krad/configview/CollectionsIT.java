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

package edu.samplu.krad.configview;

import edu.samplu.common.UpgradedSeleniumITBase;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Selenium test that tests collections
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionsIT extends UpgradedSeleniumITBase {

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start";
    }

    /**
     * Test action column placement in table layout collections
     */
    @Test
    public void testActionColumnPlacement() throws Exception {
        // check if actions column RIGHT by default
        Assert.assertTrue(selenium.isElementPresent("//div[@id='ConfigurationTestView-collection1']//tr[2]/td[6]//button[contains(.,\"delete\")]"));
        // check if actions column is LEFT
        Assert.assertTrue(selenium.isElementPresent("//div[@id='ConfigurationTestView-collection2']//tr[2]/td[1]//button[contains(.,\"delete\")]"));
        // check if actions column is 3rd in a sub collection
        Assert.assertTrue(selenium.isElementPresent("//div[@id='ConfigurationTestView-subCollection2_line0']//tr[2]/td[3]//button[contains(.,\"delete\")]"));
    }
}
