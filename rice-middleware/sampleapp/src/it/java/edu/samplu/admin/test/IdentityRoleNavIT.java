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
package edu.samplu.admin.test;

import org.junit.Test;

/**
 * tests creating and cancelling new and edit Role maintenance screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityRoleNavIT extends AdminTmplMthdSTNavBase {

    @Override
    public String getLinkLocator() {
        return "Role";
    }

    @Test
    @Override
    public void testEditCancel() throws Exception {
        super.testEditCancel();
    }

    @Test
    @Override
    public void testCreateNewCancel() throws Exception {
        gotoMenuLinkLocator();
        super.testCreateNewSearchReturnValueCancelConfirmation();
    }
}
