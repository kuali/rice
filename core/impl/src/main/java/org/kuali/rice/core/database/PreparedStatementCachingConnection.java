/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbcp.PoolingConnection;
import org.apache.commons.pool.KeyedObjectPool;

/**
 * A subclass of Apache DBCP's PoolingConnection class.  This class generates a unique
 * toString value for each instance of this class, because XApool relies on such behavior
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PreparedStatementCachingConnection extends PoolingConnection {
	private String stringRepresentation;
	
	public PreparedStatementCachingConnection(Connection conn, KeyedObjectPool preparedStatementCache) {
		super(conn, preparedStatementCache);
		stringRepresentation = null;
	}

	@Override
	public String toString() {
		// for some reason, XAPool uses the connection's toString as a unique identifier for the connection
		// this method should provide that unique identifier
		if (stringRepresentation == null) {
			stringRepresentation = "PreparedStatementCachingConnection: " + System.identityHashCode(this); 
		}
		return stringRepresentation;
	}
}
