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
package edu.sampleu.krad.validationmessagesview;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerSideTestViewAft extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Demo-ValidationServerSide&methodToCall=start";
    
    private static final String ERROR_ELEMENT_XPATH="//div[@class='uif-inputField uif-boxLayoutVerticalItem clearfix uif-hasError']";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("ServerSide Constraint Validation Demo");
        switchToWindow("Kuali :: Validation Server-side Test View");               
    }

    //Code for KRAD Test Package.
    protected void testServerSideTestView() throws Exception {
      
       //MinMax length and value  and Required Constraint
       waitAndTypeByName("field9","a");
       waitAndTypeByName("field10","1");
       waitAndClickByXpath("//button[@id='usave']");
       waitForTextPresent("MinMax Length test: Must be between 2 and 5 characters long");
       assertTextPresent("MinMax Value test: Value must be greater than 2 and no more than 50");
       assertTextPresent(new String[]{"Required constraint", "4 errors"});
       assertElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutHorizontalItem uif-hasError']");
       fireMouseOverEventByName("field3");
       waitForTextPresent("Field Label is a required field.");
       fireMouseOverEventByName("field4");
       waitForTextPresent("Radio is a required field.");
       fireMouseOverEventByName("field5");
       waitForTextPresent("Select is a required field.");
       
       //PreRequisite constraint
       waitForElementPresentByXpath("//input[@name='field7' and @disabled]");
       waitAndClickByXpath("//input[@type='checkbox' and @name='booleanField']");
       if(isElementPresentByXpath("//input[@name='field7' and @disabled]")) {
           fail("PreRequisite Constraint isn't working !");
       }
       
       //MustOccurs constraint
       waitAndTypeByName("field14","a");
       waitAndClickByXpath("//button[@id='usave']");
       Thread.sleep(4000);
       
       //Case constraint
       waitAndClickByXpath("//input[@name='field24' and @value='case1']");
       waitAndClickByXpath("//button[@id='usave']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field25']");
       waitAndClickByXpath("//input[@name='field24' and @value='case2']");
       waitAndClickByXpath("//button[@id='usave']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field25']");
       waitAndTypeByName("field25","123@#");
       waitAndClickByXpath("//button[@id='usave']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field25']");
       fireMouseOverEventByName("field25");
       waitForTextPresent("  Can only be alphanumeric characters");
       waitAndTypeByName("field26","1234");
       waitAndClickByXpath("//input[@name='field24' and @value='case3']");
       waitAndClickByXpath("//button[@id='usave']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field26']");
       fireMouseOverEventByName("field26");
       waitForTextPresent("  Value cannot be greater than 500");
       waitForTextPresent("  Must be at most 3 characters");
       waitAndTypeByName("field31","as");
       waitAndTypeByName("field32","asd");
       waitAndClickByXpath("//button[@id='usave']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field33']");
       
       //All remaining constraint
       waitAndTypeByName("field50","1111.111");
       waitAndTypeByName("field51","#1L");
       waitAndTypeByName("field77","1.1");
       waitAndTypeByName("field52","asdfasdf");
       waitAndTypeByName("field53","deep");
       waitAndTypeByName("field54","aad.@c");
       waitAndTypeByName("field84","www.kuali.org");
       waitAndTypeByName("field55","asd");
       waitAndTypeByName("field75","2014-04-04");
       waitAndTypeByName("field82","13");
       waitAndTypeByName("field83","24");
       waitAndTypeByName("field57","1599");
       waitAndTypeByName("field58","0");
       waitAndTypeByName("field61","360001");
       waitAndTypeByName("field62","@#");
       waitAndTypeByName("field63","@/");
       waitAndTypeByName("field64","@#12");
       waitAndTypeByName("field65","a s");
       waitAndTypeByName("field66","d");
       waitAndTypeByName("field67","asd");
       waitAndTypeByName("field68","1.a");
       waitAndClickByXpath("//button[@id='usave']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field50']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field51']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field77']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field52']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field53']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field54']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field84']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field55']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field75']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field82']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field83']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field57']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field58']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field61']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field62']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field63']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field64']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field65']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field66']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field67']");
       waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field68']");

    }

    @Test
    public void testServerSideTestViewBookmark() throws Exception {
        testServerSideTestView();
        passed();
    }

    @Test
    public void testServerSideTestViewNav() throws Exception {
        testServerSideTestView();
        passed();
    }
}
