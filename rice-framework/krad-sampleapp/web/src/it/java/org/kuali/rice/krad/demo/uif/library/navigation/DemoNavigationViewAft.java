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
package org.kuali.rice.krad.demo.uif.library.navigation;

import org.junit.Test;

/**
 * NOTE: The BOOKMARK_VIEW_URL is different than in DemoNavigationAft
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoNavigationViewAft extends DemoLibraryNavigationBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-NavigationGroup-NavigationView&methodToCall=start
     */
    public static final String BOOKMARK_VIEW_URL = "/kr-krad/kradsampleapp?viewId=Demo-NavigationGroup-NavigationView&methodToCall=start&pageId=Demo-NavigationGroup-Page1";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_VIEW_URL;
    }

    protected void navigate() throws Exception {
        // no-op navigation test is done in DemoNavigationAft
    }

    @Test
    public void testNavigationViewBookmark() throws Exception {
        testNavigationView();
        passed();
    }
}
