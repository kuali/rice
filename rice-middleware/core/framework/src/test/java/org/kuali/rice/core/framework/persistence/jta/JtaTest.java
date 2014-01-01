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
package org.kuali.rice.core.framework.persistence.jta;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests the static Jta class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class JtaTest {

    @Mock private TransactionManager transactionManager;
    @Mock private UserTransaction userTransaction;

    @Before
    public void setUp() throws Exception {
        assertNotNull(transactionManager);
        assertNotNull(userTransaction);
        Jta.reset();
        assertFalse(Jta.isFrozen());
    }

    @After
    public void tearDown() throws Exception {
        Jta.reset();
        assertFalse(Jta.isFrozen());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigure_NullTransactionManager() throws Exception {
        Jta.configure(null, userTransaction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigure_NullUserTransaction() throws Exception {
        Jta.configure(transactionManager, null);
    }

    @Test
    public void testConfigure_AllNull() throws Exception {
        Jta.configure(null, null);
        assertTrue(Jta.isFrozen());
        assertFalse(Jta.isEnabled());
    }

    @Test
    public void testConfigure_NotNull() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
        assertTrue(Jta.isEnabled());
        assertSame(transactionManager, Jta.getTransactionManager());
        assertSame(userTransaction, Jta.getUserTransaction());
    }

    /**
     * If try to reconfigure a frozen JTA with the same JTA objects, it will allow it.
     */
    @Test
    public void testConfigure_Frozen_SameJtaObjects() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
        assertTrue(Jta.isEnabled());
        // try to reconfigure with the same JTA objects
        Jta.configure(transactionManager, userTransaction);
    }


    /**
     * If try to reconfigure a frozen JTA with different JTA objects, it will throw IllegalStateException.
     */
    @Test
    public void testConfigure_Frozen_DifferentJtaObjects() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
        assertTrue(Jta.isEnabled());
        try {
            // try to reconfigure with *different* JTA objects
            Jta.configure(mock(TransactionManager.class), mock(UserTransaction.class));
            fail("IllegalStateException should have been thrown when trying to configure a frozen JTA setup");
        } catch (IllegalStateException e) {
            // this should have happened!
        }
    }

    @Test
    public void testReset() throws Exception {
        assertFalse(Jta.isFrozen());
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
        assertTrue(Jta.isEnabled());
        assertSame(transactionManager, Jta.getTransactionManager());
        assertSame(userTransaction, Jta.getUserTransaction());

        Jta.reset();

        assertFalse(Jta.isFrozen());
        assertFalse(Jta.isEnabled());
    }

    @Test
    public void testIsFrozen() throws Exception {
        assertFalse(Jta.isFrozen());
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertFalse(Jta.isEnabled());
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isEnabled());

        Jta.reset();
        Jta.configure(null, null);
        assertFalse(Jta.isEnabled());
    }

    @Test
    public void testGetTransactionManager_Frozen() throws Exception {
        assertFalse(Jta.isFrozen());
        try {
            Jta.getTransactionManager();
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void testGetTransactionManager() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
        assertSame(transactionManager, Jta.getTransactionManager());
    }

    @Test
    public void testGetUserTransaction_Frozen() throws Exception {
        assertFalse(Jta.isFrozen());
        try {
            Jta.getUserTransaction();
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {}

    }

    @Test
    public void testGetUserTransaction() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        assertTrue(Jta.isFrozen());
        assertSame(userTransaction, Jta.getUserTransaction());
    }

    @Test
    public void testConstructorPrivate() throws Exception {
        Constructor<Jta> constructor = Jta.class.getDeclaredConstructor(null);
        assertFalse(constructor.isAccessible());
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
