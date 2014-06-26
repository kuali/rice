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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DialogTestViewAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/dialog-configuration-test?viewId=DialogTestView&methodToCall=start
     */
    public static final String BOOKMARK_URL ="/kr-krad/dialog-configuration-test?viewId=DialogTestView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Dialog Test View");
        switchToWindow("Kuali :: Dialog Test View");
    }
    
    private void testDialogTestView() throws Exception{
        waitAndClickButtonByText("Lightbox from hidden group");
        validateSelectAuthorDialog();
        waitAndClickButtonByText("Lightbox from hidden group with overrides");
        validateSelectAuthorDialog();
        waitAndClickButtonByText("Lightbox from HTML");
        validateDialogWithHTML();
        waitAndClickButtonByText("Predefined OK/Cancel");
        validateDialogWithPredefinedOkCancel();
        waitAndClickButtonByText("Radio Buttons");
        validateDialogWithRadioButton();
        waitAndClickButtonByText("Lightbox from hidden group with progressive rendering");
        validateDialogWithProgressiveRendering();
        waitAndClickButtonByText("Lightbox from hidden image");
        validateDialogWithHiddenImage();
        waitAndClickButtonByText("Lightbox from URL");
        validateDialogWithFormUrl();
        waitAndClickButtonByText("Client Ajax Dialog");
        validateDialogWithClientAjax();
        waitAndClickButtonByText("Regular Group");
        validateDialogWithRegularGroup();
        waitAndClickButtonByText("Expression dialog");
        validateDialogWithExpression();
        waitAndClickButtonByText("Extended Dialog");
        validateDialogWithExtended();
        waitAndClickButtonByText("Displays Response in LightBox");
        validateDialogWithDisplayResponseInLightBox();
        waitAndClickButtonByText("Tell Me A Story");
        waitAndCloseDialog();
        waitAndClickButtonByText("Close");
        waitAndClickLinkContainingText("Cancel");
    }
    
    private void validateSelectAuthorDialog() throws Exception {
        waitForElementPresentByXpath("//div[@class='fancybox-skin']/div/div/form/section/header/h3");
        waitForElementPresentByXpath("//div[@class='fancybox-skin']/div/div/form/section/div/p");
        waitAndCloseDialog();
    }

    private void waitAndCloseDialog() throws InterruptedException {
        jGrowl("Click Dialog Close");
        waitAndClickByXpath("//a[@class='fancybox-item fancybox-close']");
    }

    private void validateDialogWithHTML() throws Exception {
        waitForElementPresentByXpath("//form[@id='kualiLightboxForm']/b");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithPredefinedOkCancel() throws Exception {
        waitForTextPresent("Please Confirm to Continue");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithRadioButton() throws Exception {
        waitForElementPresentByXpath("//div[@id='sampleRadioButtonDialog']/div/fieldset/span/input[@type='radio']");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithProgressiveRendering() throws Exception {
        waitForTextPresent("This is always displayed:");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithHiddenImage() throws Exception {
        waitForElementPresentByXpath("//img[@src='/krad/images/computer_programming.jpg']");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithFormUrl() throws Exception {
        waitForElementPresentByXpath("//iframe[@class='fancybox-iframe']");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithClientAjax() throws Exception {
        waitForTextPresent("Please select from the values below");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithRegularGroup() throws Exception {
        waitForTextPresent("This group does not inherit from Uif-DialogGroup:");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithExpression() throws Exception {
        waitForTextPresent("Mark as Ready for Scheduling");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithExtended() throws Exception {
        waitForElementPresentByXpath("//input[@name='field5']");
        waitAndCloseDialog();
    }
    
    private void validateDialogWithDisplayResponseInLightBox() throws Exception {
        waitForTextPresent("This group does not inherit from Uif-DialogGroup:");
        waitAndCloseDialog();
    }
    
    @Test
    public void testDialogTestViewBookmark() throws Exception {
        testDialogTestView();
        passed();
    }

    @Test
    public void testDialogTestViewNav() throws Exception {
        testDialogTestView();
        passed();
    }
}
