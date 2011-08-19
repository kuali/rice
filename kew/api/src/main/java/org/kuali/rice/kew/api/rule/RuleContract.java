package org.kuali.rice.kew.api.rule;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface RuleContract {
    /**
     * Unique Id for Template of Rule.
     *
     * <p>
     * This is the unique Id of the rule template of the rule
     * </p>
     *
     * @return ruleTemplateId
     */
    String getRuleTemplateId();

    /**
     * The active indicator for the rule.
     *
     * @return true if active false if not.
     */
    boolean isActive();

    /**
     * The description of the rule.
     *
     * @return description
     */
    String getDescription();

    /**
     * The documentTypeName of the rule.
     *
     * @return documentTypeName
     */
    String getDocTypeName();

    /**
     * The ending date of the rule.
     *
     * <p>This is the date from which the rule stops being be used</p>
     *
     * @return fromDate
     */
    DateTime getFromDate();

    /**
     * The ending date of the rule.
     *
     * <p>This is the date from which the rule starts to be used</p>
     *
     * @return toDate
     */
    DateTime getToDate();

    /**
     * Shows if rule will force its action.
     *
     * @return boolean value representing if action is forced
     */
    boolean isForceAction();

    /**
     * List of rule responsibilities associated with the Rule
     *
     * @return ruleResponsibilities
     */
    List<? extends RuleResponsibilityContract> getRuleResponsibilities();

    /**
     * rule Extensions are a key, value representation provided in a Map that extend a normal Rule
     *
     * @return ruleExtensions
     */
    Map<String, String> getRuleExtensions();

    /**
     * Template Name of the Rule.
     *
     * <p>
     * This is the name of the rule template for the rule
     * </p>
     *
     * @return ruleTemplateName
     */
    String getRuleTemplateName();
}
