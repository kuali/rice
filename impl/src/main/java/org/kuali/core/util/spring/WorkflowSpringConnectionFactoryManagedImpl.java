/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.util.spring;

import java.sql.Connection;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.util.pooling.ByPassConnection;
import org.springmodules.orm.ojb.support.LocalDataSourceConnectionFactory;

public class WorkflowSpringConnectionFactoryManagedImpl extends LocalDataSourceConnectionFactory {
    public Connection lookupConnection(JdbcConnectionDescriptor jcd) throws LookupException {
        return new ByPassConnection(super.lookupConnection(jcd));
    }

    protected Connection newConnectionFromDriverManager(JdbcConnectionDescriptor arg0) throws LookupException {
        throw new UnsupportedOperationException("Not supported in managed environment");
    }
}
