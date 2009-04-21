/*
 * Copyright 2007 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.web.document;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.web.test.WebTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class tests various ValuesFinders classes.
 * see KULRICE-1343, KULRICE-1344
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Ignore("KULRICE-3011")
public class WebActionTest extends WebTestBase {


    public WebActionTest() {
    }


    /**
     * This method tests to make sure the close button returns to where the
     * user is expecting if they choose not to save the document.
     *
     * @throws Exception
     */
    @Test public void testCloseDocumentWithoutSave() throws Exception {
        final HtmlPage page = getPortalPage();

        HtmlPage page2 = clickOn(page, "createTravelRequest");

        assertEquals("Kuali :: Travel Doc 2", page2.getTitleText());

        HtmlPage questionPage = clickOn(page2, "methodToCall.close");

        assertEquals("Kuali :: Question Dialog Page", questionPage.getTitleText());

        HtmlPage finalPagePage = clickOn(questionPage, "methodToCall.processAnswer.button1");

        assertEquals(HTML_PAGE_TITLE_TEXT, finalPagePage.getTitleText());
    }

    /**
     * This method tests to make sure the close button returns to where the
     * user is expecting if they choose not to save the document.
     *
     * @throws Exception
     */
    @Test public void testCancelDocument() throws Exception {
        final HtmlPage page = getPortalPage();

        HtmlPage page2 = clickOn(page, "createTravelRequest");

        assertEquals("Kuali :: Travel Doc 2", page2.getTitleText());

        HtmlPage questionPage = clickOn(page2, "methodToCall.cancel");

        assertEquals("Kuali :: Question Dialog Page", questionPage.getTitleText());

        HtmlPage finalPage = clickOn(questionPage, "methodToCall.processAnswer.button1");

        assertEquals("Kuali :: Travel Doc 2", finalPage.getTitleText());
        assertFalse(finalPage.asText().contains("500"));
    }

}
