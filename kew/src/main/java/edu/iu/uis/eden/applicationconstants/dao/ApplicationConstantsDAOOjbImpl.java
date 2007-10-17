/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.applicationconstants.dao;

import java.util.List;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.applicationconstants.ApplicationConstant;

/**
 * OJB implementation of the {@link ApplicationConstantsDAO}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ApplicationConstantsDAOOjbImpl extends PersistenceBrokerDaoSupport implements ApplicationConstantsDAO {

    public void saveConstant(ApplicationConstant applicationConstant) {
        this.getPersistenceBrokerTemplate().store(applicationConstant);
    }

    public void deleteConstant(ApplicationConstant applicationConstant) {
    	this.getPersistenceBrokerTemplate().delete(applicationConstant);
    }

    public ApplicationConstant findByName(String applicationConstantName) {
        ApplicationConstant applicationConstant = new ApplicationConstant();
        applicationConstant.setApplicationConstantName(applicationConstantName);
        return (ApplicationConstant) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(applicationConstant));    
    }

    @SuppressWarnings("unchecked")
	public List<ApplicationConstant> findAll() {
        return (List<ApplicationConstant>) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ApplicationConstant.class));
    }
}