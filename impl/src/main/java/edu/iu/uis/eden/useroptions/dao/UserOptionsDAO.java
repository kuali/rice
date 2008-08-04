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

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.useroptions.UserOptions;

public interface UserOptionsDAO {

	public Collection findByWorkflowUser(WorkflowUser workflowUser);
	public List findByUserQualified(WorkflowUser user, String likeString);
    public void deleteByUserQualified(WorkflowUser user, String likeString);
	public void save(UserOptions userOptions);
	public void deleteUserOptions(UserOptions userOptions);
	public UserOptions findByOptionId(String optionId, WorkflowUser workflowUser);
	public Collection findByOptionValue(String optionId, String optionValue);
	public Long getNewOptionIdForActionList();
}
