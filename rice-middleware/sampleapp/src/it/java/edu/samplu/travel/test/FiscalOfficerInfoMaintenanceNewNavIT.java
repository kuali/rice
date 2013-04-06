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
package edu.samplu.travel.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

/**
 * tests that user 'admin', can initiate, save and submit a FiscalOfficerInfo maintenance document
 * resulting in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FiscalOfficerInfoMaintenanceNewNavIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Test
    public void testUntitled() throws Exception {
        waitAndClickKRAD();
        waitAndClickByXpath("//a[@title='FiscalOfficerInfo Maintenance (New)']");
        super.testFiscalOfficerInfoMaintenanceNew();
        passed();
    }
}
