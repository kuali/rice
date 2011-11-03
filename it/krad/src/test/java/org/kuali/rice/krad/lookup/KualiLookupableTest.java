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
package org.kuali.rice.krad.lookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.test.KRADTestCase;
import org.kuali.test.KRADTestConstants.TestConstants;

/**
 * This class tests the KualiLookupable methods.
 * 
 * 
 */
public class KualiLookupableTest extends KRADTestCase {
    private KualiLookupableImpl lookupableImpl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        lookupableImpl = new KualiLookupableImpl();
        lookupableImpl.setLookupableHelperService((LookupableHelperService) KRADServiceLocatorInternal.getService("lookupableHelperService"));
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
    	
    	Account account = (Account) KRADServiceLocatorWeb.getLookupService().findObjectBySearch(Account.class, lookupProps);
//        ObjectCode objCode = getObjectCodeService().getCountry(TestConstants.Data1.UNIVERSITY_FISCAL_YEAR, TestConstants.Data1.CHART_OF_ACCOUNTS_CODE, TestConstants.Data1.OBJECT_CODE);

        Map fieldConversions = new HashMap();
        lookupableImpl.setDocFormKey("8888888");
        lookupableImpl.setBackLocation(TestConstants.BASE_PATH + "ib.do");

        String returnUrl = lookupableImpl.getReturnUrl(account, fieldConversions, "kualiLookupable", null).constructCompleteHtmlTag();

        // check url has our doc form key
        checkURLContains("Lookup return url does not contain docFormKey", KRADConstants.DOC_FORM_KEY + "=8888888", returnUrl);

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
