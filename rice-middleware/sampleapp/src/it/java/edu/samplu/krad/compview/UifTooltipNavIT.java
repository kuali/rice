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
package edu.samplu.krad.compview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import junit.framework.Assert;
import org.junit.Test;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

/**
 * Selenium test that tests that tooltips are rendered on mouse over and focus events and hidden on
 * mouse out and blur events
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifTooltipNavIT extends WebDriverLegacyITBase {
    private static final String NAME_FIELD_1 = "field1";
    private static final String NAME_FIELD_2 = "field2";
    @Override
    public String getTestUrl() {
        // open Other Examples page in kitchen sink view
        return ITUtil.PORTAL;
    }

    @Test
    public void testTooltip() throws Exception {
        waitAndClickKRAD();
        waitAndClickByXpath(KITCHEN_SINK_XPATH);
        switchToWindow(KUALI_UIF_COMPONENTS_WINDOW_XPATH);
        waitAndClickByLinkText("Other Examples");
        super.testUifTooltip(NAME_FIELD_1, NAME_FIELD_2);
    }
}
