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
package org.kuali.rice;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.rice.testharness.KNSTestCase;
import org.kuali.rice.testharness.TransactionalLifecycle;

@PerSuiteUnitTestData(
    @UnitTestData(
        sqlStatements = {
            @UnitTestSql("insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (1, 'fred')"),
            @UnitTestSql("insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (2, 'fran')"),
            @UnitTestSql("insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (3, 'frank')")
        },
        sqlFiles = {@UnitTestFile(filename = "classpath:DefaultTestData-MinusFirstThreeStatements.sql", delimiter = ";")}))
public class TestBase extends KNSTestCase {

	private TransactionalLifecycle transactionalLifecycle;

	@Before
	public void setUp() throws Exception {
		setContextName("/SampleRiceClient");
		setRelativeWebappRoot("/src/test/webapp");
		setXmlFilename("classpath:DefaultTestData.xml");
		setTestConfigFilename("classpath:META-INF/sample-app-test-config.xml");
		super.setUp();
		transactionalLifecycle = new TransactionalLifecycle();
		transactionalLifecycle.start();
	}

	@After
	public void tearDown() throws Exception {
		transactionalLifecycle.stop();
		super.tearDown();
	}

	@Override
	protected String getModuleName() {
		return "kns";
	}

}
