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
package edu.samplu.krad.labs.sessionPolicy;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import edu.samplu.common.WebDriverUtil;
import org.junit.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsSessionPolicyRedirectHomeSTNGGen extends WebDriverLegacyITBase {

    /**
     * Ajax Request
     */
    public static final String AJAX_REQUEST_BUTTON_TEXT = "Ajax Request";

    /**
     * /kr-krad/sessionPolicy?viewId=Lab-SessionPolicy-RedirectHome
     */
    public static final String BOOKMARK_URL = "/kr-krad/sessionPolicy?viewId=Lab-SessionPolicy-RedirectHome";

    /**
     * Kill Session
     */
    public static final String KILL_SESSION_BUTTON_TEXT = "Kill Session";

    /**
     * KradSampleAppHome
     */
    public static final String KRAD_SAMPLE_APP_HOME_URL_PART = "KradSampleAppHome";

    /**
     * Login
     */
    public static final String LOGIN_TEXT = "Login";

    /**
     * NonAjax Request
     */
    public static final String NON_AJAX_REQUEST_BUTTON_TEXT = "NonAjax Request";

    /**
     * Session Timeout - Redirect to Home
     */
    public static final String SESSION_TIMEOUT_REDIRECT_TO_HOME_HEADER_TEXT = "Session Timeout - Redirect to Home";

    /**
     * uif-headerText
     */
    public static final String UIF_HEADER_TEXT_CLASS_NAME = "uif-headerText";

    /**
     * UserName:
     */
    public static final String USER_NAME_TEXT = "UserName:";

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testNonAjaxAjaxBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testNonAjaxAjaxBookmark() throws Exception {
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(AJAX_REQUEST_BUTTON_TEXT);
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(NON_AJAX_REQUEST_BUTTON_TEXT);
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(KILL_SESSION_BUTTON_TEXT);
        waitAndClickButtonByText(NON_AJAX_REQUEST_BUTTON_TEXT);
        assertHomeRedirect();

        driver.navigate().to(ITUtil.getBaseUrlString() + BOOKMARK_URL);
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(KILL_SESSION_BUTTON_TEXT);
        waitAndClickButtonByText(AJAX_REQUEST_BUTTON_TEXT);
        assertHomeRedirect();

        passed();
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testAjaxNonAjaxBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testAjaxNonAjaxBookmark() throws Exception {
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(NON_AJAX_REQUEST_BUTTON_TEXT);
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(AJAX_REQUEST_BUTTON_TEXT);
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(KILL_SESSION_BUTTON_TEXT);
        waitAndClickButtonByText(AJAX_REQUEST_BUTTON_TEXT);
        assertHomeRedirect();

        driver.navigate().to(ITUtil.getBaseUrlString() + BOOKMARK_URL);
        assertHeaderTextRedirectHome();

        waitAndClickButtonByText(KILL_SESSION_BUTTON_TEXT);
        waitAndClickButtonByText(NON_AJAX_REQUEST_BUTTON_TEXT);
        assertHomeRedirect();

        passed();
    }

    protected void assertHomeRedirect() throws InterruptedException {
        waitForElementPresentByClassName(UIF_HEADER_TEXT_CLASS_NAME);
        assertTextPresent(LOGIN_TEXT);
        assertTextPresent(USER_NAME_TEXT);
        WebDriverUtil.kradLogin(driver, user, this);
        driver.getCurrentUrl().contains(KRAD_SAMPLE_APP_HOME_URL_PART);
    }

    protected void assertHeaderTextRedirectHome() throws InterruptedException {
        waitForElementPresentByClassName(UIF_HEADER_TEXT_CLASS_NAME);
        assertTextPresent(SESSION_TIMEOUT_REDIRECT_TO_HOME_HEADER_TEXT);
    }
}
