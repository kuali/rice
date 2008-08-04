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
package org.kuali.rice.database;

import java.sql.Connection;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.util.pooling.ByPassConnection;
import org.springmodules.orm.ojb.support.LocalDataSourceConnectionFactory;

/**
 * <p>A subclass of the standard Spring LocalDataSourceConnectionFactory (that supplies
 * OJB with connections to datasource defined in Spring) that wraps the connection in a
 * ByPassConnection so that the JTA defined in the managed environment can control the
 * transaction.</p>
 * <p>This class works in concert with the following parameter in the corresponding OJB properties
 * config:</p>
 * <blockquote>
 *   <code>
 *     ConnectionFactoryClass=org.kuali.rice.database.SpringConnectionFactoryManagedImpl
 *   </code>
 * </blockquote>
 * Apparently this strategy is not required in OJB 1.0.4:
 * http://db.apache.org/ojb/release-notes.txt
 * <blockquote>
 *   "ConnectionFactoryManagedImpl is declared deprecated. Now OJB automatic detect the
 *   running JTA-transaction and suppress critical method calls on the used connection"
 * </blockquote>
 * <br/>
 * Details:
 * <blockquote>This is to overcome a shortcoming in Spring/OJB that in 1.03 you can't
 * run in a managed environment with Spring and OJB if Spring is giving OJB its Connections
 * through it's LocalDataSourceConnectionFactory (which doesn't wrap the connection in a bypass
 * connection).  This is suppressing commits - so that JTA can do it.
 * If one uses JNDI to grab the DS this wouldn't be necessary because both
 * Spring and OJB would grab the DS from there and we'd be cool.
 * The ByPassConnection is a class that comes with OJB which essentially turns calls
 * to commit, rollback, etc. into NO-OPS.  The reason it does this is because, in a
 * managed environment these methods are invoked on the individual connections by
 * the transaction manager and if you call the explicitly from the code they will
 * typically throw an exception (depending on the JTA implementation).
 * </blockquote>
 * (what about TransactionAwareDataSourceConnectionFactory mentioned in Spring docs?)
 */
public class SpringConnectionFactoryManagedImpl extends LocalDataSourceConnectionFactory {

	public Connection lookupConnection(JdbcConnectionDescriptor jcd) throws LookupException {
		return new ByPassConnection(super.lookupConnection(jcd));
	}

	protected Connection newConnectionFromDriverManager(JdbcConnectionDescriptor arg0) throws LookupException {
		throw new UnsupportedOperationException("Not supported in managed environment");
	}
}