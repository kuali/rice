/*
 * Copyright 2006-2012 The Kuali Foundation
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
package edu.samplu.krad.compview;

import edu.samplu.common.WebDriverLegacyITBase;

import junit.framework.Assert;
import org.junit.Test;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

/**
 * Selenium test that tests that tooltips are rendered on mouse over and focus events and hidden on
 * mouse out and blur events
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifTooltipLegacyIT extends WebDriverLegacyITBase {
    private static final String NAME_FIELD_1 = "field1";
    private static final String NAME_FIELD_2 = "field2";
    @Override
    public String getTestUrl() {
        // open Other Examples page in kitchen sink view
        return "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page10";
    }

    @Test
    public void testTooltip() throws Exception {
        // check if tooltip opens on focus
        fireEvent(NAME_FIELD_1, "focus");
        fireMouseOverEventByName(NAME_FIELD_1);
        Assert.assertTrue(isVisible("div.jquerybubblepopup.jquerybubblepopup-black")
                && isVisible("td.jquerybubblepopup-innerHtml"));
        Assert.assertEquals("This tooltip is triggered by focus or and mouse over.", getText("td.jquerybubblepopup-innerHtml"));
       
        // check if tooltip closed on blur
        fireEvent(NAME_FIELD_1, "blur");
        Assert.assertFalse(isVisible("div.jquerybubblepopup.jquerybubblepopup-black")
                && isVisible("td.jquerybubblepopup-innerHtml"));
        //Assert.assertFalse(isVisible("//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));
        
        // check if tooltip opens on mouse over
        fireMouseOverEventByName(NAME_FIELD_2);
        Assert.assertTrue(isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));
        
        // check if tooltip closed on mouse out
        waitAndTypeByName(NAME_FIELD_2,"a");
        Thread.sleep(5000);
        Assert.assertFalse(isVisibleByXpath("//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));
        
        // check that default tooltip does not display when there are an error message on the field
        waitAndTypeByName(NAME_FIELD_1, "1");
        fireEvent(NAME_FIELD_1, "blur");
        fireEvent(NAME_FIELD_1, "focus");
        fireMouseOverEventByName(NAME_FIELD_1);
        Thread.sleep(5000);
        assertTrue("https://jira.kuali.org/browse/KULRICE-8141 Investigate why UifTooltipIT.testTooltip fails around jquerybubblepopup",
                isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-kr-error-cs']") &&
                !(isVisibleByXpath("//div[@class='jquerybubblepopup jquerybubblepopup-black']")) &&
                isVisibleByXpath("//img[@class='uif-validationImage']"));
        // TODO figure out this last assert
        //Assert.assertFalse(isVisible("//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));
        passed();
    }
}
