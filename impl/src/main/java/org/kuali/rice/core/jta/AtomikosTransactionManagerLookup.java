/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.jta;

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

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;

/**
 * TransactionManager lookup strategy for Atomikos Transactions.
 * 
 * @author Ludovic Orban
 */

// See: http://opensource.atlassian.com/projects/hibernate/browse/HHH-2821
// Once added to Hibernate distribution, we can remove this class
public class AtomikosTransactionManagerLookup implements TransactionManagerLookup {

    public TransactionManager getTransactionManager(Properties props) throws HibernateException {
        try {
            Class clazz = Class.forName("com.atomikos.icatch.jta.UserTransactionManager");
            return (TransactionManager) clazz.newInstance();
        } catch (Exception e) {
            throw new HibernateException("Could not obtain Atomikos transaction manager instance", e);
        }
    }

    public String getUserTransactionName() {
        return "java:comp/UserTransaction";
    }

}
