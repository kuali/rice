/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.krad.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.test.KNSTestCase;

import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the UrlFactory methods.
 */
public class UrlFactoryTest extends KNSTestCase {

    /**
     * Test that what is returned from url factory matches the url we expect.
     */
    @Test public void testFactoryMatch() throws Exception {
        String basePath = "http://localhost:8080/";
        String actionPath = "kr/lookup.do";
        String testUrl = basePath + actionPath + "?" + KNSConstants.DISPATCH_REQUEST_PARAMETER + "=start" + "&" + KNSConstants.DOC_FORM_KEY + "=903" + KNSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME + "=accountLookupableImpl" + KNSConstants.RETURN_LOCATION_PARAMETER + "=" + basePath + "ib.do";
        testUrl = UrlFactory.encode(testUrl);

        // construct lookup url
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KNSConstants.DOC_FORM_KEY, "903");
        parameters.put(KNSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, "accountLookupableImpl");
        parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, basePath + "ib.do");

        String returnedUrl = UrlFactory.parameterizeUrl(basePath + actionPath, parameters);

        assertTrue("Returned url is empty", StringUtils.isNotBlank(returnedUrl));
        assertTrue("Returned url has incorrect base", returnedUrl.startsWith(basePath + actionPath + "?"));
        assertTrue("Returned url does not have correct # of &", StringUtils.countMatches(returnedUrl, "&") == 3);
        assertTrue("Returned url missing parameter 1", StringUtils.contains(returnedUrl, KNSConstants.DISPATCH_REQUEST_PARAMETER + "=start"));
        assertTrue("Returned url missing parameter 2", StringUtils.contains(returnedUrl, KNSConstants.DOC_FORM_KEY + "=903"));
        assertTrue("Returned url missing parameter 3", StringUtils.contains(returnedUrl, KNSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME + "=accountLookupableImpl"));
        // assertTrue("Returned url missing parameter 4",StringUtils.contains(returnedUrl,
        // UrlFactory.encode(KNSConstants.RETURN_LOCATION_PARAMETER + "=" + basePath + "ib.do")));
    }
}
