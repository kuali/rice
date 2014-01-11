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
public class DemoValidationPrerequisiteConstraintsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-PrerequisiteConstraintView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-PrerequisiteConstraintView&methodToCall=start";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Prerequisite Constraint");
    }

    protected void testValidationPrerequisiteConstraintsRequired() throws Exception {
        waitAndClickByLinkText("Basic Example");

        waitAndTypeByName("inputField1","a");
        waitAndTypeByName("inputField2","");
        waitAndTypeByName("inputField3","");
        waitAndTypeByName("inputField1","");
        String id2 = findElement(By.name("inputField2")).getAttribute("id");
        assertTrue(findElement(By.id(id2)).getAttribute("class").contains("error"));
        String id3 = findElement(By.name("inputField3")).getAttribute("id");
        assertTrue(findElement(By.id(id3)).getAttribute("class").contains("error"));
        clearTextByName("inputField1");
        waitAndTypeByName("inputField3","a");
        waitAndTypeByName("inputField1","");
        String id1 = findElement(By.name("inputField1")).getAttribute("id");
        assertTrue(findElement(By.id(id1)).getAttribute("class").contains("error"));
    }
    
    @Test
    public void testValidationPrerequisiteConstraintsBookmark() throws Exception {
        testValidationPrerequisiteConstraintsRequired();
        passed();
    }

    @Test
    public void testValidationPrerequisiteConstraintsNav() throws Exception {
        testValidationPrerequisiteConstraintsRequired();
        passed();
    }
}
