/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.common;

import org.junit.Test;

/**
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class AdminMenuLegacyITBase extends MenuLegacyITBase {

    public static final String CREATE_NEW_LOCATOR = "a[title='Create a new record']";
    public static final String ADMIN_LOCATOR = "Administration";

    @Override
    protected String getCreateNewLinkLocator() {
        return CREATE_NEW_LOCATOR;
    }

    @Override
    protected String getMenuLinkLocator() {
        return ADMIN_LOCATOR;
    }

    @Test
    /**
     * tests that a getLinkLocator maintenance document can be cancelled
     */
    public void testCreateNewCancel() throws Exception {
        gotoCreateNew();
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
    }

    @Test
    /**
     * tests that a getLinkLocator maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditCancel() throws Exception {
        gotoMenuLinkLocator();
        waitAndClick("input[alt='search']");
        waitAndClickByLinkText("edit");
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
    }
}
