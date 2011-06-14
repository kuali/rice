package org.kuali.rice.kew.api.document.actions;

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
	
	// TODO finish contract for ActionRequest
	
}
