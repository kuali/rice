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

/**
 * Tests the {@link TransactionManagerFactoryBean}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionManagerFactoryBeanTest {

    private TransactionManagerFactoryBean transactionManagerFactoryBean;

    @Mock private TransactionManager transactionManager;
    @Mock private UserTransaction userTransaction;

    @Before
    public void setUp() throws Exception {
        this.transactionManagerFactoryBean = new TransactionManagerFactoryBean();
    }

    @After
    public void tearDown() throws Exception {
        Jta.reset();
        ConfigContext.destroy();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetObject_JtaNotInitialized() throws Exception {
        transactionManagerFactoryBean.getObject().getTransaction();
    }

    @Test
    public void testGetObject() throws Exception {
        Jta.configure(this.transactionManager, this.userTransaction);
        TransactionManager transactionManager = transactionManagerFactoryBean.getObject();

        // pre-initialization, should be null
        TargetedInvocationHandler<TransactionManager> handler =
                (TargetedInvocationHandler<TransactionManager>)Proxy.getInvocationHandler(transactionManager);
        assertNull(handler.getTarget());

        // force internal initialization
        transactionManager.getTransaction();
        assertEquals(this.transactionManager, handler.getTarget());
    }

    @Test
    public void testGetObject_Null() throws Exception {
        Jta.configure(null, null);
        TransactionManager transactionManager = transactionManagerFactoryBean.getObject();

        // should not be null, but should be a proxy to a non-existent transaction manager
        assertNotNull(transactionManager);
        assertTrue(Proxy.isProxyClass(transactionManager.getClass()));

        try {
            transactionManager.getTransaction();
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void testGetObject_SpringTransactionManager() throws Exception {
        Jta.configure(transactionManager, userTransaction);
        SimpleConfig config = new SimpleConfig();
        config.putObject(RiceConstants.SPRING_TRANSACTION_MANAGER, new JtaTransactionManager());
        ConfigContext.init(config);

        // since a spring configuration transaction manager has been configured, getObject will always return null, even
        // though we have JTA setup (i'm suppose it's questionable that it should be doing this, but that's how it's
        // always worked
        assertNull(transactionManagerFactoryBean.getObject());
    }

    @Test
    public void testGetObjectType() throws Exception {
        assertEquals(TransactionManager.class, transactionManagerFactoryBean.getObjectType());
    }

    @Test
    public void testIsSingleton() throws Exception {
        assertTrue(transactionManagerFactoryBean.isSingleton());
    }

}
