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

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests that regex validation works as expected on input fields where it is configured
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIFComponentValidationRegexPatternIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    public void testValidCharacterConstraint() throws Exception {
        
        /*
         *  Timestamp pattern validation message says it allows years from 1900 - 2099 
         *  In fact it also allows 2999 as the upper limit. This needs to be sorted out.
         *  Test failing this condition is commented in the below code section for Timestamp Validation. Once resolved can be uncommented  
         *  
         */
        
        selenium.open(System.getProperty("remote.public.url"));
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("50000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Uif Components (Kitchen Sink)");
        selenium.waitForPageToLoad("50000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("link=Validation - Regex");
        //selenium.waitForPageToLoad("30000");
        Thread.sleep(5000);
        
       
        //---------------------------------------------Fixed Point------------------------------//
        clearText("//input[@name='field50']");
        selenium.type("//input[@name='field50']", "127.344");
        selenium.focus("//input[@name='field51']");
        Thread.sleep(100);               
        assertTrue(selenium.isTextPresent("Must be a positive fixed point number, with 5 max digits and 2 digits to the right of the decimal point"));
        
        clearText("//input[@name='field50']"); 
        selenium.type("//input[@name='field50']", "1234.4"); 
        selenium.focus("//input[@name='field51']");
        Thread.sleep(100);  
        assertTrue(selenium.isTextPresent("Must be a positive fixed point number, with 5 max digits and 2 digits to the right of the decimal point"));
        
        clearText("//input[@name='field50']");
        selenium.type("//input[@name='field50']", "1234.434"); 
        selenium.focus("//input[@name='field51']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a positive fixed point number, with 5 max digits and 2 digits to the right of the decimal point"));
        
        clearText("//input[@name='field50']");
        selenium.type("//input[@name='field50']", "123.67");        
        selenium.focus("//input[@name='field51']");      
        Thread.sleep(100);        
        assertTrue(! selenium.isTextPresent("Must be a positive fixed point number, with 5 max digits and 2 digits to the right of the decimal point"));
       
        //---------------------------------------------Floating Point------------------------------//
        clearText("//input[@name='field51']");
        selenium.type("//input[@name='field51']", "127.");
        selenium.focus("//input[@name='field77']");
        Thread.sleep(100);               
        assertTrue(selenium.isTextPresent("Must be a positive or negative number, with any number of digits to the right of the decimal."));
        
        clearText("//input[@name='field51']");
        selenium.type("//input[@name='field51']", "1234.4123"); 
        selenium.focus("//input[@name='field77']");
        Thread.sleep(100);  
        assertTrue(! selenium.isTextPresent("Must be a positive or negative number, with any number of digits to the right of the decimal."));
        
        clearText("//input[@name='field51']");
        selenium.type("//input[@name='field51']", "1234()98"); 
        selenium.focus("//input[@name='field77']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a positive or negative number, with any number of digits to the right of the decimal."));
        
        clearText("//input[@name='field51']");
        selenium.type("//input[@name='field51']", "-123.67");        
        selenium.focus("//input[@name='field77']");      
        Thread.sleep(100);        
        assertTrue(! selenium.isTextPresent("Must be a positive or negative number, with any number of digits to the right of the decimal."));

        
      //---------------------------------------------Integer Pattern constraint------------------------------//
        clearText("//input[@name='field77']");
        selenium.type("//input[@name='field77']", "127.");
        selenium.focus("//input[@name='field52']");
        Thread.sleep(100);               
        assertTrue(selenium.isTextPresent("Must be a positive or negative whole number"));
        
        clearText("//input[@name='field77']");
        selenium.type("//input[@name='field77']", "1234.4123"); 
        selenium.focus("//input[@name='field52']");
        Thread.sleep(100);  
        assertTrue(selenium.isTextPresent("Must be a positive or negative whole number"));
        
        clearText("//input[@name='field77']");
        selenium.type("//input[@name='field77']", "123E123"); 
        selenium.focus("//input[@name='field52']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a positive or negative whole number"));
        
        clearText("//input[@name='field77']");
        selenium.type("//input[@name='field77']", "-123");        
        selenium.focus("//input[@name='field52']");      
        Thread.sleep(100);        
        assertTrue(! selenium.isTextPresent("Must be a positive or negative whole number"));
        
        //---------------------------------------------Phone Text------------------------------//
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "1271231234");
        selenium.focus("//input[@name='field53']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "123-123-123"); 
        selenium.focus("//input[@name='field53']");
        Thread.sleep(100);  
        assertTrue(selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "12-12-123445"); 
        selenium.focus("//input[@name='field53']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "1234-12-1234"); 
        selenium.focus("//input[@name='field53']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "123.123.1234"); 
        selenium.focus("//input[@name='field53']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "123-123-12345"); 
        selenium.focus("//input[@name='field53']");
        Thread.sleep(100);       
        assertTrue(selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        clearText("//input[@name='field52']");
        selenium.type("//input[@name='field52']", "123-123-1234");        
        selenium.focus("//input[@name='field53']");      
        Thread.sleep(100);        
        assertTrue(! selenium.isTextPresent("Must be a phone number, in the format of ###-###-####."));
        
        //---------------------------------------------JavaClass Text------------------------------//
        clearText("//input[@name='field53']");
        selenium.type("//input[@name='field53']", "127");
        selenium.focus("//input[@name='field54']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid Java class name."));
        
        clearText("//input[@name='field53']");
        selenium.type("//input[@name='field53']", "TestJava!@#Class");
        selenium.focus("//input[@name='field54']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid Java class name."));
        
        clearText("//input[@name='field53']");
        selenium.type("//input[@name='field53']", "Test JavaClass"); 
        selenium.focus("//input[@name='field54']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid Java class name."));
        
        clearText("//input[@name='field53']");
        selenium.type("//input[@name='field53']", "Test JavaClass"); 
        selenium.focus("//input[@name='field54']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid Java class name."));
        
        clearText("//input[@name='field53']");
        selenium.type("//input[@name='field53']", "TestJavaClass");        
        selenium.focus("//input[@name='field54']");      
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid Java class name."));
        
        //---------------------------------------------Email Text------------------------------//
        clearText("//input[@name='field54']");
        selenium.type("//input[@name='field54']", "123@123.123");
        selenium.focus("//input[@name='field84']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a properly formatted email address."));
        
        clearText("//input[@name='field54']");
        selenium.type("//input[@name='field54']", "email.com@emailServer");
        selenium.focus("//input[@name='field84']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a properly formatted email address."));
        
        clearText("//input[@name='field54']");
        selenium.type("//input[@name='field54']", "emailemailServer@.com"); 
        selenium.focus("//input[@name='field84']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a properly formatted email address."));
        
        clearText("//input[@name='field54']");
        selenium.type("//input[@name='field54']", "email@emailServercom"); 
        selenium.focus("//input[@name='field84']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a properly formatted email address."));
        
        clearText("//input[@name='field54']");
        selenium.type("//input[@name='field54']", "email@emailServer.com");        
        selenium.focus("//input[@name='field84']");      
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a properly formatted email address."));
        
        
        //---------------------------------------------URL pattern Text------------------------------//
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "www.google.com");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "https:www.google.com");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "ftp://www.google.comsdfa123!#@");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "ftp:/www.google.coms");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "ftp://www.google.com");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "https://www.google.com");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        clearText("//input[@name='field84']");
        selenium.type("//input[@name='field84']", "http://www.google.com");
        selenium.focus("//input[@name='field55']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid url beginning with http, https, or ftp"));
        
        
        //---------------------------------------------Date pattern Text------------------------------//
        //-------------invalid formats
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12/12/2112 12:12:87 am");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));

        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12-12-2112 12:12 am");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
      
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12-12-2112 12:12");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
     
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12/12/2112 12:12");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12-12-2112 12:12:78");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
   
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12 Sept");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
   
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "Sept 12 12:12");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
   
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "221299 12:12:13");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
   
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "111222 12:12");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));

        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "9/9/2012 12:12 am");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        //-------------valid formats      
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "09/09/2012 12:12 pm");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "090923");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "Sept 12");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
   
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "2034");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12/12/2012 23:12:59");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "12-12-12 23:12:59");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "121212 23:12:32");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "Sept 12 23:45:50");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        clearText("//input[@name='field55']");
        selenium.type("//input[@name='field55']", "2011 12:23:32");
        selenium.focus("//input[@name='field75']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yyyy hh:mm a, MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, MMddyy, MMMM dd, yyyy, MM/dd/yy HH:mm:ss, MM/dd/yyyy HH:mm:ss, MM-dd-yy HH:mm:ss, MMddyy HH:mm:ss, MMMM dd HH:mm:ss, yyyy HH:mm:ss"));
        
        
        //---------------------------------------------BasicDate pattern Text------------------------------//
        clearText("//input[@name='field75']");
        selenium.type("//input[@name='field75']", "12122012");
        selenium.focus("//input[@name='field82']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy"));
        
        clearText("//input[@name='field75']");
        selenium.type("//input[@name='field75']", "13-12-34");
        selenium.focus("//input[@name='field82']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy"));
        
        clearText("//input[@name='field75']");
        selenium.type("//input[@name='field75']", "12:12:2034");
        selenium.focus("//input[@name='field82']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy"));
        
        clearText("//input[@name='field75']");
        selenium.type("//input[@name='field75']", "12-12-2034");
        selenium.focus("//input[@name='field82']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date in the following format(s): MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy"));
        
        
        //---------------------------------------------Time12H Pattern Text------------------------------//
        clearText("//input[@name='field82']");
        selenium.type("//input[@name='field82']", "13:00:12");
        selenium.focus("//input[@name='field83']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid 12 hour time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field82']");
        selenium.type("//input[@name='field82']", "09:00:");
        selenium.focus("//input[@name='field83']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid 12 hour time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field82']");
        selenium.type("//input[@name='field82']", "3-00:12");
        selenium.focus("//input[@name='field83']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid 12 hour time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field82']");
        selenium.type("//input[@name='field82']", "3:00:34");
        selenium.focus("//input[@name='field83']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid 12 hour time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field82']");
        selenium.type("//input[@name='field82']", "3:00");
        selenium.focus("//input[@name='field83']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid 12 hour time in HH:mm format, seconds are optional"));
       
        
        //---------------------------------------------Time24H Pattern Text------------------------------//
        clearText("//input[@name='field83']");
        selenium.type("//input[@name='field83']", "24:00:12");
        selenium.focus("//input[@name='field56']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid 24 hour (0-23) time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field83']");
        selenium.type("//input[@name='field83']", "14:00:");
        selenium.focus("//input[@name='field56']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid 24 hour (0-23) time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field83']");
        selenium.type("//input[@name='field83']", "13:00:76");
        selenium.focus("//input[@name='field56']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a valid 24 hour (0-23) time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field83']");
        selenium.type("//input[@name='field83']", "13:00:23");
        selenium.focus("//input[@name='field56']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid 24 hour (0-23) time in HH:mm format, seconds are optional"));
        
        clearText("//input[@name='field83']");
        selenium.type("//input[@name='field83']", "23:00:12");
        selenium.focus("//input[@name='field56']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a valid 24 hour (0-23) time in HH:mm format, seconds are optional"));
       
        
        
        
        //---------------------------------------------Timestamp pattern Text------------------------------//
        clearText("//input[@name='field56']");
        selenium.type("//input[@name='field56']", "1000-12-12 12:12:12.103");
        selenium.focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        
        clearText("//input[@name='field56']");
        selenium.type("//input[@name='field56']", "2000/12/12 12-12-12.87");
        selenium.focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));

        clearText("//input[@name='field56']");
        selenium.type("//input[@name='field56']", "2000/12/12 12-12-12.87");
        selenium.focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        
        clearText("//input[@name='field56']");
        selenium.type("//input[@name='field56']", "2011-08-12 12:12:12");
        selenium.focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        
        //--------this should not be allowed
        /*
        clearTimeStampText();
        selenium.type("//input[@name='field56']", "2999-12-12 12:12:12.103");
        selenium.focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        
        */
        clearText("//input[@name='field56']");
        selenium.type("//input[@name='field56']", "2099-12-12 12:12:12.103");
        selenium.focus("//input[@name='field57']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a date/time in the format of yyyy-mm-dd hh:mm:ss.ms, between the years of 1900 and 2099, inclusive. \"ms\" represents milliseconds, and must be included."));
        
        
        //---------------------------------------------Year Pattern Text------------------------------//
        clearText("//input[@name='field57']");
        selenium.type("//input[@name='field57']", "1599");
        selenium.focus("//input[@name='field58']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a four digit year between 1600 to 2199, inclusive."));
        
        clearText("//input[@name='field57']");
        selenium.type("//input[@name='field57']", "2200");
        selenium.focus("//input[@name='field58']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a four digit year between 1600 to 2199, inclusive."));
        
        clearText("//input[@name='field57']");
        selenium.type("//input[@name='field57']", "20000"); 
        selenium.focus("//input[@name='field58']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a four digit year between 1600 to 2199, inclusive."));
        
        clearText("//input[@name='field57']");
        selenium.type("//input[@name='field57']", "-202"); 
        selenium.focus("//input[@name='field58']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a four digit year between 1600 to 2199, inclusive."));
        
        clearText("//input[@name='field57']");
        selenium.type("//input[@name='field57']", "2000");        
        selenium.focus("//input[@name='field58']");      
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a four digit year between 1600 to 2199, inclusive."));
        
        //---------------------------------------------Month Pattern Text------------------------------//
        clearText("//input[@name='field58']");
        selenium.type("//input[@name='field58']", "0");
        selenium.focus("//input[@name='field61']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be 1 to 12, representing a month."));
        
        clearText("//input[@name='field58']");
        selenium.type("//input[@name='field58']", "-12");
        selenium.focus("//input[@name='field61']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be 1 to 12, representing a month."));
        
        clearText("//input[@name='field58']");
        selenium.type("//input[@name='field58']", "100"); 
        selenium.focus("//input[@name='field61']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be 1 to 12, representing a month."));
        
        clearText("//input[@name='field58']");
        selenium.type("//input[@name='field58']", "12"); 
        selenium.focus("//input[@name='field61']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be 1 to 12, representing a month."));
        
        
        //---------------------------------------------ZipCode Pattern Text------------------------------//
       
        clearText("//input[@name='field61']");
        selenium.type("//input[@name='field61']", "123");
        selenium.focus("//input[@name='field62']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a ZIP code. ZIP + 4 codes are also accepted."));
        
        clearText("//input[@name='field61']");
        selenium.type("//input[@name='field61']", "2341 12");
        selenium.focus("//input[@name='field62']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a ZIP code. ZIP + 4 codes are also accepted."));
        
        clearText("//input[@name='field61']");
        selenium.type("//input[@name='field61']", "0-1231");
        selenium.focus("//input[@name='field62']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must be a ZIP code. ZIP + 4 codes are also accepted."));
        
        clearText("//input[@name='field61']");
        selenium.type("//input[@name='field61']", "12345");
        selenium.focus("//input[@name='field62']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must be a ZIP code. ZIP + 4 codes are also accepted."));
       
        
        //---------------------------------------------Alpha Numeric w/o options Text------------------------------//
        clearText("//input[@name='field62']");
        selenium.type("//input[@name='field62']", "123 23 @#");
        selenium.focus("//input[@name='field63']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alphanumeric characters "));
        
        clearText("//input[@name='field62']");
        selenium.type("//input[@name='field62']", "-asd123");
        selenium.focus("//input[@name='field63']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alphanumeric characters "));
        
        clearText("//input[@name='field62']");
        selenium.type("//input[@name='field62']", "asd/123");
        selenium.focus("//input[@name='field63']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alphanumeric characters "));
        
        clearText("//input[@name='field62']");
        selenium.type("//input[@name='field62']", "asd123");
        selenium.focus("//input[@name='field63']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Can only be alphanumeric characters "));
                
      //---------------------------------------------Alpha Numeric with options Text------------------------------//
        clearText("//input[@name='field63']");
        selenium.type("//input[@name='field63']", "123^we");
        selenium.focus("//input[@name='field64']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alphanumeric characters, whitespace, underscores, forward slashes "));
        
        clearText("//input[@name='field63']");
        selenium.type("//input[@name='field63']", "-123_asd");
        selenium.focus("//input[@name='field64']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alphanumeric characters, whitespace, underscores, forward slashes "));
                       
        clearText("//input[@name='field63']");
        selenium.type("//input[@name='field63']", "123 23 @#");
        selenium.focus("//input[@name='field64']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alphanumeric characters, whitespace, underscores, forward slashes "));
        
        clearText("//input[@name='field63']");
        selenium.type("//input[@name='field63']", "as_de 456/123");
        selenium.focus("//input[@name='field64']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Can only be alphanumeric characters, whitespace, underscores, forward slashes "));
        
        //---------------------------------------------Alpha with Whitespace and commas Text------------------------------//
        clearText("//input[@name='field64']");
        selenium.type("//input[@name='field64']", "123^we");
        selenium.focus("//input[@name='field76']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, commas"));
       
        clearText("//input[@name='field64']");
        selenium.type("//input[@name='field64']", "asd_pqr");
        selenium.focus("//input[@name='field76']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, commas"));
        
        clearText("//input[@name='field64']");
        selenium.type("//input[@name='field64']", "asd/def");
        selenium.focus("//input[@name='field76']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, commas"));
        
        clearText("//input[@name='field64']");
        selenium.type("//input[@name='field64']", "asd ,pqr");
        selenium.focus("//input[@name='field76']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Can only be alpha characters, whitespace, commas"));
        
        
        //---------------------------------------------AlphaPatterrn with disallowed charset Text------------------------------//
        clearText("//input[@name='field76']");
        selenium.type("//input[@name='field76']", "123");
        selenium.focus("//input[@name='field65']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, underscores, periods, parentheses, dollar signs, forward slashes, double quotes, apostrophes, commas, colons, null, question marks, exclaimation marks, dashes, plus signs, equals signs, *, @, %, #"));
     
        clearText("//input[@name='field76']");
        selenium.type("//input[@name='field76']", "<abcd>");
        selenium.focus("//input[@name='field65']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, underscores, periods, parentheses, dollar signs, forward slashes, double quotes, apostrophes, commas, colons, null, question marks, exclaimation marks, dashes, plus signs, equals signs, *, @, %, #"));
        
        clearText("//input[@name='field76']");
        selenium.type("//input[@name='field76']", "|abcd|");
        selenium.focus("//input[@name='field65']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, underscores, periods, parentheses, dollar signs, forward slashes, double quotes, apostrophes, commas, colons, null, question marks, exclaimation marks, dashes, plus signs, equals signs, *, @, %, #"));
        
        clearText("//input[@name='field76']");
        selenium.type("//input[@name='field76']", "~abcd~");
        selenium.focus("//input[@name='field65']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be alpha characters, whitespace, underscores, periods, parentheses, dollar signs, forward slashes, double quotes, apostrophes, commas, colons, null, question marks, exclaimation marks, dashes, plus signs, equals signs, *, @, %, #"));
        
        clearText("//input[@name='field76']");
        selenium.type("//input[@name='field76']", " ab_c d_ef ");
        selenium.focus("//input[@name='field65']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Can only be alpha characters, whitespace, underscores, periods, parentheses, dollar signs, forward slashes, double quotes, apostrophes, commas, colons, null, question marks, exclaimation marks, dashes, plus signs, equals signs, *, @, %, #"));
        
        
        //---------------------------------------------Anything with No Whitespace Text------------------------------//
        clearText("//input[@name='field65']");
        selenium.type("//input[@name='field65']", "123 ^we");
        selenium.focus("//input[@name='field66']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Must not contain any whitespace (spaces, returns, etc)"));
       
        clearText("//input[@name='field65']");
        selenium.type("//input[@name='field65']", "123^we!@#^&*~:");
        selenium.focus("//input[@name='field66']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Must not contain any whitespace (spaces, returns, etc)"));
        
        //---------------------------------------------CharacterSet Text------------------------------//
        clearText("//input[@name='field66']");
        selenium.type("//input[@name='field66']", "123 ^we");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can be any of the following characters: abcABC"));
        
        clearText("//input[@name='field66']");
        selenium.type("//input[@name='field66']", "123_^we");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can be any of the following characters: abcABC"));
        
        clearText("//input[@name='field66']");
        selenium.type("//input[@name='field66']", "abc ABC");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can be any of the following characters: abcABC"));
        
        clearText("//input[@name='field66']");
        selenium.type("//input[@name='field66']", "aAbBcC");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Can be any of the following characters: abcABC"));
        
        //---------------------------------------------Numeric Character Text------------------------------//
        clearText("//input[@name='field67']");
        selenium.type("//input[@name='field67']", "123 ^we");
        selenium.focus("//input[@name='field68']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be numeric characters, parentheses, dashes"));
        
        clearText("//input[@name='field67']");
        selenium.type("//input[@name='field67']", "123/10");
        selenium.focus("//input[@name='field68']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be numeric characters, parentheses, dashes"));
        
        clearText("//input[@name='field67']");
        selenium.type("//input[@name='field67']", "(123.00)");
        selenium.focus("//input[@name='field68']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("Can only be numeric characters, parentheses, dashes"));
        
        clearText("//input[@name='field67']");
        selenium.type("//input[@name='field67']", "(12-3)");
        selenium.focus("//input[@name='field68']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("Can only be numeric characters, parentheses, dashes"));
        
        //---------------------------------------------Valid Chars Custom Text------------------------------//
        clearText("//input[@name='field68']");
        selenium.type("//input[@name='field68']", "123.123");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("only 1 alpha character followed by a period and then followed by 1 number (a.8, b.0, etc)"));
        
        clearText("//input[@name='field68']");
        selenium.type("//input[@name='field68']", "a.b");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("only 1 alpha character followed by a period and then followed by 1 number (a.8, b.0, etc)"));
        
        clearText("//input[@name='field68']");
        selenium.type("//input[@name='field68']", "123 qwe");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("only 1 alpha character followed by a period and then followed by 1 number (a.8, b.0, etc)"));
        
        clearText("//input[@name='field68']");
        selenium.type("//input[@name='field68']", "5.a");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("only 1 alpha character followed by a period and then followed by 1 number (a.8, b.0, etc)"));
        
        clearText("//input[@name='field68']");
        selenium.type("//input[@name='field68']", "a.0,b.4");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(selenium.isTextPresent("only 1 alpha character followed by a period and then followed by 1 number (a.8, b.0, etc)"));
        
        
        clearText("//input[@name='field68']");
        selenium.type("//input[@name='field68']", "a.0");
        selenium.focus("//input[@name='field67']");
        Thread.sleep(100);
        assertTrue(! selenium.isTextPresent("only 1 alpha character followed by a period and then followed by 1 number (a.8, b.0, etc)"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
    
    public void clearText(String field) throws Exception {
        selenium.focus(field);
        selenium.type(field, "");  
        Thread.sleep(100); 
    }
}
