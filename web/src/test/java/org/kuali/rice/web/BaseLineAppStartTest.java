/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.web;

import java.net.URL;

import org.junit.Test;
import org.kuali.rice.test.web.HtmlUnitUtil;
import org.kuali.rice.web.test.ServerTestBase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BaseLineAppStartTest extends ServerTestBase {

    @Test public void testHomePage() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = HtmlUnitUtil.gotoPageAndLogin(HtmlUnitUtil.BASE_URL);
        assertEquals(HTML_PAGE_TITLE_TEXT, page.getTitleText() );
    }
    
}
