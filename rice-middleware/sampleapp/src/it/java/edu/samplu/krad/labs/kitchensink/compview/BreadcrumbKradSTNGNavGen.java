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
public class BreadcrumbKradSTNGNavGen extends BreadcrumbKradSTNGBase {

    @Test(groups = { "all", "fast", "default", "nav" }, description = "testBreadcrumbNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbNav() throws Exception {
        testBreadcrumbNav(this);
    }

    @Test(groups = { "all", "fast", "default", "nav" }, description = "testBreadcrumbNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbShuffledNav() throws Exception {
        testBreadcrumbShuffledNav(this);
    }

    @Test(groups = { "all", "fast", "default", "nav" }, description = "testBreadcrumbNavigateToNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbNavigateToNav() throws Exception {
        testBreadcrumbNavigateToNav(this);
    }

    @Test(groups = { "all", "fast", "default", "nav" }, description = "testBreadcrumbNavigateToShuffledNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testBreadcrumbNavigateToShuffledNav() throws Exception {
        testBreadcrumbNavigateToShuffledNav(this);
    }
}
