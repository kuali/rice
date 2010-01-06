/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.core.config.spring;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.util.RiceConstants;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

public class PlatformTransactionManagerFactoryBean implements FactoryBean {
	
	private UserTransaction userTransaction;
	private TransactionManager transactionManager;

	public Object getObject() throws Exception {
		if (ConfigContext.getCurrentContextConfig().getObject(RiceConstants.SPRING_TRANSACTION_MANAGER) != null) {
			return ConfigContext.getCurrentContextConfig().getObject(RiceConstants.SPRING_TRANSACTION_MANAGER);
		}
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		jtaTransactionManager.setTransactionManager(this.getTransactionManager());
		jtaTransactionManager.setUserTransaction(this.getUserTransaction());
		jtaTransactionManager.afterPropertiesSet();
		return jtaTransactionManager;
	}

	public Class getObjectType() {
		return PlatformTransactionManager.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public UserTransaction getUserTransaction() {
		return userTransaction;
	}

	public void setUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
