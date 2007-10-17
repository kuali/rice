/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.core.dependencylifecycles;

import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.objectweb.jotm.Current;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TransactionTimeoutLifecycle extends BaseLifecycle implements ApplicationListener, BeanFactoryAware {

	private BeanFactory beanFactory;

	@Override
	public void start() throws Exception {
      TransactionManager transactionManager = (TransactionManager)beanFactory.getBean(KEWServiceLocator.JTA_TRANSACTION_MANAGER);
		if (transactionManager instanceof Current) {
			Current jotm = (Current) transactionManager;
			if (!StringUtils.isBlank(Core.getRootConfig().getTransactionTimeout())) {
				jotm.setDefaultTimeout(Integer.parseInt(Core.getRootConfig().getTransactionTimeout()));
				jotm.setTransactionTimeout(jotm.getDefaultTimeout());
			} else {
				jotm.setDefaultTimeout(EdenConstants.DEFAULT_TRANSACTION_TIMEOUT_SECONDS);
				jotm.setTransactionTimeout(jotm.getDefaultTimeout());
			}
		}
		super.start();
	}

	/**
	 * Initialize this LifeCycle when spring starts.
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		try {
			if (event instanceof ContextRefreshedEvent) {
				if (isStarted()) {
					stop();
				}
				start();
			} else if (event instanceof ContextClosedEvent) {
				stop();
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}
			throw new WorkflowRuntimeException("Failed to handle application context event: " + event, e);
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
