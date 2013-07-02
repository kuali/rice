/*
 * Copyright 2006-2013 The Kuali Foundation
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
package edu.samplu.krad.demo.uif.library.controls.checkbox;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryControlCheckboxDefaultSTNGNavGen extends DemoLibraryControlCheckboxDefaultSTNGBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDefaultNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDefaultNav() throws Exception {
        testCheckboxControlDefaultNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlOptionsFinderNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlOptionsFinderNav() throws Exception {
        testCheckboxControlOptionsFinderNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlKeyValuePairNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlKeyValuePairNav() throws Exception {
        testCheckboxControlKeyValuePairNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDisabledNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDisabledNav() throws Exception {
        testCheckboxControlDisabledNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDelimiterNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDelimiterNav() throws Exception {
        testCheckboxControlDelimiterNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDisableOnKeyEventNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDisableOnKeyEventNav() throws Exception {
        testCheckboxControlDisabledOnKeyEventNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlEnableWhenChangedNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlEnableWhenChangedNav() throws Exception {
        testCheckboxControlEnableWhenChangedNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDisableWhenChangedNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDisableWhenChangedNav() throws Exception {
        testCheckboxControlDisableWhenChangedNav(this);
    }
}
