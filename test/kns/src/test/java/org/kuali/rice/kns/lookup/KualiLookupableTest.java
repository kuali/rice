/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kns.lookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kns.service.KNSServiceLocatorInternal;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.test.document.bo.Account;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.test.KNSTestCase;
import org.kuali.test.KNSTestConstants.TestConstants;

/**
 * This class tests the KualiLookupable methods.
 * 
 * 
 */
public class KualiLookupableTest extends KNSTestCase {
    private KualiLookupableImpl lookupableImpl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        lookupableImpl = new KualiLookupableImpl();
        lookupableImpl.setLookupableHelperService((LookupableHelperService) KNSServiceLocatorInternal.getService("lookupableHelperService"));
        lookupableImpl.setBusinessObjectClass(Account.class);
    }

    /**
     * Test that the return url for a business object is getting set correctly based on the defined return fields.
     * 
     * @throws Exception
     */
    @Test public void testReturnUrl() throws Exception {
    	Map<String, String> lookupProps = new HashMap<String, String>();
    	lookupProps.put("number", "a1");
    	lookupProps.put("name", "a1");
    	
    	Account account = (Account) KNSServiceLocatorWeb.getLookupService().findObjectBySearch(Account.class, lookupProps);
//        ObjectCode objCode = getObjectCodeService().getCountry(TestConstants.Data1.UNIVERSITY_FISCAL_YEAR, TestConstants.Data1.CHART_OF_ACCOUNTS_CODE, TestConstants.Data1.OBJECT_CODE);

        Map fieldConversions = new HashMap();
        lookupableImpl.setDocFormKey("8888888");
        lookupableImpl.setBackLocation(TestConstants.BASE_PATH + "ib.do");

        String returnUrl = lookupableImpl.getReturnUrl(account, fieldConversions, "kualiLookupable", null).constructCompleteHtmlTag();

        // check url has our doc form key
        checkURLContains("Lookup return url does not contain docFormKey", KNSConstants.DOC_FORM_KEY + "=8888888", returnUrl);

        // check url goes back to our back location
        checkURLContains("Lookup return url does not go back to back location", TestConstants.BASE_PATH + "ib.do", returnUrl);

        assertEquals(returnUrl, "<a title=\"return valueAccount Number=a1 \" href=\"http://localhost:8080/ib.do?refreshCaller=kualiLookupable&number=a1&methodToCall=refresh&docFormKey=8888888\">return value</a>");

        // check that field conversions are working correctly for keys
        fieldConversions.put("number", "myAccount[0].chartCode");

        returnUrl = lookupableImpl.getReturnUrl(account, fieldConversions, "kualiLookupable", null).constructCompleteHtmlTag();

        // check keys have been mapped properly
        checkURLContains("Lookup return url does not map key", "myAccount[0].chartCode=a1", returnUrl);
    }


    /**
     * Checks the url string contains a substring.
     * 
     * @param message
     * @param containString
     * @param url
     */
    private void checkURLContains(String message, String containString, String url) {
        assertTrue(message, url.indexOf(containString) > 0);
    }
}
