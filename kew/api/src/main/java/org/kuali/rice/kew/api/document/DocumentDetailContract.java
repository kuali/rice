package org.kuali.rice.kew.api.document;

import java.util.List;

import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;

public interface DocumentDetailContract {

	Document getDocument();
	List<ActionRequest> getActionRequests();
	List<ActionTaken> getActionsTaken();
	List<RouteNodeInstance> getRouteNodeInstances();
	
}
