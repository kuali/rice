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
package edu.iu.uis.eden.useroptions;

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Sits on top of the UserOptionsTable and manages certain aspects of action list refresh behaviors.
 * This service could probably be broken up and it's dao put somewhere else and injected in the appropriate places.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface UserOptionsService {
    public Collection findByWorkflowUser(WorkflowUser user);
    public List findByUserQualified(WorkflowUser user, String likeString);
    public void save(UserOptions userOptions);
    public void save(WorkflowUser user, String optionId, String optionValue);
    public void deleteUserOptions(UserOptions userOptions);
    public UserOptions findByOptionId(String optionId, WorkflowUser user);
    public Collection findByOptionValue(String optionId, String optionValue);
    public boolean refreshActionList(WorkflowUser user);
    public void saveRefreshUserOption(WorkflowUser user);
}