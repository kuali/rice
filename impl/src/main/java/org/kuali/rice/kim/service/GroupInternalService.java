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

/**
 * This is a description of what this class does 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface GroupInternalService {
    /**
     * Updates ActionItems for workgroup members according to membership differences between the
     * twho workgroups.  Since the changeset of such an operation could potentially be quite large,
     * this method should schedule the changes to occur asynchronously to mitigate transaction
     * and concurent document modification issues.
     */
    public void updateActionItemsForWorkgroupChange(String oldKimGroupId, String newKimGroupId);

    /**
     * Updates the action list for a the given document for a user who was added to a workgroup.  This method will generate
     * new action items for the requests on the document which are for the workgroup.  This method will also verify that
     * the user is, in fact, still a member of the workgroup at the time of the invocation of this method before
     * generating the action items.
     */
    public void updateActionListForUserAddedToGroup(String principalId, String groupId);

    /**
     * Updates the action list for a the given document for a user who was removed from a workgroup.  This will delete
     * any action items for the given user on the document which were sent to that user because they were a
     * member of the workgroup.  This method will also verify that the user is still no longer a member of the workgroup
     * at the time of the method invocation before removing the action items.
     */
    public void updateActionListForUserRemovedFromGroup(String principalId, String groupId);
}