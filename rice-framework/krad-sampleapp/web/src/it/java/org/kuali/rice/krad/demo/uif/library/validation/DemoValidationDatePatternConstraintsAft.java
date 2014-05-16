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
package org.kuali.rice.krad.demo.uif.library.validation;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoValidationDatePatternConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-DatePatternConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-DatePatternConstraintView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Date Constraint");
    }

    protected void testValidationDatePatternConstraints() throws Exception {
        waitAndClickByLinkText("Default");

        //Scenario-1
        assertFocusTypeBlurError("inputField1","2 June 2012");
    }
    
    protected void testValidationDatePatternConstraintsBasicDate() throws Exception {
        waitAndClickByLinkText("Basic Date");
        
        //Scenario-1
        waitAndTypeByName("inputField3","07/2/13");
        waitAndTypeByName("inputField2","2 July 2013");
        isNotVisible(By.xpath("//div[@class='uif-clientMessageItems uif-clientErrorDiv']"));
        waitAndTypeByName("inputField2","");
        isVisible(By.xpath("//div[@class='uif-clientMessageItems uif-clientErrorDiv']"));
    }
    
    protected void testValidationDatePatternCustomize() throws Exception {
        waitAndClickByLinkText("Customize");
       
        //Scenario-1
        waitAndTypeByName("inputField4","23/12/13");
        typeTab();
        isVisible(By.xpath("//div[@class='uif-clientMessageItems uif-clientErrorDiv']"));
    }
    
    @Test
    public void testValidationDatePatternConstraintsBookmark() throws Exception {
        testValidationDatePatternConstraints();
        testValidationDatePatternConstraintsBasicDate();
        testValidationDatePatternCustomize();
        passed();
    }

    @Test
    public void testValidationDatePatternConstraintsNav() throws Exception {
        testValidationDatePatternConstraints();
        testValidationDatePatternConstraintsBasicDate();
        testValidationDatePatternCustomize();
        passed();
    }
}
