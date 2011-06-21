package org.kuali.rice.kew.api.document;

import java.util.List;

import org.kuali.rice.core.api.mo.common.Identifiable;

public interface RouteNodeInstanceContract extends Identifiable {

	String getDocumentId();

	String getBranchId();
	
	String getRouteNodeId();
	
	String getProcessId();
	
	String getName();
	
	boolean isActive();
	
	boolean isComplete();
	
	boolean isInitial();
	
	List<? extends RouteNodeInstanceStateContract> getState();
	
}
