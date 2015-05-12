/*
 * Copyright 2006-2015 The Kuali Foundation
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
package org.kuali.rice.kew.actionitem.oracle.queue;

import oracle.jms.AQjmsFactory;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

/**
 * Class used to create a connection factory to read messages from the actn_item_changed_mq message queue.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class OracleAQConnectionFactory {

    private DataSource dataSource;

    /**
     * Creates a ConnectionFactory using the datasource in the spring bean
     *
     * @return the connection factory
     */
    public ConnectionFactory createConnectionFactory() throws Exception {
        return AQjmsFactory.getQueueConnectionFactory(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}