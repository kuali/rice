/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface GroupUpdateService {

	GroupInfo createGroup(GroupInfo groupInfo) throws UnsupportedOperationException;

    GroupInfo updateGroup(String groupId, GroupInfo groupInfo) throws UnsupportedOperationException;

    boolean addGroupToGroup(String childId, String parentId) throws UnsupportedOperationException;
    
    boolean removeGroupFromGroup(String childId, String parentId) throws UnsupportedOperationException;
    
    boolean addPrincipalToGroup(String principalId, String groupId) throws UnsupportedOperationException;
    
    boolean removePrincipalFromGroup(String principalId, String groupId) throws UnsupportedOperationException;
    
    void removeAllGroupMembers( String groupId ) throws UnsupportedOperationException;
}
