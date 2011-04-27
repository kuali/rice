/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.service;

import java.util.List;

import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.bo.impl.GroupImpl;

/**
 * Provides internal notification services for the GroupServiceImpl.  It
 * specifically allows GroupServiceImpl to notify interested parties that
 * a group's membership has changed.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface GroupInternalService {
	/**
	 * Save the GroupImpl, being careful to reset the action document
	 * assignments based on any membership changes.
	 * 
	 * @param group
	 */
    public GroupImpl saveWorkgroup(GroupImpl group);

    /**
	 * Save the GroupImpl, being careful to reset the action document
	 * assignments based on any membership changes.
	 *
	 * @param group
	 */
    public Group saveWorkgroup(Group group);
	
    /**
     * Updates KEW for workgroup members according to membership differences between the
     * two workgroups.  Since the changeset of such an operation could potentially be quite large,
     * this method should schedule the changes to occur asynchronously to mitigate transaction
     * and concurrent document modification issues.
     */
    public void updateForWorkgroupChange( String groupId,
    		List<String> oldPrincipalIds, List<String> newPrincipalIds);

    /**
     * Updates KEW for a the given document for a user who was added to a workgroup.  This method will generate
     * new action items for the requests on the document which are for the workgroup.  This method will also verify that
     * the user is, in fact, still a member of the workgroup at the time of the invocation of this method before
     * generating the action items.
     */
    public void updateForUserAddedToGroup(String principalId, String groupId);

    /**
     * Updates KEW for a the given document for a user who was removed from a workgroup.  This will delete
     * any action items for the given user on the document which were sent to that user because they were a
     * member of the workgroup.  This method will also verify that the user is still no longer a member of the workgroup
     * at the time of the method invocation before removing the action items.
     */
    public void updateForUserRemovedFromGroup(String principalId, String groupId);
}
