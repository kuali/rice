package org.kuali.rice.krad.data.platform;

import org.junit.Test;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * An integration test for the {@link MaxValueIncrementerFactory}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaxValueIncrementerFactoryIntegrationTest extends KRADTestCase {

    private static final String ARBITRARY_SEQUENCE = "TRVL_ID_SEQ";

    /**
     * Tests that the incrementer returned from the factory returns the proper next int, long, and String values.
     */
    @Test
    public void testGetIncrementer_nextValues() {
        DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        DataFieldMaxValueIncrementer incrementer =
                MaxValueIncrementerFactory.getIncrementer(dataSource, ARBITRARY_SEQUENCE);
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
        DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        DataFieldMaxValueIncrementer incrementer =
                MaxValueIncrementerFactory.getIncrementer(dataSource, ARBITRARY_SEQUENCE.toLowerCase());
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
        DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        DataFieldMaxValueIncrementer incrementer =
                MaxValueIncrementerFactory.getIncrementer(dataSource, "OH_NO_YOU_DIDNT!");

        // the incrementer *may* still be retrieved successfully (depending on the database this integration test is run against)
        assertNotNull(incrementer);

        // but at the very least it should throw an exception when executed
        incrementer.nextLongValue();
    }

}
