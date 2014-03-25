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
package edu.sampleu.krad.compview;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifComponentValidationRegexPatternAftBase extends WebDriverLegacyITBase  {

    /**
     * "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91"
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigation() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath(KITCHEN_SINK_XPATH);
        switchToWindow(KUALI_UIF_COMPONENTS_WINDOW_XPATH);
    }

    protected void testValidCharacterConstraintNav(JiraAwareFailable failable) throws Exception {
        navigation();
        testValidCharacterConstraint();
        passed();
    }

    protected void testValidCharacterConstraintBookmark(JiraAwareFailable failable) throws Exception {
        testValidCharacterConstraint();
        passed();
    }

    protected void testValidCharacterConstraint() throws Exception {
        waitAndClickByLinkText("Validation");
        waitAndClickByLinkText("Validation - Regex");
        waitAndClickById("UifCompView-Navigation4");

        //---------------------------------------------Fixed Point------------------------------//
        assertInvalidValidFieldValues("field50", new String[]{"123.123", "1234.4", "1234.434"}, new String[]{"123.67"});

        //---------------------------------------------Floating Point------------------------------//
        assertInvalidValidFieldValues("field51", new String[]{"127.", "1234()98"}, new String[]{"-123.67"});

        //---------------------------------------------Integer Pattern constraint------------------------------//
        assertInvalidValidFieldValues("field77", new String[]{"127.", "1234.4123", "123E123"}, new String[]{"-123"});

        //---------------------------------------------Phone Text------------------------------//
        assertInvalidValidFieldValues("field52", new String[]{"1271231234", "123-123-123", "12-12-123445", "1234-12-1234",
                "123.123.1234", "123-123-12345"}, new String[]{"123-123-1234"});

        //---------------------------------------------JavaClass Text------------------------------//
        assertInvalidValidFieldValues("field53", new String[]{"127", "TestJava!@#Class", "Test JavaClass"}, new String[]{"TestJavaClass"});

        //---------------------------------------------Email Text------------------------------//
        assertInvalidValidFieldValues("field54", new String[]{"123@123.123", "email.com@emailServer", "emailemailServer@.com"}, new String[]{"email@emailServer.com"});

        //---------------------------------------------URL pattern Text------------------------------//
        assertInvalidValidFieldValues("field84", new String[]{"www.google.com", "https:www.google.com", "ftp://www.google.comsdfa123!#@",
                "ftp:/www.google.coms"}, new String[]{"ftp://www.google.com", "https://www.google.com", "http://www.google.com"});

        //---------------------------------------------Date pattern Text------------------------------//
        assertInvalidValidFieldValues("field55", new String[]{"12/12/2112 12:12:87 am", "12-12-2112 12:12 am", "12-12-2112 12:12",
                "12/12/2112 12:12", "12-12-2112 12:12:78", "12 Sept", "Sept 12 12:12", "221299 12:12:13", "111222 12:12", "9/9/2012 12:12 am"},
                new String[]{"09/09/2012 12:12 pm", "090923", "Sept 12", "2034", "12/12/2012 23:12:59", "12-12-12 23:12:59", "121212 23:12:32",
                        "Sept 12 23:45:50", "2011 12:23:32"});


        //---------------------------------------------BasicDate pattern Text------------------------------//
        assertInvalidValidFieldValues("field75", new String[]{"12122012", "13-12-34", "12:12:2034"}, new String[]{"12-12-2034"});

        //---------------------------------------------Time12H Pattern Text------------------------------//
        assertInvalidValidFieldValues("field82", new String[]{"13:00:12", "09:00:", "3-00:12"}, new String[]{"3:00:34", "3:00"});

        //---------------------------------------------Time24H Pattern Text------------------------------//
        assertInvalidValidFieldValues("field83", new String[]{"24:00:12", "14:00:", "13:00:76"}, new String[]{"13:00:23", "23:00:12"});

        //---------------------------------------------Timestamp pattern Text------------------------------//
        assertInvalidValidFieldValues("field56", new String[]{"1000-12-12 12:12:12.103", "2000/12/12 12-12-12.87",
                "2000/12/12 12-12-12.87", "2011-08-12 12:12:12", /*"2999-12-12 12:12:12.103"*/ }, new String[]{"2099-12-12 12:12:12.103"});

        //---------------------------------------------Year Pattern Text------------------------------//
        assertInvalidValidFieldValues("field57", new String[]{"1599", "2200",
                "20000", "-202"}, new String[]{"2000"});

        //---------------------------------------------Month Pattern Text------------------------------//
        assertInvalidValidFieldValues("field58", new String[]{"0", "-12", "100"}, new String[]{"12"});

        //---------------------------------------------ZipCode Pattern Text------------------------------//
        assertInvalidValidFieldValues("field61", new String[]{"123", "2341 12", "0-1231"}, new String[]{"12345"});

        //---------------------------------------------Alpha Numeric w/o options Text------------------------------//
        assertInvalidValidFieldValues("field62", new String[]{"123 23 @#", "-asd123", "asd/123"}, new String[]{"asd123"});

        //---------------------------------------------Alpha Numeric with options Text------------------------------//
        assertInvalidValidFieldValues("field63", new String[]{"123^we", "-123_asd", "123 23 @#"}, new String[]{"as_de 456/123"});

        //---------------------------------------------Alpha with Whitespace and commas Text------------------------------//
        assertInvalidValidFieldValues("field64", new String[]{"123^we", "asd_pqr", "asd/def"}, new String[]{"asd ,pqr"});

        //---------------------------------------------AlphaPatterrn with disallowed charset Text------------------------------//
        assertInvalidValidFieldValues("field76", new String[]{"123", "`abcd`", "|abcd|", "~abcd~"}, new String[]{" ab_c d_ef "});

        //---------------------------------------------Anything with No Whitespace Text------------------------------//
        assertInvalidValidFieldValues("field65", new String[]{"123 ^we"}, new String[]{"123^we!@#^&*~:"});

        //---------------------------------------------CharacterSet Text------------------------------//
        assertInvalidValidFieldValues("field66", new String[]{"123 ^we", "123_^we", "abc ABC"}, new String[]{"aAbBcC"});

        //---------------------------------------------Numeric Character Text------------------------------//
        assertInvalidValidFieldValues("field67", new String[]{"123 ^we", "123/10", "(123.00)"}, new String[]{"(12-3)"});

        //---------------------------------------------Valid Chars Custom Text------------------------------//
        assertInvalidValidFieldValues("field68", new String[]{ "123.123", "a.b", "123 qwe", "5.a", "a.0,b.4"}, new String[]{"a.0"});
    }

    private boolean isErrorAttributeTrue(String fieldName) throws Exception {
        Thread.sleep(500);
        boolean valid = false;

        for (int second = 0; second < 5; second++) {
            if ((valid = validateErrorAttribute(fieldName)) == true) {
                break;
            }
        }

        return valid;
    }

    private boolean validateErrorAttribute(String fieldName) throws InterruptedException {
        try {
            WebElement field = findElement(By.name(fieldName));
            return "true".equals(field.getAttribute("aria-invalid"));
        } catch (Exception e) {
            // don't fail here, we're in a loop let the caller decide when to fail
        }

        Thread.sleep(1000);

        return false;
    }

    private void assertInvalidValidFieldValues(String fieldNameToTest, String[] invalids, String[] valids) throws Exception {
        for (String invalid : invalids) {
            clearTextByName(fieldNameToTest);
            waitAndTypeByName(fieldNameToTest, invalid);
            fireEvent(fieldNameToTest, "blur");
            if (!isErrorAttributeTrue(fieldNameToTest)) {
                jiraAwareFail(invalid + " expected to be invalid for field name " + fieldNameToTest);
            }
            clearTextByName(fieldNameToTest);
        }

        for (String valid : valids) {
            clearTextByName(fieldNameToTest);
            waitAndTypeByName(fieldNameToTest, valid);
            fireEvent(fieldNameToTest, "blur");
            if (isErrorAttributeTrue(fieldNameToTest)) {
                jiraAwareFail(valid + " expected to be valid for field name " + fieldNameToTest);
            }
            clearTextByName(fieldNameToTest);
        }
    }
}