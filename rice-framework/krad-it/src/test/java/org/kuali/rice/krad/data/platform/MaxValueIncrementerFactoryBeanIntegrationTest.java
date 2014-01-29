/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.data.platform;

import org.junit.Test;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;

import static org.junit.Assert.*;

public class MaxValueIncrementerFactoryBeanIntegrationTest extends KRADTestCase {

    private final static String TEST_INCREMENTER = "testIncrementer";
    private final static String INVALID_TEST_INCREMENTER = "invalidTestIncrementer";

    private ConfigurableApplicationContext context;

    @Override
    public void setUpInternal() throws Exception {
        super.setUpInternal();
        context = new ClassPathXmlApplicationContext("MaxValueIncrementerFactoryBeanTest.xml", getClass());
    }

    @Override
    public void tearDown() throws Exception {
        if (context != null) {
            context.close();
        }
        super.tearDown();
    }

    /**
     * Tests that the incrementer returned from the factory returns the proper next int, long, and String values.
     */
    @Test
    public void testGetIncrementer_nextValues() {
        DataFieldMaxValueIncrementer incrementer = (DataFieldMaxValueIncrementer) context.getBean(TEST_INCREMENTER);
        assertNotNull(incrementer);

        // now that we have our incrementer, let's get the next value
        int nextIntValue = incrementer.nextIntValue();
        assertTrue("nextIntValue should be greater than 0", nextIntValue > 0);

        // do it again, should be 1 larger!
        int nextNextIntValue = incrementer.nextIntValue();
        assertEquals("Next value should be one higher", nextIntValue + 1, nextNextIntValue);

        // try getting the next value as a long
        long nextLongValue = incrementer.nextLongValue();
        assertEquals(nextNextIntValue + 1, nextLongValue);

        // try getting it as a String now
        String nextStringValue = incrementer.nextStringValue();
        assertEquals(nextLongValue + 1, Long.parseLong(nextStringValue));
    }

    /**
     * Tests that the sequence name is case insensitive. We will do this by using the same sequence name as the
     * previous test, but changing the case to all lowercase.
     */
    @Test
    public void testGetIncrementer_CaseInsensitive() {
        DataFieldMaxValueIncrementer incrementer = (DataFieldMaxValueIncrementer) context.getBean(TEST_INCREMENTER);
        assertNotNull(incrementer);

        // now that we have our incrementer, let's get the next value
        int nextIntValue = incrementer.nextIntValue();
        assertTrue("nextIntValue should be greater than 0", nextIntValue > 0);
    }

    /**
     * Tests that if you try to use the factory with an invalid sequence name, it will throw a DataAccessException.
     */
    @Test(expected = DataAccessException.class)
    public void testGetIncrementer_BadSequence() {
        DataFieldMaxValueIncrementer incrementer = (DataFieldMaxValueIncrementer) context.getBean(INVALID_TEST_INCREMENTER);

        // the incrementer *may* still be retrieved successfully (depending on the database this integration test is run against)
        assertNotNull(incrementer);

        // but at the very least it should throw an exception when executed
        incrementer.nextLongValue();
    }
}
