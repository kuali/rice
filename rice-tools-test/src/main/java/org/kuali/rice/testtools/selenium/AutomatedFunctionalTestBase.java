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
package org.kuali.rice.testtools.selenium;

import org.junit.runner.RunWith;

/**
 * <p>
 * Automated Functional Tests should extend this Base class or have it in their class hierarchy, enables
 * bookmark mode for test methods ending in Bookmark and navigation mode for test methods ending in Nav.
 * </p><p>
 * The abstract method getBookmarkUrl should be implemented to return the Bookmark URL
 * of the page under test.  The abstract method navigate should be implemented to Navigate
 * through the UI to the page under test.  {@see #navigateInternal} should be called from a setUp.
 * </p><p>
 * Runs With {@see AutomatedFunctionalTestRunner}.
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(AutomatedFunctionalTestRunner.class)
public abstract class AutomatedFunctionalTestBase {

    /**
     * testUrl set by {@see #enableBookmarkMode} and {@see #enableNavigationMode}, if there are test methods that
     * do not end with Bookmark or Nav then testUrl must be defined in the test.
     */
    protected String testUrl;

    /**
     * Set to true by {@see #enableNavigationMode} and false by {@see #enableBookmarkMode}.
     */
    protected boolean shouldNavigate = false;

    /**
     * Called by {see #enableBookmarkMode}.
     *
     * @return Bookmark url where test will start.
     */
    protected abstract String getBookmarkUrl();

    /**
     * Called by {see #enableBookmarkMode}.
     *
     * @return Navigation url where test will start navigating from.
     */
    protected abstract String getNavigationUrl();

    /**
     * Called by {@see #navigateInternal}, should navigate from the testUrl.
     *
     * @throws Exception
     */
    protected abstract void navigate() throws Exception;

    /**
     * Called by {@see AutomatedFunctionalTestRunner#methodInvoker} if test method ends with Bookmark.
     */
    protected void enableBookmarkMode() {
        this.shouldNavigate = false;
        this.testUrl = getBookmarkUrl();
    }

    /**
     * Called by {@see AutomatedFunctionalTestRunner#methodInvoker} if test method ends with Nav.
     */
    protected void enableNavigationMode() {
        this.shouldNavigate = true;
        this.testUrl = getNavigationUrl();
    }

    /**
     * @return testUrl
     */
    protected String getTestUrl() {
        return testUrl;
    }

    /**
     * Calls {@see #navigate} if {@see #shouldNavigate} is true.
     *
     * @throws Exception
     */
    protected void navigateInternal() throws Exception {
        if (this.shouldNavigate) {
            navigate();
        }
    }
}