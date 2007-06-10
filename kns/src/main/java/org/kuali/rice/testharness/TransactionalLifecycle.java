package org.kuali.rice.testharness;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.lifecycle.Lifecycle;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * A lifecycle for testing with database transactional rollback.
 * @author natjohns
 */
public class TransactionalLifecycle implements Lifecycle {

		private boolean started;
		private TransactionStatus TRANSACTION_STATUS;

		public boolean isStarted() {
			return started;
		}

		public void start() throws Exception {
			DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
			defaultTransactionDefinition.setTimeout(30);
			TRANSACTION_STATUS = KNSServiceLocator.getTransactionManager().getTransaction(defaultTransactionDefinition);
			started = true;
		}

		public void stop() throws Exception {
			KNSServiceLocator.getTransactionManager().rollback(TRANSACTION_STATUS);
			started = false;
		}
}
