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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;
import org.kuali.rice.testtools.selenium.JiraAwareFailureUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesColumnCalculationsAft extends AutomatedFunctionalTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutTotaling-View&methodToCall=start
     */
    public static final String BOOKMARK_URL =
            "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutTotaling-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Column Calculations");
    }

    protected void testCollectionFeaturesColumnCalculations() throws Exception {
        String preValueString = waitAndGetText(By.xpath(
                "//div[@id='Demo-TableLayoutTotaling-Section1']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]"))[0];
        if (StringUtils.isBlank(preValueString)) {
            JiraAwareFailureUtil.fail("calculation column contains no text", this);
        }
        Integer preValue = Integer.parseInt(preValueString);
        clearTextByName("collection1[0].field1");
        waitAndTypeByName("collection1[0].field1", "0");
        waitAndTypeByName("newCollectionLines['collection1'].field1", "");
        Integer postValue = Integer.parseInt(getTextByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section1']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]"));

        if (postValue >= preValue) {
            fail("Calculation Error !");
        }
    }

    protected void testCollectionFeaturesColumnCalculationsOnKeyUp() throws Exception {
        selectByName("exampleShown", "On Key Up");
        Integer preValue = Integer.parseInt(waitAndGetText(By.xpath(
                "//div[@id='Demo-TableLayoutTotaling-Section2']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]"))[0]);
        clearTextByName("collection1_2[0].field1");
        waitAndTypeByName("collection1_2[0].field1", "0");
        Thread.sleep(2000);
        Integer postValue = Integer.parseInt(getTextByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section2']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]"));
        if (postValue >= preValue) {
            fail("Calculation Error !");

        }
    }

    protected void testCollectionFeaturesColumnCalculationsSomeColumns() throws Exception {
        selectByName("exampleShown", "Some Columns");
        Integer preValue = Integer.parseInt(waitAndGetText(By.xpath(
                "//div[@id='Demo-TableLayoutTotaling-Section3']/div/table/tfoot/tr/th[3]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]"))[0]);
        clearTextByName("collection1_3[0].field2");
        waitAndTypeByName("collection1_3[0].field2", "0");
        waitAndTypeByName("newCollectionLines['collection1_3'].field2", "");
        Integer postValue = Integer.parseInt(getTextByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section3']/div/table/tfoot/tr/th[3]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]"));
        if (postValue >= preValue) {
            fail("Calculation Error !");

        }
    }

    protected void testCollectionFeaturesColumnCalculationsLeftLabel() throws Exception {
        selectByName("exampleShown", "Left Label");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section4']/div/table/tfoot/tr/th/div/div[@class='uif-verticalBoxLayout clearfix']/span/label");
    }

    protected void testCollectionFeaturesColumnCalculationsMultipleOptions() throws Exception {
        selectByName("exampleShown", "Multiple Options");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section5']/div/table/tfoot/tr/th/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section5']/div/table/tfoot/tr/th/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span/label");
        if (isElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section5']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span/label")) {
            fail("Multiple Options Failure !");
        }
    }

    protected void testCollectionFeaturesColumnCalculationsMultipleCalculations() throws Exception {
        selectByName("exampleShown", "Multiple Calculations");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout clearfix']/div[2]/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout clearfix']/div[3]/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout clearfix']/div[4]/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]");
        if (isElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[2]/div/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]")) {
            fail("Multiple Calculations Failure !");
        }
    }

    protected void testCollectionFeaturesColumnCalculationsGroupingCalculations() throws Exception {
        selectByName("exampleShown", "Grouping Calculations");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section7']/div/table/tbody/tr[7]/td/div/span[@data-role]");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section7']/div/table/tbody/tr[7]/td[2]/div/span[@data-role]");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section7']/div/table/tbody/tr[7]/td[3]/div/span[@data-role]");
    }

    protected void testCollectionFeaturesColumnCalculationsNonClientSide() throws Exception {
        selectByName("exampleShown", "Non Client-side");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section8']/div/table/tfoot/tr/th[3]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span[@data-role]");
    }

    protected void testCollectionFeaturesColumnCalculationsCustomCalculation() throws Exception {
        selectByName("exampleShown", "Custom Calculation");
        assertTextPresent("Subtracted:");
    }

    @Test
    public void testCollectionFeaturesColumnCalculationsBookmark() throws Exception {
        testCollectionFeaturesColumnCalculations();
        testCollectionFeaturesColumnCalculationsOnKeyUp();
        testCollectionFeaturesColumnCalculationsSomeColumns();
        testCollectionFeaturesColumnCalculationsLeftLabel();
        testCollectionFeaturesColumnCalculationsMultipleOptions();
        testCollectionFeaturesColumnCalculationsMultipleCalculations();
        testCollectionFeaturesColumnCalculationsGroupingCalculations();
        testCollectionFeaturesColumnCalculationsNonClientSide();
        testCollectionFeaturesColumnCalculationsCustomCalculation();
        passed();
    }

    @Test
    public void testCollectionFeaturesColumnCalculationsNav() throws Exception {
        testCollectionFeaturesColumnCalculations();
        testCollectionFeaturesColumnCalculationsOnKeyUp();
        testCollectionFeaturesColumnCalculationsSomeColumns();
        testCollectionFeaturesColumnCalculationsLeftLabel();
        testCollectionFeaturesColumnCalculationsMultipleOptions();
        testCollectionFeaturesColumnCalculationsMultipleCalculations();
        testCollectionFeaturesColumnCalculationsGroupingCalculations();
        testCollectionFeaturesColumnCalculationsNonClientSide();
        testCollectionFeaturesColumnCalculationsCustomCalculation();
        passed();
    }
}
