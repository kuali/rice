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

import edu.samplu.common.MainMenuLookupLegacyITBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;

/**
 * tests whether the "Create New Agenda" is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaLegacyIT extends MainMenuLookupLegacyITBase {
    @Override
    protected String getLinkLocator() {
        return "Create New Agenda";
    }
    
    @Test
    public void testCreateNewAgenda() throws Exception {
        gotoMenuLinkLocator(); // NOT gotoCreateNew because this create new is on the Main Menu screen
        selectByName("document.newMaintainableObject.dataObject.namespace", "Kuali Rules Test");
        String agendaName = "Agenda Date :"+ Calendar.getInstance().getTime().toString();
        waitAndTypeByName("document.newMaintainableObject.dataObject.agenda.name", "Agenda " + agendaName);
        waitAndTypeByName("document.newMaintainableObject.dataObject.contextName", "Context1");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "blur");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "focus");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.agenda.typeId");                
        selectByName("document.newMaintainableObject.dataObject.agenda.typeId", "Campus Agenda");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]");
        waitAndTypeByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]", "BL");
        waitAndClickByXpath("//div[2]/button");
        waitForPageToLoad();    
        waitAndClickByXpath("//div[2]/button[3]");
        waitForPageToLoad();
        selectTopFrame();
        waitAndClickByXpath("(//input[@name='imageField'])[2]");
        passed();
    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MainMenuLookupLegacyITBase#lookupAssertions()
     */
    @Override
    public void lookupAssertions() {
        // TODO mmodi - THIS METHOD NEEDS JAVADOCS
        // none yet
    }
    
    @Ignore // link to create new is off the main menu    
    @Test
    @Override
    public void testLookUp() throws Exception {}
   
}
