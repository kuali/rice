package org.kuali.rice.kew.api.document.actions;

import java.util.List;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.Identifiable;

public interface ActionRequestContract extends Identifiable {

	String getId();
	
	ActionRequestType getActionRequested();
	
	ActionRequestStatus getStatus();
	
	boolean isCurrent();
	
	DateTime getDateCreated();
	
	String getResponsibilityId();
	
	String getDocumentId();
	
	String getRouteMethod();
	
	int getPriority();
	
	String getAnnotation();
	
	String getActionTakenId();
	
	String getGroupId();
	
	RecipientType getRecipientType();
	
	ActionRequestPolicy getRequestPolicy();
	
	String getResponsibilityDescription();
	
	boolean isForceAction();
	
	String getPrincipalId();
	
	// TODO delegation type enum
	
	String getParentActionRequestId();
	
	String getRoleName();
	
	String getQualifiedRoleName();
	
	String getQualifiedRoleNameLabel();
	
	String getNodeName();
	
	String getRequestLabel();
	
	ActionTakenContract getActionTaken();
	
	List<? extends ActionRequestContract> getChildrenRequests();
		
}
