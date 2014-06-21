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
public abstract class LabsValidCharsConstraintAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page4";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToKitchenSink("Validation - Regex");
    }

    @Test
    public void testValidCharsConstraintNav() throws Exception {
        testValidCharsConstraintIT();
        passed();
    }

    @Test
    public void testValidCharsConstraintBookmark() throws Exception {
        testValidCharsConstraintIT();
        passed();
    }

    protected void testValidCharsConstraintIT() throws Exception {
        assertFocusTypeBlurValidation("field50", new String[]{"12.333", "-123.33"}, new String[]{"123.33"});
        assertFocusTypeBlurValidation("field51", new String[]{"A"}, new String[]{"-123.33"});

        // TODO continue to convert to assertFocusTypeBlurValidation
        assertFocusTypeBlurValidation("field77", new String[]{"1.1"},new String[]{"12"});
        assertFocusTypeBlurValidation("field52", new String[]{"5551112222"},new String[]{"555-111-1111"});
        assertFocusTypeBlurValidation("field53", new String[]{"1ClassName.java"},new String[]{"ClassName.java"});
        assertFocusTypeBlurValidation("field54", new String[]{"aaaaa"},new String[]{"aaaaa@kuali.org"});
        assertFocusTypeBlurValidation("field84", new String[]{"aaaaa"},new String[]{"http://www.kuali.org"});
        assertFocusTypeBlurValidation("field55", new String[]{"023512"},new String[]{"022812"});
        assertFocusTypeBlurValidation("field75", new String[]{"02/35/12"},new String[]{"02/28/12"});
        assertFocusTypeBlurValidation("field82", new String[]{"13:22"},new String[]{"02:33"});
        assertFocusTypeBlurValidation("field83", new String[]{"25:22"},new String[]{"14:33"});
        assertFocusTypeBlurValidation("field56", new String[]{"2020-06-02"},new String[]{"2020-06-02 03:30:30.22"});
        assertFocusTypeBlurValidation("field57", new String[]{"0"},new String[]{"2020"});
        assertFocusTypeBlurValidation("field58", new String[]{"13"},new String[]{"12"});
        assertFocusTypeBlurValidation("field61", new String[]{"5555-444"},new String[]{"55555-4444"});
        assertFocusTypeBlurValidation("field62", new String[]{"aa5bb6_a"},new String[]{"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"});
        assertFocusTypeBlurValidation("field63", new String[]{"#fff555"},new String[]{"aa22 _/"});
        assertFocusTypeBlurValidation("field64", new String[]{"AABB55"},new String[]{"ABCDEFGHIJKLMNOPQRSTUVWXY,Z abcdefghijklmnopqrstuvwxy,z"});
        assertFocusTypeBlurValidation("field76", new String[]{"AA~BB%"},new String[]{"abcABC %$#@&<>\\{}[]*-+!=.()/\"\"',:;?"});
        assertFocusTypeBlurValidation("field65", new String[]{"sdfs$#$# dsffs"},new String[]{"sdfs$#$#sffs"});
        assertFocusTypeBlurValidation("field66", new String[]{"abcABCD"},new String[]{"ABCabc"});
        assertFocusTypeBlurValidation("field67", new String[]{"(111)B-(222)A"},new String[]{"(12345)-(67890)"});
        assertFocusTypeBlurValidation("field68", new String[]{"A.66"},new String[]{"a.4"});
    }
}