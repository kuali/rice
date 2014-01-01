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
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the JtaConfigurer spring bean.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class JtaConfigurerTest {

    private static final String TRANSACTION_MANAGER_JNDI_NAME = "java:comp/TransactionManager";
    private static final String USER_TRANSACTION_JNDI_NAME = "java:comp/UserTransaction";

    @Mock private javax.transaction.TransactionManager transactionManager;
    @Mock private UserTransaction userTransaction;

    private ClassPathXmlApplicationContext context;
    private SimpleConfig config;
    private SimpleNamingContextBuilder builder;

    private void loadSpring(String fileName) {
        context = new ClassPathXmlApplicationContext(fileName, getClass());
    }

    private void initializeJtaObjectConfig() {
        this.config = new SimpleConfig();
        this.config.putObject(RiceConstants.TRANSACTION_MANAGER_OBJ, transactionManager);
        this.config.putObject(RiceConstants.USER_TRANSACTION_OBJ, userTransaction);
        ConfigContext.init(this.config);
    }

    private void initializeJtaJndiConfig() throws Exception {
        this.builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        builder.bind(TRANSACTION_MANAGER_JNDI_NAME, transactionManager);
        builder.bind(USER_TRANSACTION_JNDI_NAME, userTransaction);
        this.config = new SimpleConfig();
        this.config.putProperty(RiceConstants.TRANSACTION_MANAGER_JNDI, TRANSACTION_MANAGER_JNDI_NAME);
        this.config.putProperty(RiceConstants.USER_TRANSACTION_JNDI, USER_TRANSACTION_JNDI_NAME);
        ConfigContext.init(this.config);
    }

    @Before
    public void setUp() {
        assertFalse(Jta.isFrozen());
    }

    @After
    public void tearDown() {
        if (context != null) {
            context.destroy();
        }
        if (ConfigContext.isInitialized()) {
            ConfigContext.destroy();
        }
        Jta.reset();
        if (this.builder != null) {
            this.builder.clear();
        }
    }

    @Test
    public void testLoadUsingSpring_ConfigNotInitialized() {
        try {
            loadSpring("JtaConfigurerTestSpring_Basic.xml");
        } catch (BeanCreationException e) {
            assertTrue(IllegalStateException.class == e.getCause().getClass());
        }
    }

    @Test
    public void testLoadUsingSpring_JtaObjectConfig() {
        initializeJtaObjectConfig();
        loadSpring("JtaConfigurerTestSpring_Basic.xml");
        assertTrue(Jta.isFrozen());
        assertTrue(Jta.isEnabled());
        assertEquals(transactionManager, Jta.getTransactionManager());
        assertEquals(userTransaction, Jta.getUserTransaction());
    }

    @Test
    public void testLoadUsingSpring_JtaJndiConfig() throws Exception {
        initializeJtaJndiConfig();
        loadSpring("JtaConfigurerTestSpring_Basic.xml");
        assertTrue(Jta.isFrozen());
        assertTrue(Jta.isEnabled());
        assertEquals(transactionManager, Jta.getTransactionManager());
        assertEquals(userTransaction, Jta.getUserTransaction());
    }

    @Test(expected = IllegalStateException.class)
    public void testAfterPropertiesSet_ConfigNotInitialized() throws Exception {
        new JtaConfigurer().afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSet_NoJta() throws Exception {
        ConfigContext.init(new SimpleConfig()); // initialize empty config
        new JtaConfigurer().afterPropertiesSet();
        assertTrue(Jta.isFrozen());
        assertFalse(Jta.isEnabled());
        assertNull(Jta.getTransactionManager());
        assertNull(Jta.getUserTransaction());
    }

    @Test
    public void testAfterPropertiesSet_JtaObjectConfig() throws Exception {
        initializeJtaObjectConfig();
        new JtaConfigurer().afterPropertiesSet();
        assertEquals(transactionManager, Jta.getTransactionManager());
        assertEquals(userTransaction, Jta.getUserTransaction());
    }

    @Test
    public void testAfterPropertiesSet_JtaJndiConfig() throws Exception {
        initializeJtaJndiConfig();
        new JtaConfigurer().afterPropertiesSet();
        assertEquals(transactionManager, Jta.getTransactionManager());
        assertEquals(userTransaction, Jta.getUserTransaction());
    }

    @Test
    public void testAfterPropertiesSet_JndiTemplate() throws Exception {
        // go ahead and initialize JNDI, so we can make sure it's actually using our custom template
        initializeJtaJndiConfig();
        // create a mock JNDI context
        final Context context = mock(Context.class);
        // create custom JTA objects so we can be sure it's using our template
        TransactionManager transactionManager2 = mock(TransactionManager.class);
        UserTransaction userTransaction2 = mock(UserTransaction.class);
        when(context.lookup(TRANSACTION_MANAGER_JNDI_NAME)).thenReturn(transactionManager2);
        when(context.lookup(USER_TRANSACTION_JNDI_NAME)).thenReturn(userTransaction2);

        // create a custom JndiTemplate which will use our mock Context
        JndiTemplate template = new JndiTemplate() {
            @Override
            protected Context createInitialContext() throws NamingException {
                return context;
            }
        };

        JtaConfigurer configurer = new JtaConfigurer();
        configurer.setJndiTemplate(template);
        configurer.afterPropertiesSet();

        assertEquals(transactionManager2, Jta.getTransactionManager());
        assertEquals(userTransaction2, Jta.getUserTransaction());
    }

    /**
     * If there is a NamingException when calling JNDI, this configurer should handle it and throw an appropriate
     * ConfigurationException.
     */
    @Test(expected = ConfigurationException.class)
    public void testAfterPropertiesSet_ConfigurationException() throws Exception {
        initializeJtaJndiConfig();
        JtaConfigurer configurer = new JtaConfigurer();
        configurer.setJndiTemplate(new JndiTemplate() {
            @Override
            public Object lookup(String name) throws NamingException {
                throw new NamingException("Throwing NamingException from JtaConfigurerTest!");
            }
        });
        configurer.afterPropertiesSet();
    }


    @Test
    public void testSetTransactionManager() throws Exception {
        initializeJtaObjectConfig();
        JtaConfigurer configurer = new JtaConfigurer();
        TransactionManager newMockTransactionManager = mock(TransactionManager.class);
        configurer.setTransactionManager(newMockTransactionManager);
        configurer.afterPropertiesSet();
        assertNotSame(newMockTransactionManager, transactionManager);
        assertEquals(newMockTransactionManager, Jta.getTransactionManager());
        assertEquals(userTransaction, Jta.getUserTransaction());
    }

    @Test
    public void testSetUserTransaction() throws Exception {
        initializeJtaObjectConfig();
        JtaConfigurer configurer = new JtaConfigurer();
        UserTransaction newMockUserTransaction = mock(UserTransaction.class);
        configurer.setUserTransaction(newMockUserTransaction);
        configurer.afterPropertiesSet();
        assertNotSame(newMockUserTransaction, userTransaction);
        assertEquals(newMockUserTransaction, Jta.getUserTransaction());
        assertEquals(transactionManager, Jta.getTransactionManager());
    }

}
