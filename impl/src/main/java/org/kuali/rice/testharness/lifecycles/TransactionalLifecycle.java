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
package org.kuali.rice.testharness.lifecycles;

import org.kuali.rice.core.lifecycle.Lifecycle;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * A lifecycle for testing with database transactional rollback.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TransactionalLifecycle implements Lifecycle {

		private boolean started;
		private TransactionStatus TRANSACTION_STATUS;
		private PlatformTransactionManager transactionManager;

		public boolean isStarted() {
			return started;
		}

		public void start() throws Exception {
			if (transactionManager == null) {
				throw new RuntimeException("TransactionManager was null.  Please inject a proper PlatformTransactionManager instance.");
			}
			DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
			defaultTransactionDefinition.setTimeout(3600);
			TRANSACTION_STATUS = transactionManager.getTransaction(defaultTransactionDefinition);
			started = true;
		}

		public void stop() throws Exception {
			transactionManager.rollback(TRANSACTION_STATUS);
			started = false;
		}
		
		public void setTransactionManager(PlatformTransactionManager transactionManager) {
			this.transactionManager = transactionManager;
		}
}
