/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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