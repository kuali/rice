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

package edu.samplu.krad.demo.uif.library.fields;

import org.junit.Test;
import org.testng.annotations.Parameters;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryDataFieldSTNGBkMrkGen extends DemoLibraryDataFieldSTJUnitBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @org.testng.annotations.Test(groups = { "all", "fast", "default", "bookmark" }, description = "testDataFieldBookmark")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testDataFieldBookmark() throws Exception {
        testDataFieldBookmark(this);
    }
}
