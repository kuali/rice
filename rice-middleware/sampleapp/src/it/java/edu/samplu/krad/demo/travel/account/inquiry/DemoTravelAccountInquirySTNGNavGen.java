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
package edu.samplu.krad.demo.travel.account.inquiry;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountInquirySTNGNavGen extends DemoTravelAccountInquirySTNGBase {

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testInquiryCollapseExpandNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testInquiryCollapseExpandNav() throws Exception {
        testInquiryCollapseExpandNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testInquiryLightBoxNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testInquiryLightBoxNav() throws Exception {
        testInquiryLightBoxNav(this);
    }

    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testInquiryBackButtonNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testInquiryBackButtonNav() throws Exception {
        testInquiryBackButtonNav(this);
    }
}
