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

import org.kuali.rice.core.api.util.reflect.BaseTargetedInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import javax.transaction.UserTransaction;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Factory bean that supplies a the currently configured JTA UserTransaction. This factory bean simply returns a
 * reference to the UserTransaction configured on {@link Jta#getUserTransaction()}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserTransactionFactoryBean implements FactoryBean<UserTransaction> {

    @Override
	public UserTransaction getObject() throws Exception {
		return (UserTransaction)Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] { getObjectType() },
                new LazyInitializationHandler());
	}

    @Override
	public Class<UserTransaction> getObjectType() {
		return UserTransaction.class;
	}

    @Override
	public boolean isSingleton() {
		return true;
	}

    private static class LazyInitializationHandler extends BaseTargetedInvocationHandler<UserTransaction> {

        private volatile boolean initialized;
        private UserTransaction userTransaction;

        LazyInitializationHandler() {
            super(null);
        }

        @Override
        public Object invokeInternal(Object proxy, Method method, Object[] args) throws Throwable {
            if (!this.initialized) {
                if (Jta.isFrozen()) {
                    this.userTransaction = Jta.getUserTransaction();
                    this.initialized = true;
                } else {
                    throw new IllegalStateException("JTA has not been initialized, in order to use the "
                            + "UserTransaction please ensure that it has been configured on " + Jta.class.getName());
                }
            }
            if (this.userTransaction == null) {
                throw new IllegalStateException("Attempting to use TransactionManager but JTA is not enabled.");
            }
            return method.invoke(userTransaction, args);
        }

        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public UserTransaction getTarget() {
            return userTransaction;
        }

    }

}
