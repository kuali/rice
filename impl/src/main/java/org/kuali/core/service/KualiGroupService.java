/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.service;

import java.util.List;

import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;

/**
 * This interface defines methods that a KualiGroup Service must provide.
 * 
 * 
 */
public interface KualiGroupService {

    /**
     * method to get a KualiGroup based on groupName
     * 
     * @param groupName
     * @return KualiGroup if a group by the name passed in exists
     * @throws GroupNotFoundException
     */
    public KualiGroup getByGroupName(String groupName) throws GroupNotFoundException;

    /**
     * method to get a list of the users KualiGroups
     * 
     * @param kualiUser
     * @return a list of the groups that the user is a member of
     */
    public List getUsersGroups(UniversalUser kualiUser);


    /**
     * @return true if a group with that name exists
     */
    public boolean groupExists(String groupName);

}