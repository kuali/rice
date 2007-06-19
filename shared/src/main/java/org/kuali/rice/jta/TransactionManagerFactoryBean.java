package org.kuali.rice.jta;

import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jndi.JndiTemplate;


/**
 * Factory bean that supplies a TransactionManager object from the the current context
 * (i.e. plugin, embedding webapp) Config object map if defined therein (under the Config.TRANSACTION_MANAGER_OBJ key),
 * from JNDI if {@link Config#TRANSACTION_MANAGER_JNDI} is defined,
 * or from a default declaratively assigned in containing bean factory.
 * 
 * @author ewestfal
 */
public class TransactionManagerFactoryBean implements FactoryBean {

	private TransactionManager defaultTransactionManager;
	private JndiTemplate jndiTemplate;
	
	public Object getObject() throws Exception {
		TransactionManager transactionManager =  (TransactionManager)Core.getCurrentContextConfig().getObject(Config.TRANSACTION_MANAGER_OBJ);
		if (transactionManager == null) {
			String transactionManagerJndiName = Core.getCurrentContextConfig().getProperty(Config.TRANSACTION_MANAGER_JNDI);
			if (!StringUtils.isEmpty(transactionManagerJndiName)) {
				if (this.jndiTemplate == null) {
				    this.jndiTemplate = new JndiTemplate();
				}
				try {
					transactionManager = (TransactionManager)this.jndiTemplate.lookup(transactionManagerJndiName, TransactionManager.class);
				} catch (NamingException e) {
					throw new ConfigurationException("Could not locate the TransactionManager at the given JNDI location: '" + transactionManagerJndiName + "'", e);
				}
			}
			
		}
		if (transactionManager != null) {
			return transactionManager;
		}
		return this.defaultTransactionManager;
	}

	public Class getObjectType() {
		return TransactionManager.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	public void setDefaultTransactionManager(TransactionManager transactionManager) {
	    this.defaultTransactionManager = transactionManager;
	}

	public JndiTemplate getJndiTemplate() {
		return this.jndiTemplate;
	}

	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = jndiTemplate;
	}

}
