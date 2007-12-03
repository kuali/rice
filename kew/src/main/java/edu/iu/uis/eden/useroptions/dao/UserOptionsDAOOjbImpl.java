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
package edu.iu.uis.eden.useroptions.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.database.platform.Platform;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.useroptions.UserOptions;

public class UserOptionsDAOOjbImpl extends PersistenceBrokerDaoSupport implements UserOptionsDAO {

	public Long getNewOptionIdForActionList() {
        return (Long)this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
            	return getPlatform().getNextValSQL("SEQ_ACTION_LIST_OPTN", broker);
            }
        });
    }

	protected Platform getPlatform() {
    	return (Platform)GlobalResourceLoader.getService(KEWServiceLocator.DB_PLATFORM);
    }

    public List findByUserQualified(WorkflowUser user, String likeString) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
        criteria.addLike("optionId", likeString);
        return new ArrayList(this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(UserOptions.class, criteria)));
    }

    public void deleteByUserQualified(WorkflowUser user, String likeString) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("workflowId", user.getWorkflowUserId().getWorkflowId());
        criteria.addLike("optionId", likeString);
        this.getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(UserOptions.class, criteria));
    }

    public Collection findByWorkflowUser(WorkflowUser workflowUser) {
        UserOptions userOptions = new UserOptions();
        userOptions.setWorkflowId(workflowUser.getWorkflowUserId().getWorkflowId());
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(userOptions));
    }

    public void save(UserOptions userOptions) {
    	this.getPersistenceBrokerTemplate().store(userOptions);
    }

    public void deleteUserOptions(UserOptions userOptions) {
    	this.getPersistenceBrokerTemplate().delete(userOptions);
    }

    public UserOptions findByOptionId(String optionId, WorkflowUser workflowUser) {
        UserOptions userOptions = new UserOptions();
        userOptions.setOptionId(optionId);
        userOptions.setWorkflowId(workflowUser.getWorkflowUserId().getWorkflowId());
        return (UserOptions) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(userOptions));
    }

    public Collection findByOptionValue(String optionId, String optionValue) {
        UserOptions userOptions = new UserOptions();
        userOptions.setOptionId(optionId);
        userOptions.setOptionVal(optionValue);
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(userOptions));
    }
}