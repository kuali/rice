/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesColumnCalculationsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutTotalingView&methodToCall=start
     */
    public static final String BOOKMARK_URL =
            "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutTotalingView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Column Calculations");
    }

    protected void testCollectionFeaturesColumnCalculations() throws Exception {
        String preValueString = waitAndGetText(By.xpath(
                "//div[@id='Demo-TableLayoutTotaling-Section1']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@data-role='pageTotal']/p"))[0];
        if (StringUtils.isBlank(preValueString)) {
            jiraAwareFail("calculation column contains no text");
        }
        Integer preValue = Integer.parseInt(preValueString);
        clearTextByName("collection1[0].field1");
        waitAndTypeByName("collection1[0].field1", "0");
        waitAndTypeByName("newCollectionLines['collection1'].field1", "");
        Integer postValue = Integer.parseInt(getTextByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section1']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@data-role='pageTotal']/p"));
        if (postValue >= preValue) {
            jiraAwareFail("Calculation Error !");
        }
    }

    protected void testCollectionFeaturesColumnCalculationsOnKeyUp() throws Exception {
        selectByName("exampleShown", "On Key Up");
        Integer preValue = Integer.parseInt(waitAndGetText(By.xpath(
                "//div[@id='Demo-TableLayoutTotaling-Section2']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@data-role='pageTotal']/p"))[0]);
        clearTextByName("collection1_2[0].field1");
        waitAndTypeByName("collection1_2[0].field1", "0");
        Thread.sleep(2000);
        Integer postValue = Integer.parseInt(getTextByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section2']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@data-role='pageTotal']/p"));
        if (postValue >= preValue) {
            jiraAwareFail("Calculation Error !");

        }
    }

    protected void testCollectionFeaturesColumnCalculationsSomeColumns() throws Exception {
        selectByName("exampleShown", "Some Columns");
        Integer preValue = Integer.parseInt(waitAndGetText(By.xpath(
                "//div[@id='Demo-TableLayoutTotaling-Section3']/div/table/tfoot/tr/th[3]/div/fieldset/div/div[@data-role='pageTotal']/p"))[0]);
        clearTextByName("collection1_3[0].field2");
        waitAndTypeByName("collection1_3[0].field2", "0");
        waitAndTypeByName("newCollectionLines['collection1_3'].field2", "");
        Integer postValue = Integer.parseInt(getTextByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section3']/div/table/tfoot/tr/th[3]/div/fieldset/div/div[@data-role='pageTotal']/p"));
        if (postValue >= preValue) {
            jiraAwareFail("Calculation Error !");

        }
    }

    protected void testCollectionFeaturesColumnCalculationsLeftLabel() throws Exception {
        selectByName("exampleShown", "Left Label");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section4']/div/table/tfoot/tr/th/div/label");
    }

    protected void testCollectionFeaturesColumnCalculationsMultipleOptions() throws Exception {
        selectByName("exampleShown", "Multiple Options");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section5']/div/table/tfoot/tr/th/div/fieldset/div/div/p");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section5']/div/table/tfoot/tr/th/div/fieldset/div/div/label");
        if (isElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section5']/div/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/span/label")) {
            jiraAwareFail("Multiple Options Failure !");
        }
    }

    protected void testCollectionFeaturesColumnCalculationsMultipleCalculations() throws Exception {
        selectByName("exampleShown", "Multiple Calculations");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div/fieldset/div/div/p");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[2]/fieldset/div/div/p");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[3]/fieldset/div/div/p");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[5]/div/div[4]/fieldset/div/div/p");
        if (isElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section6']/div/table/tfoot/tr/th[2]/div/div[@class='uif-verticalBoxLayout clearfix']/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div/p")) {
            jiraAwareFail("Multiple Calculations Failure !");
        }
    }

    protected void testCollectionFeaturesColumnCalculationsGroupingCalculations() throws Exception {
        selectByName("exampleShown", "Grouping Calculations");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section7']/div/table/tbody/tr[7]/td/div/p");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section7']/div/table/tbody/tr[7]/td[2]/div/p");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section7']/div/table/tbody/tr[7]/td[3]/div/p");
    }

    protected void testCollectionFeaturesColumnCalculationsNonClientSide() throws Exception {
        selectByName("exampleShown", "Non Client-side");
        assertElementPresentByXpath(
                "//div[@id='Demo-TableLayoutTotaling-Section8']/div/table/tfoot/tr/th[3]/div/fieldset/div/div/p[@data-role='totalValue']");
    }

    protected void testCollectionFeaturesColumnCalculationsCustomCalculation() throws Exception {
        selectByName("exampleShown", "Custom Calculation");
        assertTextPresent("Subtracted:");
    }

    private void testAllColumnCalculations() throws Exception {
        testCollectionFeaturesColumnCalculations();
        testCollectionFeaturesColumnCalculationsOnKeyUp();
        testCollectionFeaturesColumnCalculationsSomeColumns();
        testCollectionFeaturesColumnCalculationsGroupingCalculations();
        testCollectionFeaturesColumnCalculationsNonClientSide();
        testCollectionFeaturesColumnCalculationsCustomCalculation();
        testCollectionFeaturesColumnCalculationsLeftLabel();
        testCollectionFeaturesColumnCalculationsMultipleOptions();
        testCollectionFeaturesColumnCalculationsMultipleCalculations();
    }

    @Test
    public void testCollectionFeaturesColumnCalculationsBookmark() throws Exception {
        testAllColumnCalculations();
        passed();
    }

    @Test
    public void testCollectionFeaturesColumnCalculationsNav() throws Exception {
        testAllColumnCalculations();
        passed();
    }
}
