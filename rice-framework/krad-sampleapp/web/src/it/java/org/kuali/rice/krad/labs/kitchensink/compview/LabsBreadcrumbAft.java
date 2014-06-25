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
package org.kuali.rice.krad.labs.kitchensink.compview;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.breadcrumb.BreadcrumbAftBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsBreadcrumbAft extends BreadcrumbAftBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView";

    public static final String DOWN_TRIANGLE_XPATH = "(//a[@class='uif-breadcrumbSiblingLink'])";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Kitchen Sink");
    }

    @Override
    protected String getTriangleXpath() {
        return this.DOWN_TRIANGLE_XPATH;
    }

    @Test
    @Override
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testBreadcrumbBookmark() throws Exception {
        testBreadcrumbs();
        passed();
    }

    @Test
    @Override
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testBreadcrumbShuffledBookmark() throws Exception {
        testBreadcrumbsShuffled();
        passed();
    }
}
