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
public class DemoValidationSimpleConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-SimpleConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-SimpleConstraintView&methodToCall=start";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Simple Constraints");
    }

    protected void testValidationSimpleConstraintsRequired() throws Exception {
        waitAndClickByLinkText("Required");

        assertFocusTypeBlurError("inputField1", "");
        assertFocusTypeBlurError("inputField2", "");
        assertFocusTypeBlurError("inputField3", "");
    }
    
    protected void testValidationSimpleConstraintsMinMaxLength() throws Exception {
        waitAndClickByLinkText("Min/Max Length");

        waitAndTypeByName("inputField4","deepmoteria");
        Thread.sleep(1000);
        assertEquals("deepm", findElement(By.name("inputField4")).getAttribute("value"));
        assertFocusTypeBlurError("inputField5", "de");
    }

    protected void testValidationSimpleConstraintsMinMaxValue() throws Exception {
        waitAndClickByLinkText("Min/Max Value");

        assertFocusTypeBlurError("inputField6","21");
        assertFocusTypeBlurError("inputField7","2");
    }
    
    @Test
    public void testValidationSimpleConstraintsBookmark() throws Exception {
        testValidationSimpleConstraintsRequired();
        testValidationSimpleConstraintsMinMaxLength();
        testValidationSimpleConstraintsMinMaxValue();
        passed();
    }

    @Test
    public void testValidationSimpleConstraintsNav() throws Exception {
        testValidationSimpleConstraintsRequired();
        testValidationSimpleConstraintsMinMaxLength();
        testValidationSimpleConstraintsMinMaxValue();
        passed();
    }
}
