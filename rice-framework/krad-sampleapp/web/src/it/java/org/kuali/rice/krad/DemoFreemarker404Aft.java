/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad;

import org.junit.Ignore;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoFreemarker404Aft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/lab
     */
    public static final String BAD_BOOKMARK_URL = "/kr-krad/lab";
    
    @Override
    public String getBookmarkUrl() {
        return BAD_BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	//No need for navigation as we just need to test Freemarker Error on BAD URL.
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12268
    public void testLookUpConditionalCriteriaBookmark() throws Exception {
        checkForIncidentReport();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12268
    public void testLookUpConditionalCriteriaNav() throws Exception {
        checkForIncidentReport();
    }
}
