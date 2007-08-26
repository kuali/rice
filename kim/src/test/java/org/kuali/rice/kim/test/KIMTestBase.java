/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.test;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.testharness.KNSTestCase;
import org.kuali.rice.testharness.TransactionalLifecycle;

/**
 * This is test base that should be used for all KIM unit tests.  All non-web unit 
 * tests for KIM should extend this base class.
 * 
 * @author Aaron Godert (ag266 at cornell dot edu)
 */
public abstract class KIMTestBase extends KNSTestCase {
    // The lifecycle that can rollback Spring transactions
    private TransactionalLifecycle transactionalLifecycle;
    
    /**
     * This overridden method performs basic set up for each KIM unit test.
     * 
     * @see org.kuali.rice.test.RiceTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
	// Bootstrap SQL needed for all KIM tests
	//setSqlFilename("classpath:KIMTestData.sql");
	//setSqlDelimiter(";");

	// Bootstrap KEW test data needed for all KIM tests
	//setXmlFilename("classpath:KIMTestKEWBootstrapData.xml");

	// Bootstrap the location of the KIM config attributes
	setTestConfigFilename("classpath:META-INF/kim-test-config.xml");
	
	// setUp() of KNSTestCase will start the above lifecycles in
	// suite mode, so they are only run at the start of all tests
	super.setUp();

	// Adding the Transaction Lifecycle
	transactionalLifecycle = new TransactionalLifecycle();
	transactionalLifecycle.start();
    }

    /**
     * This overridden method performs basic teardown for all KIM unit tests.
     * 
     * @see org.kuali.rice.test.RiceTestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
	transactionalLifecycle.stop();
	super.tearDown();
    }
}