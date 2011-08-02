package org.kuali.rice.kew.api.rule;

import org.kuali.rice.core.api.util.KeyValue;

import java.util.List;
import java.util.Map;

public interface RuleReportCriteriaContract {
    String getRuleDescription();
    String getDocumentTypeName();
    String getRuleTemplateName();
    List<String> getActionRequestCodes();
    String getResponsiblePrincipalId();
    String getResponsibleGroupId();
    String getResponsibleRoleName();
    Map<String, String> getRuleExtensions();
    boolean isActive();
    boolean isConsiderGroupMembership();
    boolean isIncludeDelegations();
}
