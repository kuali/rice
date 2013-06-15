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
package edu.samplu.krad.labs.kitchensink.compview;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BreadcrumbKradSTNGBkMrkGen extends BreadcrumbKradSTNGBase {

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testBreadcrumbBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbBookmark() throws Exception {
        testBreadcrumbBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testBreadcrumbShuffledBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbShuffledBookmark() throws Exception {
        testBreadcrumbShuffledBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testBreadcrumbNavigateToBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbNavigateToBookmark() throws Exception {
        testBreadcrumbNavigateToBookmark(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testBreadcrumbNavigateToShuffledBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbNavigateToShuffledBookmark() throws Exception {
        testBreadcrumbNavigateToShuffledBookmark(this);
    }
}