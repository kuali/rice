package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.peopleflow.MemberType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember;
import org.kuali.rice.kew.engine.RouteContext;

import java.util.List;

public class PeopleFlowRequestGeneratorImpl implements PeopleFlowRequestGenerator {

    @Override
    public List<ActionRequestValue> generateRequests(RouteContext routeContext, PeopleFlowDefinition peopleFlow, ActionRequestType actionRequested) {
        if (peopleFlow == null) {
            throw new IllegalArgumentException("peopleFlow was null");
        }
        if (!peopleFlow.isActive()) {
            throw new ConfigurationException("Attempted to route to a PeopleFlow that is not active! " + peopleFlow);
        }
        if (actionRequested == null) {
            actionRequested = ActionRequestType.APPROVE;
        }
        ActionRequestFactory factory = new ActionRequestFactory(routeContext);
        for (PeopleFlowMember member : peopleFlow.getMembers()) {
            // TODO - need to figure out how best to handle responsibility id for this
            generateRequestForMember(factory, peopleFlow.getId(), member, actionRequested);
        }
        return factory.getRequestGraphs();
    }

    protected void generateRequestForMember(ActionRequestFactory factory, String responsibilityId, PeopleFlowMember member, ActionRequestType actionRequested) {
        // TODO - description, responsibilityId, forceAction, approvePolicy, ruleId, annotation, request label
        // defaulting all of these at the moment as per below
        factory.addRootActionRequest(actionRequested.getCode(), member.getPriority(), toRecipient(member), null, responsibilityId, Boolean.FALSE, null, null);
    }

    private Recipient toRecipient(PeopleFlowMember member) {
        Recipient recipient;
        if (MemberType.PRINCIPAL == member.getMemberType()) {
            recipient = new KimPrincipalRecipient(member.getMemberId());
        } else if (MemberType.GROUP == member.getMemberType()) {
            recipient = new KimGroupRecipient(member.getMemberId());
        } else {
            // TODO - what about roles!
            throw new UnsupportedOperationException("implement me!!!");
        }
        return recipient;
    }
}
