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

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.reflect.BaseTargetedInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import javax.transaction.TransactionManager;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Factory bean that supplies a the currently configured JTA TransactionManager. This factory bean simply returns a
 * reference to the TransactionManager configured on {@link Jta#getTransactionManager()}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionManagerFactoryBean implements FactoryBean<TransactionManager> {

    @Override
    public TransactionManager getObject() throws Exception {
        if (ConfigContext.getCurrentContextConfig() != null &&
                ConfigContext.getCurrentContextConfig().getObject(RiceConstants.SPRING_TRANSACTION_MANAGER) != null) {
            return null;
        }
        return (TransactionManager) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{getObjectType()},
                new LazyInitializationHandler());
    }

    @Override
    public Class<TransactionManager> getObjectType() {
        return TransactionManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    static class LazyInitializationHandler extends BaseTargetedInvocationHandler<TransactionManager> {

        private volatile boolean initialized;
        private TransactionManager transactionManager;

        LazyInitializationHandler() {
            super(null);
        }

        @Override
        public Object invokeInternal(Object proxy, Method method, Object[] args) throws Throwable {
            if (!this.initialized) {
                if (Jta.isFrozen()) {
                    this.transactionManager = Jta.getTransactionManager();
                    this.initialized = true;
                } else {
                    throw new IllegalStateException("JTA has not been initialized, in order to use the "
                            + "TransactionManager please ensure that it has been configured on " + Jta.class.getName());
                }
            }
            if (this.transactionManager == null) {
                throw new IllegalStateException("Attempting to use TransactionManager but JTA is not enabled.");
            }
            return method.invoke(transactionManager, args);
        }

        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public TransactionManager getTarget() {
            return transactionManager;
        }

    }


}
