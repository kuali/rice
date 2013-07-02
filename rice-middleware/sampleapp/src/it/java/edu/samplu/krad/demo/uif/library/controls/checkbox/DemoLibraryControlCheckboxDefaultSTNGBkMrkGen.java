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
public class DemoLibraryControlCheckboxDefaultSTNGBkMrkGen extends DemoLibraryControlCheckboxDefaultSTNGBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDefaultBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDefaultBookmark() throws Exception {
        testCheckboxControlDefaultBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlOptionsFinderBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlOptionsFinderBookmark() throws Exception {
        testCheckboxControlOptionsFinderBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlKeyValuePairBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlKeyValuePairBookmark() throws Exception {
        testCheckboxControlKeyValuePairBookmark(this);
    }
    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDisabledBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDisabledBookmark() throws Exception {
        testCheckboxControlDisabledBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDelimiterBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDelimiterBookmark() throws Exception {
        testCheckboxControlDelimiterBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDisableOnKeyEventBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDisabledOnKeyEventBookmark() throws Exception {
        testCheckboxControlDisabledOnKeyEventBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlEnableWhenChangedBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlEnableWhenChangedBookmark() throws Exception {
        testCheckboxControlEnableWhenChangedBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testCheckboxControlDisableWhenChangedBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testCheckboxControlDisableWhenChangedBookmark() throws Exception {
        testCheckboxControlDisableWhenChangedBookmark(this);
    }
}
