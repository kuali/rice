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
package edu.samplu.admin.test;

import org.junit.Test;

/**
 * JUnit implementation of ComponentSTJUnitBase that goes directly to the page under test by a bookmarkable url,
 * avoiding navigation.  In the future the idea is to generate this class using the test methods
 * from ComponentAbstractSmokeTestBase and following the simple pattern of <pre>super.testTestMethod(this);</pre>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentSTJUnitBkMrkGen extends ComponentSTJUnitBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void gotoTest() throws Exception {
        bookmark();
    }

    /**
     * @link ComponentAbstractSmokeTestBase#testCreateNewCancelComponent
     * @throws Exception
     */
    @Test
    public void testCreateNewCancelComponentBookmark() throws Exception {
        testComponentCreateNewCancelBookmark(this);
    }

    /**
     * @link ComponentAbstractSmokeTestBase#testComponentParameter
     * @throws Exception
     */
    @Test
    public void testComponentParameterBookmark() throws Exception {
        testComponentParameterBookmark(this);
    }
}
