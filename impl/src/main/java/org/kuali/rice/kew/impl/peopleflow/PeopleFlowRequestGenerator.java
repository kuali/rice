package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.engine.RouteContext;

import java.util.List;

public interface PeopleFlowRequestGenerator {

    List<ActionRequestValue> generateRequests(RouteContext routeContext, PeopleFlowDefinition peopleFlow, ActionRequestType actionRequested);

}
