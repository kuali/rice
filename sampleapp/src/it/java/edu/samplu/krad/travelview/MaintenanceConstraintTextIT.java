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
package edu.samplu.krad.travelview;

import edu.samplu.common.KradMenuITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceConstraintTextIT extends KradMenuITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }
    @Test
    /**
     * Verify constraint text matches specific values
     */
    public void testVerifyConstraintText() throws Exception {
        gotoMenuLinkLocator();
        assertEquals("Must be 10 digits", selenium.getText("css=#u542_constraint_span"));
        assertEquals("Must be 10 digits", selenium.getText("css=#quickfinder2_constraint_span"));
        assertEquals("Must be 10 digits", selenium.getText("css=#u716_add_constraint_span"));
        assertEquals("* indicates required field", selenium.getText("css=#u100008_span"));
    }
}
