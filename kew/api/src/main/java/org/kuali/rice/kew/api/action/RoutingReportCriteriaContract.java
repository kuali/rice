package org.kuali.rice.kew.api.action;

import java.util.List;

public interface RoutingReportCriteriaContract {
    String getDocumentId();
	String getTargetNodeName();
    List<String> getTargetPrincipalIds();
    String getRoutingPrincipalId();
    String getDocumentTypeName();
    String getXmlContent();
    List<String> getRuleTemplateNames();
    List<String> getNodeNames();
    List<? extends RoutingReportActionToTakeContract> getActionsToTake();
    boolean isActivateRequests();
    boolean isFlattenNodes();
}
