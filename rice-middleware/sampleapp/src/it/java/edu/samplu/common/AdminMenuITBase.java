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

@Deprecated
public abstract class AdminMenuITBase extends MenuITBase {

    public static final String LABEL_KUALI_KUALI_SYSTEMS = "label=KUALI - Kuali Systems";
    public static final String LABEL_KUALI_DEFAULT = "label=KUALI : Default";
    public static final String CREATE_NEW_LOCATOR = "//img[@alt='create new']";
    public static final String ADMIN_LOCATOR = "link=Administration";
    public static final String DOC_ID_LOCATOR = "//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]";

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
        waitAndClick("methodToCall.cancel");
        waitAndClick("methodToCall.processAnswer.button0");
        waitForPageToLoad();
    }

    @Test
    /**
     * tests that a getLinkLocator maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditCancel() throws Exception {
        gotoMenuLinkLocator();
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitAndClick("link=edit");
        waitAndClick("methodToCall.cancel");
        waitAndClick("methodToCall.processAnswer.button0");
        waitForPageToLoad();
    }
}
