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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class LabsParameterUpdaterAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/parameter?viewId=LabsParameterView
     */
    public static final String BOOKMARK_URL = "/kr-krad/parameter?viewId=LabsParameterView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Parameter Updater");
    }

    @Test
    public void testParameterUpdaterBookmark() throws Exception {
        testParameterUpdater();
        passed();
    }

    @Test
    public void testParameterUpdaterNav() throws Exception {
        testParameterUpdater();
        passed();
    }

    protected void testParameterUpdater()throws Exception {
    	waitAndTypeByName("namespaceCode","IAT");
    	waitAndTypeByName("componentCode","IAT");
    	waitAndTypeByName("parameterName","IAT");
    	waitAndTypeByName("parameterValue","IAT");
    	waitAndClickButtonByText("Update Parameter");
    }
}
