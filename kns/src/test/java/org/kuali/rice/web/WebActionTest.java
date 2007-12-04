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
package org.kuali.rice.web;

import java.net.URL;

import org.junit.Test;
import org.kuali.rice.testharness.HtmlUnitUtil;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class tests various ValuesFinders classes.
 * see KULRICE-1343, KULRICE-1344
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
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
        final WebClient webClient = new WebClient();
        final URL url = new URL(HtmlUnitUtil.BASE_URL);
        final HtmlPage page = (HtmlPage)webClient.getPage(url);
        assertEquals("Rice Sample Client", page.getTitleText() );

        final HtmlAnchor createTravelRequest = (HtmlAnchor) page.getAnchorByName("createTravelRequest");
        final HtmlPage page2 = login(webClient, url, "Rice Sample Client", createTravelRequest.getHrefAttribute());

        assertEquals("Kuali :: Travel Doc 2", page2.getTitleText());

        final HtmlForm kualiForm = (HtmlForm) page2.getForms().get(0);

        HtmlPage questionPage = clickButton(page2, kualiForm, "methodToCall.close", IMAGE_INPUT);

        assertEquals("Kuali :: Question Dialog Page", questionPage.getTitleText());

        final HtmlForm questionForm = (HtmlForm) questionPage.getForms().get(0);

        HtmlPage finalPagePage = clickButton(questionPage, questionForm, "methodToCall.processAnswer.button1", IMAGE_INPUT);

        assertEquals("Rice Sample Client", finalPagePage.getTitleText());
    }

}
