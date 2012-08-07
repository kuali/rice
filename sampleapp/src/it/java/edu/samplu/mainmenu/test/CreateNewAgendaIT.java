/*
 * Copyright 2011 The Kuali Foundation
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
package edu.samplu.mainmenu.test;

import java.util.Calendar;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;
import org.junit.Assert;

/**
 * tests whether the "Create New Agenda" is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    public void testCreateNewAgenda() throws Exception {
        selenium.click("link=Create New Agenda");
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        selenium.select("name=document.newMaintainableObject.dataObject.namespace", "label=Kuali Rules Test");
        String agendaName = "Agenda Date :"+ Calendar.getInstance().getTime().toString();
        selenium.type("name=document.newMaintainableObject.dataObject.agenda.name", "Agenda " + agendaName);
        selenium.type("name=document.newMaintainableObject.dataObject.contextName", "Context1");
        selenium.fireEvent("name=document.newMaintainableObject.dataObject.contextName", "blur");
        selenium.fireEvent("name=document.newMaintainableObject.dataObject.contextName", "focus");
        Thread.sleep(3000);
        selenium.select("name=document.newMaintainableObject.dataObject.agenda.typeId", "label=Campus Agenda");
        selenium.waitForPageToLoad("30000");
        Thread.sleep(3000);
        selenium.type("name=document.newMaintainableObject.dataObject.customAttributesMap[Campus]", "BL");
        selenium.click("//div[2]/button");
        selenium.waitForPageToLoad("30000");
        //selenium.selectFrame("relative=up");
        //selenium.click("css=div.jGrowl-close");
        //selenium.selectFrame("iframeportlet");
        selenium.click("//div[2]/button[3]");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("xpath=(//input[@name='imageField'])[2]");
        selenium.waitForPageToLoad("30000");
        
    }
}
