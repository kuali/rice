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
package edu.samplu.mainmenu.test;

import org.junit.Test;

import edu.samplu.common.MainMenuLookupLegacyITBase;

/**
 * tests that user 'admin' can display the Term lookup screen, search,
 * initiate an Term maintenance document via an edit action on the search results and
 * finally cancel the maintenance document
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermLookUpNavIT extends MainMenuLookupLegacyITBase {
    @Override
    public void testLookUp() {} // no-op to avoid https://jira.kuali.org/browse/KULRICE-9047 messing up the server state

    @Override
    public String getLinkLocator() {
        return "Term Lookup";
    }

    @Test
    public void lookupAssertions() throws Exception{
        gotoMenuLinkLocator();
        super.testTermLookUp();
    }
}