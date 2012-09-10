/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.travel.krad.test;

import edu.samplu.common.ITUtil;
import junit.framework.Assert;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

/**
 * tests that regex validation works as expected on input fields where it is configured
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIFComponentValidationRegexPatternIT extends UpgradedSeleniumITBase {
    
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91";
    }

    @Test
    public void testValidCharacterConstraint() throws Exception {

        /*
         *  Timestamp pattern validation message says it allows years from 1900 - 2099 
         *  In fact it also allows 2999 as the upper limit. This needs to be sorted out.
         *  Test failing this condition is commented in the below code section for Timestamp Validation. Once resolved can be uncommented  
         *  
         */
     
        waitAndClick("//a[contains(text(),'Validation - Regex')]");
        ITUtil.waitForElement(selenium, "name=field50");
        
        //---------------------------------------------Fixed Point------------------------------//

       
        waitAndType("name=field50", "123.123");
        fireEvent("name=field50", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field50']", "1234.4");
        fireEvent("name=field50", "blur");
        validateErrorImage(true);

     
        waitAndType("//input[@name='field50']", "1234.434");
        fireEvent("name=field50", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field50']", "123.67");
        fireEvent("name=field50", "blur");
        validateErrorImage(false);

        //---------------------------------------------Floating Point------------------------------//
       
        waitAndType("//input[@name='field51']", "127.");
        fireEvent("//input[@name='field51']", "blur");
        validateErrorImage(true);
         
        waitAndType("//input[@name='field51']", "1234()98");
        fireEvent("//input[@name='field51']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field51']", "-123.67");
        fireEvent("//input[@name='field51']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Integer Pattern constraint------------------------------//
        
        waitAndType("//input[@name='field77']", "127.");
        fireEvent("//input[@name='field77']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field77']", "1234.4123");
        fireEvent("//input[@name='field77']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field77']", "123E123");
        fireEvent("//input[@name='field77']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field77']", "-123");
        fireEvent("//input[@name='field77']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Phone Text------------------------------//
        waitAndType("//input[@name='field52']", "1271231234");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field52']", "123-123-123");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field52']", "12-12-123445");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field52']", "1234-12-1234");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field52']", "123.123.1234");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field52']", "123-123-12345");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field52']", "123-123-1234");
        fireEvent("//input[@name='field52']", "blur");
        validateErrorImage(false);

        //---------------------------------------------JavaClass Text------------------------------//
        waitAndType("//input[@name='field53']", "127");
        fireEvent("//input[@name='field53']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field53']", "TestJava!@#Class");
        fireEvent("//input[@name='field53']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field53']", "Test JavaClass");
        fireEvent("//input[@name='field53']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field53']", "Test JavaClass");
        fireEvent("//input[@name='field53']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field53']", "TestJavaClass");
        fireEvent("//input[@name='field53']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Email Text------------------------------//
        waitAndType("//input[@name='field54']", "123@123.123");
        fireEvent("//input[@name='field54']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field54']", "email.com@emailServer");
        fireEvent("//input[@name='field54']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field54']", "emailemailServer@.com");
        fireEvent("//input[@name='field54']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field54']", "email@emailServercom");
        fireEvent("//input[@name='field54']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field54']", "email@emailServer.com");
        fireEvent("//input[@name='field54']", "blur");
        validateErrorImage(false);

        //---------------------------------------------URL pattern Text------------------------------//
        waitAndType("//input[@name='field84']", "www.google.com");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field84']", "https:www.google.com");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field84']", "ftp://www.google.comsdfa123!#@");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field84']", "ftp:/www.google.coms");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field84']", "ftp://www.google.com");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field84']", "https://www.google.com");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field84']", "http://www.google.com");
        fireEvent("//input[@name='field84']", "blur");
        validateErrorImage(false);
       

        //---------------------------------------------Date pattern Text------------------------------//
        //-------------invalid formats
        waitAndType("//input[@name='field55']", "12/12/2112 12:12:87 am");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "12-12-2112 12:12 am");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "12-12-2112 12:12");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "12/12/2112 12:12");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "12-12-2112 12:12:78");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "12 Sept");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "Sept 12 12:12");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "221299 12:12:13");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "111222 12:12");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field55']", "9/9/2012 12:12 am");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(true);

        //-------------valid formats      
        waitAndType("//input[@name='field55']", "09/09/2012 12:12 pm");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field55']", "090923");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);
        
        
        waitAndType("//input[@name='field55']", "Sept 12");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);
        
        waitAndType("//input[@name='field55']", "2034");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field55']", "12/12/2012 23:12:59");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);
        
        waitAndType("//input[@name='field55']", "12-12-12 23:12:59");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);
        
        waitAndType("//input[@name='field55']", "121212 23:12:32");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);
        
        waitAndType("//input[@name='field55']", "Sept 12 23:45:50");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field55']", "2011 12:23:32");
        fireEvent("//input[@name='field55']", "blur");
        validateErrorImage(false);
        
        //---------------------------------------------BasicDate pattern Text------------------------------//
        waitAndType("//input[@name='field75']", "12122012");
        fireEvent("//input[@name='field75']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field75']", "13-12-34");
        fireEvent("//input[@name='field75']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field75']", "12:12:2034");
        fireEvent("//input[@name='field75']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field75']", "12-12-2034");
        fireEvent("//input[@name='field75']", "blur");
        validateErrorImage(false);
 
        //---------------------------------------------Time12H Pattern Text------------------------------//
        waitAndType("//input[@name='field82']", "13:00:12");
        fireEvent("//input[@name='field82']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field82']", "09:00:");
        fireEvent("//input[@name='field82']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field82']", "3-00:12");
        fireEvent("//input[@name='field82']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field82']", "3:00:34");
        fireEvent("//input[@name='field82']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field82']", "3:00");
        fireEvent("//input[@name='field82']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Time24H Pattern Text------------------------------//
        waitAndType("//input[@name='field83']", "24:00:12");
        fireEvent("//input[@name='field83']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field83']", "14:00:");
        fireEvent("//input[@name='field83']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field83']", "13:00:76");
        fireEvent("//input[@name='field83']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field83']", "13:00:23");
        fireEvent("//input[@name='field83']", "blur");
        validateErrorImage(false);

        waitAndType("//input[@name='field83']", "23:00:12");
        fireEvent("//input[@name='field83']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Timestamp pattern Text------------------------------//
        waitAndType("//input[@name='field56']", "1000-12-12 12:12:12.103");
        fireEvent("//input[@name='field56']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field56']", "2000/12/12 12-12-12.87");
        fireEvent("//input[@name='field56']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field56']", "2000/12/12 12-12-12.87");
        fireEvent("//input[@name='field56']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field56']", "2011-08-12 12:12:12");
        fireEvent("//input[@name='field56']", "blur");
        validateErrorImage(true);

        //--------this should not be allowed
        /*
        clearTimeStampText();
        waitAndType("//input[@name='field56']", "2999-12-12 12:12:12.103");
        focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        
        */
        waitAndType("//input[@name='field56']", "2099-12-12 12:12:12.103");
        fireEvent("//input[@name='field56']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Year Pattern Text------------------------------//
        waitAndType("//input[@name='field57']", "1599");
        fireEvent("//input[@name='field57']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field57']", "2200");
        fireEvent("//input[@name='field57']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field57']", "20000");
        fireEvent("//input[@name='field57']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field57']", "-202");
        fireEvent("//input[@name='field57']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field57']", "2000");
        fireEvent("//input[@name='field57']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Month Pattern Text------------------------------//
        waitAndType("//input[@name='field58']", "0");
        fireEvent("//input[@name='field58']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field58']", "-12");
        fireEvent("//input[@name='field58']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field58']", "100");
        fireEvent("//input[@name='field58']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field58']", "12");
        fireEvent("//input[@name='field58']", "blur");
        validateErrorImage(false);

        //---------------------------------------------ZipCode Pattern Text------------------------------//

        waitAndType("//input[@name='field61']", "123");
        fireEvent("//input[@name='field61']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field61']", "2341 12");
        fireEvent("//input[@name='field61']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field61']", "0-1231");
        fireEvent("//input[@name='field61']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field61']", "12345");
        fireEvent("//input[@name='field61']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Alpha Numeric w/o options Text------------------------------//
        waitAndType("//input[@name='field62']", "123 23 @#");
        fireEvent("//input[@name='field62']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field62']", "-asd123");
        fireEvent("//input[@name='field62']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field62']", "asd/123");
        fireEvent("//input[@name='field62']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field62']", "asd123");
        fireEvent("//input[@name='field62']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Alpha Numeric with options Text------------------------------//
        waitAndType("//input[@name='field63']", "123^we");
        fireEvent("//input[@name='field63']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field63']", "-123_asd");
        fireEvent("//input[@name='field63']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field63']", "123 23 @#");
        fireEvent("//input[@name='field63']", "blur");

        waitAndType("//input[@name='field63']", "as_de 456/123");
        fireEvent("//input[@name='field63']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Alpha with Whitespace and commas Text------------------------------//
        waitAndType("//input[@name='field64']", "123^we");
        fireEvent("//input[@name='field64']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field64']", "asd_pqr");
        fireEvent("//input[@name='field64']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field64']", "asd/def");
        fireEvent("//input[@name='field64']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field64']", "asd ,pqr");
        fireEvent("//input[@name='field64']", "blur");
        validateErrorImage(false);

        //---------------------------------------------AlphaPatterrn with disallowed charset Text------------------------------//
        waitAndType("//input[@name='field76']", "123");
        fireEvent("//input[@name='field76']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field76']", "`abcd`");
        fireEvent("//input[@name='field76']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field76']", "|abcd|");
        fireEvent("//input[@name='field76']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field76']", "~abcd~");
        fireEvent("//input[@name='field76']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field76']", " ab_c d_ef ");
        fireEvent("//input[@name='field76']", "blur");
        validateErrorImage(false);

        //---------------------------------------------Anything with No Whitespace Text------------------------------//
        waitAndType("//input[@name='field65']", "123 ^we");
        fireEvent("//input[@name='field65']", "blur");
        validateErrorImage(true);

        waitAndType("//input[@name='field65']", "123^we!@#^&*~:");
        fireEvent("//input[@name='field65']", "blur");
        validateErrorImage(false);
        
        //---------------------------------------------CharacterSet Text------------------------------//
        waitAndType("//input[@name='field66']", "123 ^we");
        fireEvent("//input[@name='field66']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field66']", "123_^we");
        fireEvent("//input[@name='field66']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field66']", "abc ABC");
        fireEvent("//input[@name='field66']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field66']", "aAbBcC");
        fireEvent("//input[@name='field66']", "blur");
        validateErrorImage(false);
        
        //---------------------------------------------Numeric Character Text------------------------------//
        waitAndType("//input[@name='field67']", "123 ^we");
        fireEvent("//input[@name='field67']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field67']", "123/10");
        fireEvent("//input[@name='field67']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field67']", "(123.00)");
        fireEvent("//input[@name='field67']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field67']", "(12-3)");
        fireEvent("//input[@name='field67']", "blur");
        validateErrorImage(false);
        
        //---------------------------------------------Valid Chars Custom Text------------------------------//
        waitAndType("//input[@name='field68']", "123.123");
        fireEvent("//input[@name='field68']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field68']", "a.b");
        fireEvent("//input[@name='field68']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field68']", "123 qwe");
        fireEvent("//input[@name='field68']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field68']", "5.a");
        fireEvent("//input[@name='field68']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field68']", "a.0,b.4");
        fireEvent("//input[@name='field68']", "blur");
        validateErrorImage(true);
        
        waitAndType("//input[@name='field68']", "a.0");
        fireEvent("//input[@name='field68']", "blur");
        validateErrorImage(false);
        
    }
        
    public void validateErrorImage(boolean validateVisible) throws Exception {
        Thread.sleep(500);
        for (int second = 0;; second++) {
            if (second >= 5)
                Assert.fail("timeout");
            try {
                if (validateVisible) {
                    
                    if (selenium.isVisible("css=img.uif-validationImage"));
                        break;
                } else {
                    
                    if (! selenium.isVisible("css=img.uif-validationImage"))
                        break;
                }

            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        if (validateVisible) {
            Assert.assertTrue(selenium.isVisible("css=img.uif-validationImage"));
        } else {
            Assert.assertTrue(! selenium.isVisible("css=img.uif-validationImage"));
        }        
    }
}
