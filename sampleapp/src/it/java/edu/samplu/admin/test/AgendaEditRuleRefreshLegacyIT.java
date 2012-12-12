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

import org.junit.Assert;
import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;


/**
 * test that repeated ajax refreshes work
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditRuleRefreshLegacyIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl(){
        return ITUtil.PORTAL;
    }
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.WebDriverLegacyITBase#getTestUrl()
     */
    
    @Test
    /**
     * test that repeated ajax refreshes work
     */
    public void testAgendaEditRuleRefreshIT() throws Exception {
        waitAndClickByLinkText("Agenda Lookup");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[1]"); //  waitAndClick("id=32");
        Thread.sleep(3000);
        waitAndClickByXpath("//a[@title='edit Agenda Definition withAgenda Id=T1000 ']", "Does user have edit permissions?"); // waitAndClick("id=194_line0");
        checkForIncidentReport("");
        Thread.sleep(3000);
        waitAndClickByXpath("//li/a[@class='agendaNode ruleNode']"); // waitAndClick("//li[@id='473_node_0_parent_root']/a");
        waitAndClickByXpath("//li/a[@class='agendaNode logicNode whenTrueNode']");
        waitAndClickByLinkText("[-] collapse all");

        // click refresh  several times
        for (int i = 0; i < 6; i++) {
            for (int second = 0;; second++) {
                if (second >= 60)
                    Assert.fail("timeout");
                try {
                    if (isElementPresent(".kr-refresh-button"))
                        break;
                } catch (Exception e) {}
                Thread.sleep(1000);
            }

            waitAndClick("button.kr-refresh-button");
        }
    }
    
}

