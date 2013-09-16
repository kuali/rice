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
package edu.samplu.krad.library.containers;

import org.junit.Test;

import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.SmokeTestBase;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryContainerLinkGroupSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-LinkGroup-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-LinkGroup-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Link Group");
    }

    protected void testLibraryContainerLinkGroup() throws Exception {
        waitAndClickByLinkText("Link 1");
        alertAccept();
        assertTextPresent("|");
        waitAndClickByLinkText("Link 2");
        alertAccept();
        waitAndClickByLinkText("Link 3");
        alertAccept();
    }
    
    @Test
    public void testContainerLinkGroupBookmark() throws Exception {
        testLibraryContainerLinkGroup();
        passed();
    }

    @Test
    public void testContainerLinkGroupNav() throws Exception {
        testLibraryContainerLinkGroup();
        passed();
    }  
}