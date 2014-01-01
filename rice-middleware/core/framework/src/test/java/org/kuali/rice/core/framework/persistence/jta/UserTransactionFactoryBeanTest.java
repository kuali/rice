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
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.reflect.TargetedInvocationHandler;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link org.kuali.rice.core.framework.persistence.jta.UserTransactionFactoryBean}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserTransactionFactoryBeanTest {

    private UserTransactionFactoryBean userTransactionFactoryBean;

    @Mock private TransactionManager transactionManager;
    @Mock private UserTransaction userTransaction;

    @Before
    public void setUp() throws Exception {
        this.userTransactionFactoryBean = new UserTransactionFactoryBean();
    }

    @After
    public void tearDown() throws Exception {
        Jta.reset();
        ConfigContext.destroy();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetObject_JtaNotInitialized() throws Exception {
        userTransactionFactoryBean.getObject().getStatus();
    }

    @Test
    public void testGetObject() throws Exception {
        Jta.configure(this.transactionManager, this.userTransaction);
        UserTransaction userTransaction = userTransactionFactoryBean.getObject();

        // pre-initialization, should be null
        TargetedInvocationHandler<UserTransaction> handler =
                (TargetedInvocationHandler<UserTransaction>) Proxy.getInvocationHandler(userTransaction);
        assertNull(handler.getTarget());

        // force internal initialization
        userTransaction.getStatus();
        assertEquals(this.userTransaction, handler.getTarget());
    }

    @Test
    public void testGetObject_Null() throws Exception {
        Jta.configure(null, null);
        UserTransaction userTransaction = userTransactionFactoryBean.getObject();

        // should not be null, but should be a proxy to a non-existent transaction manager
        assertNotNull(userTransaction);
        assertTrue(Proxy.isProxyClass(userTransaction.getClass()));

        try {
            userTransaction.getStatus();
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void testGetObjectType() throws Exception {
        assertEquals(UserTransaction.class, userTransactionFactoryBean.getObjectType());
    }

    @Test
    public void testIsSingleton() throws Exception {
        assertTrue(userTransactionFactoryBean.isSingleton());
    }
}
