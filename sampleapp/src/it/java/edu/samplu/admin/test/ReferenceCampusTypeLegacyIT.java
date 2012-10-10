/**
 * Copyright 2005-2011 The Kuali Foundation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;

/**
 * tests creating and cancelling the new Campus Type maintenance screen 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReferenceCampusTypeLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Test
    /**
     * tests that a new Campus Type maintenance document can be cancelled
     */
    public void testEditCampusType() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Campus Type");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitAndClickByLinkText("edit");
        assertElementPresentByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
    }
}
