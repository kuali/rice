package org.kuali.rice.jta;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jndi.JndiTemplate;


/**
 * Factory bean that supplies a UserTransaction object from the the current context
 * (i.e. plugin, embedding webapp) Config object map if defined therein (under the Config.USER_TRANSACTION key),
 * from JNDI if {@link Config#USER_TRANSACTION_JNDI} is defined,
 * or from a default declaratively assigned in containing bean factory.
 * 
 * @author ewestfal
 */
public class UserTransactionFactoryBean implements FactoryBean {

	private UserTransaction defaultUserTransaction;
	private JndiTemplate jndiTemplate;
	
	public Object getObject() throws Exception {
		UserTransaction userTransaction = (UserTransaction)Core.getCurrentContextConfig().getObject(Config.USER_TRANSACTION_OBJ);
		if (userTransaction == null) {
			String userTransactionJndiName = Core.getCurrentContextConfig().getProperty(Config.USER_TRANSACTION_JNDI);
			if (!StringUtils.isEmpty(userTransactionJndiName)) {
				if (this.jndiTemplate == null) {
				    this.jndiTemplate = new JndiTemplate();
				}
				try {
					userTransaction = (UserTransaction)this.jndiTemplate.lookup(userTransactionJndiName, UserTransaction.class);
				} catch (NamingException e) {
					throw new ConfigurationException("Could not locate the UserTransaction at the given JNDI location: '" + userTransactionJndiName + "'", e);
				}
			}
			
		}
		if (userTransaction != null) {
			return userTransaction;
		}
		return this.defaultUserTransaction;
	}

	public Class getObjectType() {
		return UserTransaction.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	public void setDefaultUserTransaction(UserTransaction userTransaction) {
	    this.defaultUserTransaction = userTransaction;
	}

	public JndiTemplate getJndiTemplate() {
		return this.jndiTemplate;
	}

	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = jndiTemplate;
	}
	
	

}
