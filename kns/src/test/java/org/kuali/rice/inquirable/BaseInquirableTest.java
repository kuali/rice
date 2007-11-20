/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.inquirable;

import org.junit.Test;
import org.kuali.rice.TestBase;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.rice.testharness.HtmlUnitUtil;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@PerTestUnitTestData(
    @UnitTestData(
        order = { UnitTestData.Type.SQL_FILES, UnitTestData.Type.SQL_STATEMENTS },
        sqlStatements = {
            @UnitTestSql("select 1 from dual"),
            @UnitTestSql("select 1 from dual")
        },
        sqlFiles = {@UnitTestFile(filename = "classpath:UselessTest.sql", delimiter = "/")}))
public class BaseInquirableTest extends TestBase {

	@UnitTestData(sqlStatements = {@UnitTestSql("select 2 from dual")})
	@Test
	public void testInquiryFetch() throws Exception {
		HtmlPage inquiryPage = HtmlUnitUtil.gotoPageAndLogin(HtmlUnitUtil.BASE_URL + "/kr/inquiry.do?businessObjectClassName=edu.sampleu.travel.bo.TravelAccount&number=a1&methodToCall=start");
		assertEquals("Kuali :: Inquiry", inquiryPage.getTitleText());
		assertTrue("Inquiry page should have 'Travel Account Inquiry' in title bar", HtmlUnitUtil.pageContainsText(inquiryPage, "Travel Account Inquiry"));
	}

}
