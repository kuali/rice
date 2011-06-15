package org.kuali.rice.kew.api.document.actions;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.Identifiable;

public interface ActionTakenContract extends Identifiable {

	String getId();
	
	String getDocumentId();
	
	String getPrincipalId();
	
	String getDelegatorPrincipalId();
	
	String getDelegatorGroupId();
	
	ActionType getActionTaken();
	
	DateTime getActionDate();
	
	String getAnnotation();
	
	boolean isCurrent();
	
}
