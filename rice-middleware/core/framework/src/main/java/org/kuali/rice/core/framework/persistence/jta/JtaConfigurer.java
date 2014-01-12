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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * A Spring-enabled bean which can be used to configure the {@link Jta} setup for a Kuali Rice application. If the
 * TransactionManager and UserTransaction are not explicitly set, they will be derived based on Rice configuration
 * based on the following properties:
 *
 * <ul>
 *     <li>{@link RiceConstants#TRANSACTION_MANAGER_OBJ}</li>
 *     <li>{@link RiceConstants#TRANSACTION_MANAGER_JNDI}</li>
 *     <li>{@link RiceConstants#USER_TRANSACTION_OBJ}</li>
 *     <li>{@link RiceConstants#USER_TRANSACTION_JNDI}</li>
 * </ul>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JtaConfigurer implements InitializingBean, DisposableBean {

    private TransactionManager transactionManager;
    private UserTransaction userTransaction;

    private JndiTemplate jndiTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        configureTransactionManager();
        configureUserTransaction();
        Jta.configure(transactionManager, userTransaction);
    }

    protected void configureTransactionManager() {
        if (this.transactionManager == null) {
            this.transactionManager = loadFromConfiguration(TransactionManager.class,
                    RiceConstants.TRANSACTION_MANAGER_OBJ, RiceConstants.TRANSACTION_MANAGER_JNDI);
        }
    }

    protected void configureUserTransaction() {
        if (this.userTransaction == null) {
            this.userTransaction = loadFromConfiguration(UserTransaction.class,
                    RiceConstants.USER_TRANSACTION_OBJ, RiceConstants.USER_TRANSACTION_JNDI);
        }
    }


    protected <T> T loadFromConfiguration(Class<T> objectClass, String objectKey, String jndiKey) {
        if (!ConfigContext.isInitialized()) {
            throw new IllegalStateException("Configuration system has not been initialized, so cannot load JTA "
                    + "configuration. Please ensure the Configuration system is initalized first.");
        }
        T configured = (T)ConfigContext.getCurrentContextConfig().getObject(objectKey);
        if (configured == null) {
            String jndiName = ConfigContext.getCurrentContextConfig().getProperty(jndiKey);
            if (StringUtils.isNotEmpty(jndiName)) {
                if (this.jndiTemplate == null) {
                    this.jndiTemplate = new JndiTemplate();
                }
                try {
                    configured = (T)this.jndiTemplate.lookup(jndiName, objectClass);
                } catch (NamingException e) {
                    throw new ConfigurationException("Could not locate the " + objectClass.getSimpleName() + " at the given JNDI location: '" + jndiName + "'", e);
                }
            }
        }
        return configured;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate;
    }

    @Override
    public void destroy() throws Exception {
        Jta.reset();
    }

}
