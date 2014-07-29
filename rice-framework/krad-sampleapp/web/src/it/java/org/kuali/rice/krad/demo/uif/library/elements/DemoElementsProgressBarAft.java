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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsProgressBarAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ProgressBarView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ProgressBarView";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Progress Bar");
    }

    protected void testLibraryElementsProgressBarPercentage() throws Exception {
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 0%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 25%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 50%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 75%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 100%;']");
        waitForElementPresentByXpath("//div[@class='progress uif-boxLayoutVerticalItem clearfix']/div[@style='width: 40%;']");
    }
    
    protected void testLibraryElementsProgressBarSteps() throws Exception {
    	selectByName("exampleShown","Steps");
        waitForElementPresentByXpath("//div[@class='progress']/div[@class='progress-bar progress-bar-empty']");
        waitForElementPresentByXpath("//div[@class='progress']/div[@class='progress-bar progress-bar-success']");
        waitForElementPresentByXpath("//div[@class='progress']/div[@class='progress-bar progress-bar-info']");
    }
    
    protected void testLibraryElementsProgressBarSuccessStep() throws Exception {
    	selectByName("exampleShown","Success Step");
        waitForElementPresentByXpath("//div[@aria-valuetext='Task Complete']/div/div[@class='progress-bar progress-bar-success']");
    }
    
    protected void testLibraryElementsProgressBarVertical() throws Exception {
    	selectByName("exampleShown","Vertical");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='0']");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='33']");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='66']");
        waitForElementPresentByXpath("//div[@class='progress uif-progressBar-vertical uif-boxLayoutHorizontalItem' and @aria-valuemin='100']");
    }
    
    protected void testLibraryElementsProgressBarVerticalSteps() throws Exception {
    	selectByName("exampleShown","Vertical Steps");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@class='uif-step complete']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@class='uif-step active']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@class='uif-step']");
    }
    
    protected void testLibraryElementsProgressBarVerticalStepHeight() throws Exception {
    	selectByName("exampleShown","Vertical Step Height");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 180px;']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@style='height: 16.0%;']");
        waitForElementPresentByXpath("//div[@class='progress-details' and @style='height: 225px;']/div[@style='height: 20.0%;']");
    }
    
    protected void testLibraryElementsProgressBarLookOptions() throws Exception {
    	selectByName("exampleShown","Look Options");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-info']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-warning']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-danger']");
    }
    
    protected void testLibraryElementsProgressBarStepSizes() throws Exception {
    	selectByName("exampleShown","Step Sizes");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-success' and @style='width: 20.0%;']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-success' and @style='width: 50.0%;']");
        waitForElementPresentByXpath("//div[@class='progress-bar progress-bar-info' and @style='width: 30.0%;']");
    }
    
    private void testLibraryElementsProgressBar() throws Exception {
    	testLibraryElementsProgressBarPercentage();
    	testLibraryElementsProgressBarSteps();
    	testLibraryElementsProgressBarSuccessStep();
    	testLibraryElementsProgressBarVertical();
    	testLibraryElementsProgressBarVerticalSteps();
    	testLibraryElementsProgressBarVerticalStepHeight();
    	testLibraryElementsProgressBarLookOptions();
    	testLibraryElementsProgressBarStepSizes();
    }
    
    @Test
    public void testLibraryElementsProgressBarBookmark() throws Exception {
    	testLibraryElementsProgressBar();
        passed();
    }

    @Test
    public void testLibraryElementsProgressBarNav() throws Exception {
    	testLibraryElementsProgressBar();
        passed();
    }  
}
