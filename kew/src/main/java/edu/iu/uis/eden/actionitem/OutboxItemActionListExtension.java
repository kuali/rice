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
package edu.iu.uis.eden.actionitem;

/**
 * Outbox item.  An extension of {@link ActionItemActionListExtension} for OJB.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class OutboxItemActionListExtension extends ActionItemActionListExtension {

    private static final long serialVersionUID = 5776214610837858304L;

    public OutboxItemActionListExtension() {}
    
    public OutboxItemActionListExtension(ActionItem actionItem) {
	this.setActionRequestCd(actionItem.getActionRequestCd());
	this.setActionRequestId(actionItem.getActionRequestId());
	this.setActionToTake(actionItem.getActionToTake());
	this.setDateAssigned(actionItem.getDateAssigned());
	this.setDelegationType(actionItem.getDelegationType());
	this.setDelegatorWorkflowId(actionItem.getDelegatorWorkflowId());
	this.setDelegatorWorkgroupId(actionItem.getDelegatorWorkgroupId());
	this.setDocHandlerURL(actionItem.getDocHandlerURL());
	this.setDocLabel(actionItem.getDocLabel());
	this.setDocName(actionItem.getDocName());
	this.setDocTitle(actionItem.getDocTitle());
	this.setResponsibilityId(actionItem.getResponsibilityId());
	this.setRoleName(actionItem.getRoleName());
	this.setRouteHeader(actionItem.getRouteHeader());
	this.setRouteHeaderId(actionItem.getRouteHeaderId());
	this.setWorkflowId(actionItem.getWorkflowId());
	this.setWorkgroupId(actionItem.getWorkgroupId());
    }
    
}