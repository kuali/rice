package org.kuali.rice.config.spring;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

public class PlatformTransactionManagerFactoryBean implements FactoryBean {
	
	private UserTransaction userTransaction;
	private TransactionManager transactionManager;

	public Object getObject() throws Exception {
		if (Core.getCurrentContextConfig().getObject(RiceConstants.SPRING_TRANSACTION_MANAGER) != null) {
			return Core.getCurrentContextConfig().getObject(RiceConstants.SPRING_TRANSACTION_MANAGER);
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
