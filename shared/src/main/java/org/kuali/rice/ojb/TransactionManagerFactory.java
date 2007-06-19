package org.kuali.rice.ojb;

import javax.transaction.TransactionManager;

import org.apache.ojb.broker.transaction.tm.TransactionManagerFactoryException;
import org.kuali.rice.exceptions.RiceRuntimeException;

/**
 * <p>An implementation of an OJB TransactionManagerFactory which provides access to Workflow's
 * JTA UserTransaction.</p>
 *
 * <p>If the TransactionManager singleton has been set via {@link #setTransactionManager(TransactionManager)}
 * then that reference is returned, otherwise the TransactionManager is pulled from Workflow's Spring core
 * via the SpringServiceLocator.</p>
 *
 * <p>When accessed from outside the workflow core (i.e. embedded mode), the transaction manager
 * singleton MUST explicitly be set - it cannot be resolved through the SpringServiceLocator.</p>
 *
 * <p>Note: if OJB is caused to initialize DURING Spring initialization (for example, by programmatically
 * obtaining the OJB PersistenceBrokerFactory to set the platform attribute of connection descriptors
 * from within a bean initialized by Spring), the TransactionManager singleton MUST be set beforehand,
 * otherwise NPE will result from attempting to traverse SpringServiceLocator as the GlobalResourceLoader
 * will not have been initialized yet).</p>
 *
 * <p>This TransactionManagerFactory implementation is specified in OJB via the following
 * setting the OJB properties:</p>
 * <blockquote>
 *   <code>
 *     JTATransactionManagerClass=org.kuali.rice.database.WorkflowTransactionManagerFactory
 *   </code>
 * </blockquote>
 *
 * @author ewestfal
 */
public class TransactionManagerFactory implements org.apache.ojb.broker.transaction.tm.TransactionManagerFactory {

	private static TransactionManager transactionManager;

	public TransactionManager getTransactionManager() throws TransactionManagerFactoryException {
	    // return SpringServiceLocator.getJtaTransactionManager().getTransactionManager();
		if (transactionManager == null) {
			throw new RiceRuntimeException("The JTA Transaction Manager for OJB was not configured properly.");
		}
		return transactionManager;
		// core and plugins
		// TODO what to do here
        //return KSBServiceLocator.getTransactionManager();
	}

	public static void setTransactionManager(TransactionManager transactionManager) {
		TransactionManagerFactory.transactionManager = transactionManager;
	}

}
