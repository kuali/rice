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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsAdditionalHtmlHeadLinkTagsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-HeadLink-Tags
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-HeadLink-Tags";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Html head link tags");
    }

    protected void testDemoAdditionalHtmlHeadLinkTags() throws InterruptedException {
    	waitForElementPresentByXpath("//link[@rel='shortcut icon' and @href='http://example.com/print_style.css']");
    	waitForElementPresentByXpath("//link[@rel='stylesheet' and @href='http://example.com/style.css']");
    }

    @Test
    public void testDemoAdditionalHtmlHeadLinkTagsBookmark() throws Exception {
    	testDemoAdditionalHtmlHeadLinkTags();
        passed();
    }

    @Test
    public void testDemoAdditionalHtmlHeadLinkTagsNav() throws Exception {
    	testDemoAdditionalHtmlHeadLinkTags();
        passed();
    }
}
