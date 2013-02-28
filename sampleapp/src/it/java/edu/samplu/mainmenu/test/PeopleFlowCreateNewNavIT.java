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

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import com.thoughtworks.selenium.Selenium;

import edu.samplu.common.AdminMenuITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.MainMenuLookupITBase;
import edu.samplu.common.MainMenuLookupLegacyITBase;
import edu.samplu.common.MenuITBase;
import edu.samplu.common.UpgradedSeleniumITBase;

/**
 * TODO vchauhan don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowCreateNewNavIT extends MainMenuLookupLegacyITBase {

    @Test
    public void testPeopleFlow() throws Exception {

        //Click Main Menu and Create New
        gotoMenuLinkLocator();
        super.testPeopleFlow();
    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MainMenuLookupITBase#lookupAssertions()
     */
    //    @Override
    public void lookupAssertions() {
        // nothing

    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuITBase#getLinkLocator()
     */
    @Override
    protected String getLinkLocator() {
        return "People Flow";
    }

    @Ignore
    // No Need to     
    @Test
    @Override
    public void testLookUp() throws Exception {}
}
