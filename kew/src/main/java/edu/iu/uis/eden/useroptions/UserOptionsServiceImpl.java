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
package edu.iu.uis.eden.useroptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.useroptions.dao.UserOptionsDAO;

public class UserOptionsServiceImpl implements UserOptionsService {

    private UserOptionsDAO userOptionsDAO;

    private static final Properties defaultProperties = new Properties();

    static {
        defaultProperties.setProperty(EdenConstants.EMAIL_RMNDR_KEY, EdenConstants.EMAIL_RMNDR_WEEK_VAL);
    }

    private Long getNewOptionIdForActionList() {
		return getUserOptionsDAO().getNewOptionIdForActionList();
	}

    public List findByUserQualified(WorkflowUser user, String likeString) {
        if ((user == null)) {
            return new ArrayList(0);
        }
        return this.getUserOptionsDAO().findByUserQualified(user, likeString);
    }
    
    public UserOptions findByOptionId(String optionId, WorkflowUser user) {
        if (optionId == null || "".equals(optionId) || user == null || user.getWorkflowUserId() == null) {
            return null;
        }
        return this.getUserOptionsDAO().findByOptionId(optionId, user);
    }
    
    public Collection findByOptionValue(String optionId, String optionValue){
        return this.getUserOptionsDAO().findByOptionValue(optionId, optionValue);
    }
    
    public Collection findByWorkflowUser(WorkflowUser user) {
        return this.getUserOptionsDAO().findByWorkflowUser(user);
    }

    public void save(UserOptions userOptions) {
        this.getUserOptionsDAO().save(userOptions);
    }

    public void deleteUserOptions(UserOptions userOptions) {
        this.getUserOptionsDAO().deleteUserOptions(userOptions);
    }

    public void save(WorkflowUser user, String optionId, String optionValue) {
        UserOptions option = findByOptionId(optionId, user);
        if (option == null) {
            option = new UserOptions();
            option.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
        }
        option.setOptionId(optionId);
        option.setOptionVal(optionValue);
        getUserOptionsDAO().save(option);
    }
    
    public boolean refreshActionList(WorkflowUser user) {
        List options = findByUserQualified(user, EdenConstants.RELOAD_ACTION_LIST + "%");
        boolean refresh = ! options.isEmpty();
        if (refresh && user != null) {
            getUserOptionsDAO().deleteByUserQualified(user, EdenConstants.RELOAD_ACTION_LIST + "%");
        }
        return refresh;
    }
    
    public void saveRefreshUserOption(WorkflowUser user) {
        save(user, EdenConstants.RELOAD_ACTION_LIST + new Date().getTime() + getNewOptionIdForActionList(), "true");
    }

    public UserOptionsDAO getUserOptionsDAO() {
        return userOptionsDAO;
    }

    public void setUserOptionsDAO(UserOptionsDAO optionsDAO) {
        userOptionsDAO = optionsDAO;
    }
    

}