/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.lookup;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kuali.Constants;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

import edu.sampleu.travel.bo.TravelAccount;

/**
 * This class tests the KualiLookupable methods.
 * 
 * 
 */
@KNSWithTestSpringContext
public class KualiLookupableTest extends KNSTestBase {
    private KualiLookupableImpl lookupableImpl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        lookupableImpl = (KualiLookupableImpl) KNSServiceLocator.getKualiLookupable();
        lookupableImpl.setBusinessObjectClass(TravelAccount.class);
    }

    /**
     * Test that the return url for a business object is getting set correctly based on the defined return fields.
     * 
     * @throws Exception
     */
    @Test public void testReturnUrl() throws Exception {
    	Map<String, Object> lookupProps = new HashMap<String, Object>();
    	lookupProps.put("number", "a1");
    	lookupProps.put("name", "a1");
    	
    	TravelAccount account = (TravelAccount)KNSServiceLocator.getLookupService().findObjectBySearch(TravelAccount.class, lookupProps);
//        ObjectCode objCode = getObjectCodeService().getByPrimaryId(TestConstants.Data1.UNIVERSITY_FISCAL_YEAR, TestConstants.Data1.CHART_OF_ACCOUNTS_CODE, TestConstants.Data1.OBJECT_CODE);

        Map fieldConversions = new HashMap();
        lookupableImpl.setDocFormKey("8888888");
        lookupableImpl.setBackLocation(TestConstants.BASE_PATH + "ib.do");

        String returnUrl = lookupableImpl.getReturnUrl(account, fieldConversions, "kualiLookupable");

        // check url has our doc form key
        checkURLContains("Lookup return url does not contain docFormKey", Constants.DOC_FORM_KEY + "=8888888", returnUrl);

        // check url goes back to our back location
        assertTrue("Lookup return url does not go back to back location", returnUrl.startsWith(TestConstants.BASE_PATH + "ib.do"));

        assertEquals(returnUrl, "http://localhost:8080/ib.do?refreshCaller=kualiLookupable&number=a1&methodToCall=refresh&docFormKey=8888888");

        // check that field conversions are working correctly for keys
        fieldConversions.put("number", "myAccount[0].chartCode");

        returnUrl = lookupableImpl.getReturnUrl(account, fieldConversions, "kualiLookupable");

        // check keys have been mapped properly
        checkURLContains("Lookup return url does not map key", "myAccount%5B0%5D.chartCode=a1", returnUrl);
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
