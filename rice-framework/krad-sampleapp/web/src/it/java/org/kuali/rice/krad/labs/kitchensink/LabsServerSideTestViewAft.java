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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsServerSideTestViewAft extends LabsKitchenSinkBase {

    /**
     * "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
     */
    public static final String BOOKMARK_URL = /* "/kr-krad/uicomponents?viewId=Demo-ValidationServerSide&methodToCall=start"; */
            "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page3";

    private static final String ERROR_ELEMENT_XPATH="//div[@class='uif-inputField uif-boxLayoutVerticalItem clearfix uif-hasError']";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        navigateToKitchenSink("Validation");
        //        waitAndClickKRAD();
        //        waitAndClickByLinkText("ServerSide Constraint Validation Demo");
        //        switchToWindow("Kuali :: Validation Server-side Test View");
    }

    //Code for KRAD Test Package.
    protected void testServerSideTestView() throws Exception {

        //MinMax length and value  and Required Constraint
        waitAndTypeByName("field9","a");
        waitAndTypeByName("field10","1");
        waitAndClickByXpath("//button[@id='usave']");
        waitForTextPresent("MinMax Length test: Please enter at least 2 characters");
        assertTextPresent("MinMax Value test: Value must be greater than 2");
        assertTextPresent(new String[]{"Required constraint", "3 errors"});
        assertElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutHorizontalItem uif-hasError']");
        fireMouseOverEventByName("field3");
        waitForTextPresent("Required");
        fireMouseOverEventByName("field4");
        waitForTextPresent("Required");

        fireMouseOverEventByName("field5");
        waitForTextPresent("Required");

        //PreRequisite constraint

        waitAndTypeByName("field6", "test");
        waitAndTypeByName("field7", "test");
        fireMouseOverEventByName("field8");
        waitForTextPresent("Required by Field A");
        assertTextPresent("Required by Field A");

        clearTextByName("field6");
        clearTextByName("field7");

        waitAndTypeByName("field8","test");
        fireMouseOverEventByName("field6");
        assertTextPresent("Required");


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
        waitForTextPresent("  Must be at most 3 characters");
        waitAndTypeByName("field31","as");
        waitAndTypeByName("field32","asd");
        waitAndClickByXpath("//button[@id='usave']");
        waitForElementPresentByXpath(ERROR_ELEMENT_XPATH+"/input[@name='field33']");

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
