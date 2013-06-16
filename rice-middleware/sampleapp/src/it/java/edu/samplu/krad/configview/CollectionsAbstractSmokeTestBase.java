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
package edu.samplu.krad.configview;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class CollectionsAbstractSmokeTestBase extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start";

    /**
     * (//a[contains(text(),'Collections Configuration Test View')])[2]
     */
    public static final String TEXT_COLLECTIONS_CONFIGURATION_TEST_VIEW_XPATH =
            "(//a[contains(text(),'Collections Configuration Test View')])[2]";

    /**
     * Kuali :: Collection Test View
     */
    public static final String KUALI_COLLECTION_WINDOW_TITLE = "Kuali :: Collection Test View";
    
    /**
     * Nav tests start at {@link edu.samplu.common.ITUtil#PORTAL}.  Bookmark Tests should override and return {@link CollectionsAbstractSmokeTestBase#BOOKMARK_URL}
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    protected void navigation() throws Exception {
        waitAndClickKRAD();
        waitAndClickByXpath(TEXT_COLLECTIONS_CONFIGURATION_TEST_VIEW_XPATH);
        switchToWindow(KUALI_COLLECTION_WINDOW_TITLE);
    }

    protected void testCollectionsNav(Failable failable) throws Exception {
        navigation();
        testDefaultTestsTableLayout();
        navigation();
        testAddBlankLine();
        navigation();
        testActionColumnPlacement();
        navigation();
        testAddViaLightbox();
        navigation();
        testColumnSequence();
        navigation();
        testSequencerow();
        passed();
    }

    protected void testCollectionsBookmark(Failable failable) throws Exception {
        testDefaultTestsTableLayout();
        testAddBlankLine();
        testActionColumnPlacement();
        testAddViaLightbox();
        testColumnSequence();
        testSequencerow();
        passed();
    }
}
