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
package edu.iu.uis.eden.workgroup;

import java.util.List;

import org.kuali.workflow.workgroup.WorkgroupType;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Defines a service for routing of Workgroups within Workflow.  This interface should be implemented to
 * provide routing capabilities for a Workgroup implementation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkgroupRoutingService {

	/**
	 * Return the name of the default DocumentType defining the Workgroup routing process.
	 */
	public String getDefaultDocumentTypeName();

	/**
	 * Create a new WorkflowDocument for a Workgroup document type for the given initiator and WorkgroupType
	 */
	public WorkflowDocument createWorkgroupDocument(UserSession initiator, WorkgroupType workgroupType) throws WorkflowException;

	/**
	 * Determines whether or not the Workgroup with the given id is locked for routing.  If it is locked, then the
	 * id of the Workflow document which causes the document to be locked is returned.  Otherwise, null is returned.
	 */
	public Long getLockingDocumentId(GroupId groupId) throws WorkflowException;

	/**
	 * Routes the given Workgroup as the specified user.
	 */
	public void route(Workgroup workgroup, UserSession user, String annotation) throws WorkflowException;

	/**
	 * Routes the given Workgroup with the blanket approve command.
	 */
	public void blanketApprove(Workgroup workgroup, UserSession user, String annotation) throws WorkflowException;

	/**
	 * Invoked after the Workgroup document has finished routing.  Effectively activates the routed workgroup by
	 * marking the current version (if there is one) as non-current and the new version as current.
	 */
	public void activateRoutedWorkgroup(Long documentId) throws WorkflowException;

	/**
	 * Retrieves the Workgroup which has the given Document ID
	 */
	public Workgroup findByDocumentId(Long documentId) throws WorkflowException;

	/**
	 * Saves the workgroup by marking replacing existing version of workgroup (if there is one) with
	 * the given workgroup.
	 */
	public void versionAndSave(Workgroup workgroup) throws WorkflowException;

	/**
	 * Replaces entities who are members of the given set of workgroups with the specified new entity.  In this case
	 * the Id can be the id of either a Workgroup or a User.
	 *
	 * <p>This method should handle any versioning of the workgroups that is required.
	 */
	public void replaceWorkgroupInvolvement(Id entityToBeReplaced, Id newEntity, List<Long> workgroupIds, Long documentId) throws WorkflowException;

	/**
	 * Removes entities who are members of the given set of workgroups.  In the case that a targeted workgroup
	 * contains only a single member, the workgroup will be inactivated instead of removing the final member.
	 * The Id can be the id of either a Workgroup or a User.
	 *
	 * <p>This method should handle any versioning of the workgroups that is required.
	 */
	public void removeWorkgroupInvolvement(Id entityToBeRemoved, List<Long> workgroupIds, Long documentId) throws WorkflowException;

}
