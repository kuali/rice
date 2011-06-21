package org.kuali.rice.kew.api.action;

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
		
	int getPriority();
	
	String getAnnotation();
		
	RecipientType getRecipientType();
	
	String getPrincipalId();
	
	String getGroupId();
	
	ActionRequestPolicy getRequestPolicy();
	
	String getResponsibilityDescription();
	
	boolean isForceAction();
	
	DelegationType getDelegationType();
	
	String getRoleName();
	
	String getQualifiedRoleName();
	
	String getQualifiedRoleNameLabel();
	
	String getRouteNodeInstanceId();
	
	String getNodeName();
	
	String getRequestLabel();
	
	String getParentActionRequestId();
	
	ActionTakenContract getActionTaken();
	
	List<? extends ActionRequestContract> getChildRequests();
		
}
