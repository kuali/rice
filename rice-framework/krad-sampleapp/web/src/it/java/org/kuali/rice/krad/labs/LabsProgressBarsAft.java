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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsProgressBarsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=Lab-ProgressBar
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-ProgressBar";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Progress Bars");
    }

    protected void testProgressBarPercentage() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 0%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 25%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 50%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 75%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 100%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 40%;']");
    }
    
    protected void testProgressBarSteps() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress']/div[@class='progress-bar progress-bar-empty']");
        waitForElementPresentByXpath("//div[@class='progress']/div[@class='progress-bar progress-bar-success']");
        waitForElementPresentByXpath("//div[@class='progress']/div[@class='progress-bar progress-bar-info']");
    }
    
    protected void testProgressBarSuccessStep() throws Exception {
        waitForElementPresentByXpath("//div[@aria-valuetext='Task Complete']/div/div[@class='progress-bar progress-bar-success']");
    }
    
    protected void testProgressBarVertical() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='0']");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='33']");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='66']");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='100']");
    }
    
    protected void testProgressBarVerticalSteps() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@class='uif-step complete']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@class='uif-step active']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@class='uif-step']");
    }
    
    protected void testProgressBarVerticalStepHeight() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 180px;']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@style='height: 16.0%;']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@style='height: 20.0%;']");
    }
    
    protected void testProgressBarLookOptions() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-info']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-warning']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-danger']");
    }
    
    protected void testProgressBarStepSizes() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-success' and @style='width: 20.0%;']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-success' and @style='width: 50.0%;']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-info' and @style='width: 30.0%;']");
    }
    
    private void testProgressBar() throws Exception {
    	testProgressBarPercentage();
    	testProgressBarSteps();
    	testProgressBarSuccessStep();
    	testProgressBarVertical();
    	testProgressBarVerticalSteps();
    	testProgressBarVerticalStepHeight();
    	testProgressBarLookOptions();
    	testProgressBarStepSizes();
    }

    @Test
    public void testProgressBarBookmark() throws Exception {
    	testProgressBar();
        passed();
    }

    @Test
    public void testProgressBarNav() throws Exception {
    	testProgressBar();
        passed();
    }
}
