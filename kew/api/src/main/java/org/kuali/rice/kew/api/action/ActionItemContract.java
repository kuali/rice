package org.kuali.rice.kew.api.action;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.delegation.DelegationType;

public interface ActionItemContract {
    String getId();
    DateTime getDateTimeAssigned();
    String getActionRequestCd();
    String getActionRequestId();
    String getDocumentId();
    String getDocTitle();
    String getDocLabel();
    String getDocHandlerURL();
    String getDocName();
    String getResponsibilityId();
    String getRoleName();
    String getDateAssignedString();
    String getActionToTake();
    DelegationType getDelegationType();
    Integer getActionItemIndex();
    String getGroupId();
    String getPrincipalId();
    String getDelegatorGroupId();
    String getDelegatorPrincipalId();
}
