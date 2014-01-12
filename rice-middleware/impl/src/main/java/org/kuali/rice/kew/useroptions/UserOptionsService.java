/**
 * Copyright 2005-2014 The Kuali Foundation
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

    /**
     * Finds {@link UserOptions} for the given workflow id.
     * @param principalId the workflow id to search by
     * @return a collection of {@link UserOptions} or an empty collection if no results were found.
     */
    Collection<UserOptions> findByWorkflowUser(String principalId);

    /**
     * Finds a collection of {@link UserOptions} for the given principal id and search string.
     * @param principalId the workflow id.
     * @param likeString the option id search string.
     * @return  A {@link List} of {@link UserOptions} or an empty collection if no results are found.
     */
    List<UserOptions> findByUserQualified(String principalId, String likeString);

    /**
     * Persists the given {@link UserOptions} to the datasource.
     * @param userOptions the {@link UserOptions} to persist to the datasource
     */
    void save(UserOptions userOptions);

    /**
     * This overridden method saves an option for each optionsMap entry, all for the given principalId.
     * @param principalId the unique identifier
     * @param optionsMap a {@link Map} of user options keyed with option ids
     */
    void save(String principalId, Map<String, String> optionsMap);

    /**
     * Combines the given parameters into an {@link UserOptions} and persists the object to the datasource.
     * @param principalId the principal id to persist to the datasource
     * @param optionId the option id to persist to the datasource
     * @param optionValue the option value to persist to the datasource
     */
    void save(String principalId, String optionId, String optionValue);

    /**
     * Removes the given {@link UserOptions} from the underlining datasource.
     * @param userOptions the {@link UserOptions} to delete
     */
    void deleteUserOptions(UserOptions userOptions);

    /**
     * Find a {@link UserOptions} for the given option id and principal id.
     * @param optionId the option id to search by.
     * @param principalId the workflow id to search by
     * @return a {@link UserOptions} or null if no results are found.
     */
    UserOptions findByOptionId(String optionId, String principalId);

    /**
     * Finds a {@link List} of {@link UserOptions} for the given email setting.
     * @param emailSetting the option value to search by.
     * @return a {@link List} of {@link UserOptions} or an empty collection if no results are found.
     */
    List<UserOptions> retrieveEmailPreferenceUserOptions(String emailSetting);
}
