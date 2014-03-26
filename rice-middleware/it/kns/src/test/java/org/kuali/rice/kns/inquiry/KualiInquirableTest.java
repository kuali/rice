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
package org.kuali.rice.kns.inquiry;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kns.KNSTestCase;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.test.KRADTestCase;

import org.junit.Assert;
import org.kuali.rice.krad.test.document.bo.AccountType;

/**
 * KualiInquirableTest tests {@link KualiInquirableImpl} methods
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated KNS test class, convert to KRAD equivalent if applicable.
 */
@Deprecated
public class KualiInquirableTest extends KNSTestCase {

    private AccountType at;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        at = new AccountType();
        at.setAccountTypeCode("ABC");
    }

    /**
     * Tests the inquiry url output for a given bo and property name
     */
    @Test public final void testBuildInquiryUrl() {
    	String inquiryUrl = ((HtmlData.AnchorHtmlData)new KualiInquirableImpl().getInquiryUrl(at, "accountTypeCode", true)).getHref();
        Assert.assertTrue("An inquiry URL to AccountType should be built. CI Failure - ", StringUtils.contains(inquiryUrl,
                "accountTypeCode=ABC"));
        Assert.assertTrue("An inquiry URL to AccountType should be built", StringUtils.contains(inquiryUrl,
                "businessObjectClassName=" + AccountType.class.getName()));
    }

}
