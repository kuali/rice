/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.dao.proxy;

import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.UniversalUserDao;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.rice.kew.exception.EdenUserNotFoundException;
import org.kuali.rice.util.OrmUtils;

import edu.iu.uis.eden.user.BaseWorkflowUser;
import edu.iu.uis.eden.user.WorkflowUser;

public class UniversalUserDaoProxy implements UniversalUserDao {

    private UniversalUserDao universalUserDaoJpa;
    private UniversalUserDao universalUserDaoOjb;
	
    private UniversalUserDao getDao(Class clazz) {
    	return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? universalUserDaoJpa : universalUserDaoOjb; 
    }
    
	public UniversalUser getUser(org.kuali.core.bo.user.UserId userId) throws UserNotFoundException {
		return getDao(UniversalUser.class).getUser(userId);
	}

	public WorkflowUser getWorkflowUser(edu.iu.uis.eden.user.UserId userId) throws EdenUserNotFoundException {
		return getDao(BaseWorkflowUser.class).getWorkflowUser(userId);
	}

	public void save(WorkflowUser workflowUser) {
		getDao(BaseWorkflowUser.class).save(workflowUser);
	}

	public List search(WorkflowUser user, boolean useWildCards) {
		return getDao(BaseWorkflowUser.class).search(user, useWildCards);
	}

	public void setUniversalUserDaoJpa(UniversalUserDao universalUserDaoJpa) {
		this.universalUserDaoJpa = universalUserDaoJpa;
	}

	public void setUniversalUserDaoOjb(UniversalUserDao universalUserDaoOjb) {
		this.universalUserDaoOjb = universalUserDaoOjb;
	}

}