package org.kuali.rice.test;

import javax.transaction.UserTransaction;

import org.kuali.rice.database.XAPoolDataSource;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Locator that sits on the testharness SpringContext.
 * 
 * This doesn't defer to the {@link GlobalResourceLoader} because I'm not sure 
 * the test harness justifies the extra setup at the moment.  If/when the 
 * testharness {@link SpringResourceLoader} is placed in the {@link GlobalResourceLoader} 
 * this can delegate to that {@link GlobalResourceLoader}.
 * 
 * @author rkirkend
 *
 */
public class TestHarnessServiceLocator {

	private static ConfigurableApplicationContext context;
	
	public static final String USER_TRANSACTION = "userTransaction";
	public static final String TRANSACTON_MANAGER = "transactionManager";
	public static final String DATA_SOURCE = "dataSource";
	
	public static Object getService(String serviceName) {
		return getContext().getBean(serviceName);
	}
	
	public static XAPoolDataSource getDataSource() {
		return (XAPoolDataSource)getService(DATA_SOURCE);
	}
	
	public static JtaTransactionManager getJtaTransactionManager() {
		return (JtaTransactionManager)getService(TRANSACTON_MANAGER);
	}
	
	public static UserTransaction getUserTransaction() {
		return (UserTransaction)getService(USER_TRANSACTION);
	}
	
	public static ConfigurableApplicationContext getContext() {
		return context;
	}

	public static void setContext(ConfigurableApplicationContext context) {
		TestHarnessServiceLocator.context = context;
	}	
}