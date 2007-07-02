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
package org.kuali.core.inquiry;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

import edu.sampleu.travel.bo.FiscalOfficer;

/**
 * This class tests the KualiInquirable methods.
 * 
 * 
 */
@KNSWithTestSpringContext
public class KualiInquirableTest extends KNSTestBase {

    private FiscalOfficer fo;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fo = new FiscalOfficer();
        fo.setId(new Long(1));
    }

    /**
     * Tests the inquiry url output for a given bo and property name.
     */
    @Test public final void testBuildInquiryUrl() {
        String inquiryUrl = KualiInquirableImpl.getInquiryUrl(fo, "id", true);
        assertTrue("An inquiry URL to Organization should be built", StringUtils.contains(inquiryUrl, "id=1"));
        assertTrue("An inquiry URL to Organization should be built", StringUtils.contains(inquiryUrl, "businessObjectClassName=edu.sampleu.travel.bo.FiscalOfficer"));
    }

}
