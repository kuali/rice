/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.kuali.rice.krad.demo.uif.library.LibraryBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class LibraryNavigationBase extends LibraryBase {

    protected void assertNavigationView(String linkText, String supportTitleText) throws Exception {
        waitAndClickByLinkText(linkText);
        waitForElementPresentByClassName("uif-viewHeader-supportTitle", "LibraryNavigationBase");
        waitForProgressLoading();
        assertTrue("LibraryNavigationBase", getTextByClassName("uif-viewHeader-supportTitle").contains(supportTitleText));
    }


    protected void testNavigationView() throws Exception {
        assertNavigationView("Page 2", "Test Course 2");
        assertNavigationView("Page 3", "Test Course 3");
        assertNavigationView("Page 1", "Test Course 1");
    }
}
