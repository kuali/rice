package org.kuali.rice.ojb;

import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springmodules.orm.ojb.support.LocalOjbConfigurer;

/**
 * Utility bean that sets the JTA TransactionManager on the WorkflowTransactionManagerFactory, the
 * OJB TransactionManagerFactory implementation that makes this available from Workflow core.
 * @see TransactionManagerFactory
 * @see org.apache.ojb.broker.transaction.tm.TransactionManagerFactory
 */
public class JtaOjbConfigurer extends LocalOjbConfigurer implements InitializingBean, DisposableBean {
    private static final Logger LOG = Logger.getLogger(JtaOjbConfigurer.class);

	private TransactionManager transactionManager;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		RiceDataSourceConnectionFactory.addBeanFactory(beanFactory);
	}

	public void afterPropertiesSet() {
        LOG.debug("Setting OJB WorkflowTransactionManagerFactory transaction manager to: " + this.transactionManager);
        TransactionManagerFactory.setTransactionManager(this.transactionManager);
	}

	public void destroy() {
	    this.transactionManager = null;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}