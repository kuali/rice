/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.useroptions;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Sits on top of the UserOptionsTable and manages certain aspects of action list refresh behaviors.
 * This service could probably be broken up and it's dao put somewhere else and injected in the appropriate places.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface UserOptionsService {
    public Collection<UserOptions> findByWorkflowUser(String principalId);
    public List<UserOptions> findByUserQualified(String principalId, String likeString);
    public void save(UserOptions userOptions);
    public void save(String principalId, Map<String,String> optionsMap);
    public void save(String principalId, String optionId, String optionValue);
    public void deleteUserOptions(UserOptions userOptions);
    public UserOptions findByOptionId(String optionId, String principalId);
    public Collection<UserOptions> findByOptionValue(String optionId, String optionValue);
    public boolean refreshActionList(String principalId);
    public void saveRefreshUserOption(String principalId);
}
