package org.kuali.rice.kew.api.rule;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface RuleContract {

    String getRuleTemplateId();
    boolean isActive();
    String getDescription();
    String getDocTypeName();
    DateTime getFromDate();
    DateTime getToDate();
    boolean isForceAction();
    List<? extends RuleResponsibilityContract> getRuleResponsibilities();
    Map<String, String> getRuleExtensions();
    String getRuleTemplateName();
}
