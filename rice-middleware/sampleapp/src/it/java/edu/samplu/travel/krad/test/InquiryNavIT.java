/*
 * Copyright 20052013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package edu.samplu.travel.krad.test;

import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * tests the inquiry feature in rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryNavIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Test
    public void testInquiry() throws Exception {
        waitAndClickKRAD();
        waitAndClickByLinkText(TRAVEL_ACCOUNT_LOOKUP_LINK_TEXT);
        super.testInquiry();
        passed();
    }
}
