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
package org.kuali.rice.kns.dao;

import java.util.List;

import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;


/**
 * This interface defines basic methods that UniversalUser Dao's must provide
 * 
 * 
 */

public interface UniversalUserDao {
    public UniversalUser getUser(org.kuali.rice.kns.bo.user.UserId userId) throws UserNotFoundException;

    public WorkflowUser getWorkflowUser(org.kuali.rice.kew.user.UserId userId) throws KEWUserNotFoundException;

    public void save(WorkflowUser workflowUser);

    public List search(WorkflowUser user, boolean useWildCards);
    
}
