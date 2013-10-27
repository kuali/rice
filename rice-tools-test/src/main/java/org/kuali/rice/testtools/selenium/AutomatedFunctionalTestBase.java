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
package org.kuali.rice.testtools.selenium;

import org.junit.runner.RunWith;

/**
 * Automated Functional Tests should extend this Base class or have it in their class hierarchy, enables
 * bookmark mode for test methods ending in Bookmark and navigation mode for test methods ending in Nav.
 *
 * The abstract method getBookmarkUrl should be implemented to return the Bookmark URL
 * of the page under test.  The abstract method navigate should be implemented to Navigate
 * through the UI to the page under test.  {@see #navigateInternal} should be called from a setUp.
 *
 * Runs With {@see AutomatedFunctionalTestRunner}.
 */
@RunWith(AutomatedFunctionalTestRunner.class)
public abstract class AutomatedFunctionalTestBase {

    protected String testUrl;

    protected boolean shouldNavigate = false;

    protected abstract String getBookmarkUrl();

    protected abstract String getNavigationUrl();

    protected abstract void navigate() throws Exception;

    protected void enableBookmarkMode() {
        this.shouldNavigate = false;
        this.testUrl = getBookmarkUrl();
    }

    protected void enableNavigationMode() {
        this.shouldNavigate = true;
        this.testUrl = getNavigationUrl();
    }

    protected String getTestUrl() {
        return testUrl;
    }

    protected void navigateInternal() throws Exception {
        if (this.shouldNavigate) {
            navigate();
        }
    }
}