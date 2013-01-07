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

import edu.samplu.common.MainMenuLookupITBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;

/**
 * tests whether the "Create New Agenda" is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaIT extends MainMenuLookupITBase {
    @Override
    protected String getLinkLocator() {
        return "link=Create New Agenda";
    }

    @Test
    public void testCreateNewAgenda() throws Exception {
        gotoMenuLinkLocator(); // NOT gotoCreateNew because this create new is on the Main Menu screen
        select("name=document.newMaintainableObject.dataObject.namespace", "label=Kuali Rules Test");
        String agendaName = "Agenda Date :"+ Calendar.getInstance().getTime().toString();
        waitAndType("name=document.newMaintainableObject.dataObject.agenda.name", "Agenda " + agendaName);
        waitAndType("name=document.newMaintainableObject.dataObject.contextName", "Context1");
        fireEvent("name=document.newMaintainableObject.dataObject.contextName", "blur");
        fireEvent("name=document.newMaintainableObject.dataObject.contextName", "focus");
        waitForElementPresent("name=document.newMaintainableObject.dataObject.agenda.typeId",
                "https://jira.kuali.org/browse/KULRICE-7924 : KRMS Agenda type select option not rendered w/o using Context lookup");
        select("name=document.newMaintainableObject.dataObject.agenda.typeId", "label=Campus Agenda");
        waitForElementPresent("name=document.newMaintainableObject.dataObject.customAttributesMap[Campus]");
        waitAndType("name=document.newMaintainableObject.dataObject.customAttributesMap[Campus]", "BL");
        waitAndClick("//div[2]/button");
        waitForPageToLoad();
        //selectFrame("relative=up");
        //waitAndClick("css=div.jGrowl-close");
        //selectFrame("iframeportlet");
        waitAndClick("//div[2]/button[3]");
        waitForPageToLoad();
        selectWindow("null");
        waitAndClick("xpath=(//input[@name='imageField'])[2]");
    }

    @Ignore // link to create new is off the main menu
    @Test
    public void testLookUp() throws Exception {}

    @Override
    public void lookupAssertions() {
        //none yet
    }
}
