/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.responsibility.dao;

import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.database.platform.Platform;

public class ResponsibilityIdDAOOjbImpl extends PersistenceBrokerDaoSupport implements ResponsibilityIdDAO {

	public Long getNewResponsibilityId() {
        return (Long)this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
            	return getPlatform().getNextValSQL("SEQ_RESPONSIBILITY_ID", broker);
            }
        });
    }

	protected Platform getPlatform() {
    	return (Platform)GlobalResourceLoader.getService(KEWServiceLocator.DB_PLATFORM);
    }

}