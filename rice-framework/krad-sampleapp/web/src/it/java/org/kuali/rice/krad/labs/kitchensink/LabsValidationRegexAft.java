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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsValidationRegexAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page4#UifCompView-Page4
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page4#UifCompView-Page4";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Validation - Regex");
	}
	
	@Test
    public void testValidationRegexBookmark() throws Exception {
        testValidationRegex();
        passed();
    }

    @Test
    public void testValidationRegexNav() throws Exception {
        testValidationRegex();
        passed();
    }
    
    protected void testValidationRegex() throws Exception {
        testValidationRegex_FixedPoint();
        testValidationRegex_FloatingPoint();
        testValidationRegex_Integer();
        testValidationRegex_PhoneNumber();
        testValidationRegex_JavaClass();
        testValidationRegex_EmailAddress();
        testValidationRegex_Url();
        testValidationRegex_Date();
        testValidationRegex_DateBasic();
        testValidationRegex_Time();
        testValidationRegex_Time24H();
        testValidationRegex_Timestamp();
        testValidationRegex_Year();
        testValidationRegex_Month();
        testValidationRegex_ZipCode();
        testValidationRegex_AlphaNumeric();
        testValidationRegex_AlphaNumericOptions();
        testValidationRegex_AlphaWhitespaceCommas();
        testValidationRegex_AlphaAllAllowable();
        testValidationRegex_NoWhitespace();
        testValidationRegex_Charset();
        testValidationRegex_Numeric();
        testValidationRegex_Custom();
    }

    protected void testValidationRegex_FixedPoint() throws Exception {
        assertFocusTypeBlurError("field50","1qqqqq.qqqqqq");
        assertInvalidValidFieldValues("field50", new String[]{"123.123", "1234.4", "1234.434"}, new String[]{"123.67"});
    }

    protected void testValidationRegex_FloatingPoint() throws Exception {
        assertFocusTypeBlurError("field51","-1.0E");
        assertInvalidValidFieldValues("field51", new String[]{"127.", "1234()98"}, new String[]{"-123.67"});
    }

    protected void testValidationRegex_Integer() throws Exception {
        assertFocusTypeBlurError("field77","1.2");
        assertInvalidValidFieldValues("field77", new String[]{"127.", "1234.4123", "123E123"}, new String[]{"-123"});
    }

    protected void testValidationRegex_PhoneNumber() throws Exception {
        assertFocusTypeBlurError("field52","asddffgghj");
        assertInvalidValidFieldValues("field52",
                new String[]{"1271231234", "123-123-123", "12-12-123445", "1234-12-1234", "123.123.1234", "123-123-12345"},
                new String[]{"123-123-1234"});
    }

    protected void testValidationRegex_JavaClass() throws Exception {
        assertFocusTypeBlurError("field53"," :_");
        assertInvalidValidFieldValues("field53",
                new String[]{"127", "TestJava!@#Class", "Test JavaClass"}, new String[]{"TestJavaClass"});
    }

    protected void testValidationRegex_EmailAddress() throws Exception {
        assertFocusTypeBlurError("field54","as");
        assertInvalidValidFieldValues("field54",
                new String[]{"123@123.123", "email.com@emailServer", "emailemailServer@.com"}, new String[]{"email@emailServer.com"});
    }

    protected void testValidationRegex_Url() throws Exception {
        assertFocusTypeBlurError("field84","kuali.org");
        assertInvalidValidFieldValues("field84",
                new String[]{"www.google.com", "https:www.google.com", "ftp://www.google.comsdfa123!#@", "ftp:/www.google.coms"},
                new String[]{"ftp://www.google.com", "https://www.google.com", "http://www.google.com"});
    }

    protected void testValidationRegex_Date() throws Exception {
        assertFocusTypeBlurError("field55","1234");
        assertInvalidValidFieldValues("field55",
                new String[]{"12/12/2112 12:12:87 am", "12-12-2112 12:12 am", "12-12-2112 12:12", "12/12/2112 12:12",
                             "12-12-2112 12:12:78", "12 Sept", "Sept 12 12:12", "221299 12:12:13", "111222 12:12", "9/9/2012 12:12 am"},
                new String[]{"09/09/2012 12:12 pm", "090923", "Sept 12", "2034", "12/12/2012 23:12:59", "12-12-12 23:12:59",
                             "121212 23:12:32", "Sept 12 23:45:50", "2011 12:23:32"});
    }

    protected void testValidationRegex_DateBasic() throws Exception {
        assertFocusTypeBlurError("field75","aws");
        assertInvalidValidFieldValues("field75", new String[]{"12122012", "13-12-34", "12:12:2034"}, new String[]{"12-12-2034"});
    }

    protected void testValidationRegex_Time() throws Exception {
        assertFocusTypeBlurError("field82","12");
        assertInvalidValidFieldValues("field82", new String[]{"13:00:12", "09:00:", "3-00:12"}, new String[]{"3:00:34", "3:00"});
    }

    protected void testValidationRegex_Time24H() throws Exception {
        assertFocusTypeBlurError("field83","24");
        assertInvalidValidFieldValues("field83", new String[]{"24:00:12", "14:00:", "13:00:76"}, new String[]{"13:00:23", "23:00:12"});
    }

    protected void testValidationRegex_Timestamp() throws Exception {
        assertFocusTypeBlurError("field56", "1000");
        assertInvalidValidFieldValues("field56",
                new String[]{"1000-12-12 12:12:12.103", "2000/12/12 12-12-12.87", "2000/12/12 12-12-12.87", "2011-08-12 12:12:12"},
                new String[]{"2099-12-12 12:12:12.103"});
    }

    protected void testValidationRegex_Year() throws Exception {
        assertFocusTypeBlurError("field57","1599");
        assertInvalidValidFieldValues("field57", new String[]{"1599", "2200", "20000", "-202"}, new String[]{"2000"});
    }

    protected void testValidationRegex_Month() throws Exception {
        assertFocusTypeBlurError("field58","0");
        assertInvalidValidFieldValues("field58", new String[]{"0", "-12", "100"}, new String[]{"12"});
    }

    protected void testValidationRegex_ZipCode() throws Exception {
        assertFocusTypeBlurError("field61","360001");
        assertInvalidValidFieldValues("field61", new String[]{"123", "2341 12", "0-1231"}, new String[]{"12345"});
    }

    protected void testValidationRegex_AlphaNumeric() throws Exception {
        assertFocusTypeBlurError("field62","@#");
        assertInvalidValidFieldValues("field62", new String[]{"123 23 @#", "-asd123", "asd/123"}, new String[]{"asd123"});
    }

    protected void testValidationRegex_AlphaNumericOptions() throws Exception {
        assertFocusTypeBlurError("field63","2a#");
        assertInvalidValidFieldValues("field63", new String[]{"123^we", "-123_asd", "123 23 @#"}, new String[]{"as_de 456/123"});
    }

    protected void testValidationRegex_AlphaWhitespaceCommas() throws Exception {
        assertFocusTypeBlurError("field64","1@");
        assertInvalidValidFieldValues("field64", new String[]{"123^we", "asd_pqr", "asd/def"}, new String[]{"asd ,pqr"});
    }

    protected void testValidationRegex_AlphaAllAllowable() throws Exception {
        assertFocusTypeBlurError("field76","a2");
        assertInvalidValidFieldValues("field76", new String[]{"123", "`abcd`", "|abcd|", "~abcd~"}, new String[]{" ab_c d_ef "});
    }

    protected void testValidationRegex_NoWhitespace() throws Exception {
        assertFocusTypeBlurError("field65","a e");
        assertInvalidValidFieldValues("field65", new String[]{"123 ^we"}, new String[]{"123^we!@#^&*~:"});
    }

    protected void testValidationRegex_Charset() throws Exception {
        assertFocusTypeBlurError("field66","sdfa");
        assertInvalidValidFieldValues("field66", new String[]{"123 ^we", "123_^we", "abc ABC"}, new String[]{"aAbBcC"});
    }

    protected void testValidationRegex_Numeric() throws Exception {
        assertFocusTypeBlurError("field67","1234-a");
        assertInvalidValidFieldValues("field67", new String[]{"123 ^we", "123/10", "(123.00)"}, new String[]{"(12-3)"});
    }

    protected void testValidationRegex_Custom() throws Exception {
        assertFocusTypeBlurError("field68","4.a");
        assertInvalidValidFieldValues("field68", new String[]{ "123.123", "a.b", "123 qwe", "5.a", "a.0,b.4"}, new String[]{"a.0"});
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

}
