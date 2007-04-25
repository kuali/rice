/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;

/**
 * A rule evaluated from an entry in the parameters table.
 */
public class KualiParameterRule implements Serializable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiParameterRule.class);
    private final String name;
    private final String parameterText;
    private final String ruleOperator;
    private final boolean ruleActiveIndicator;
    private final Set parameterValueSet;

    /**
     * Constructor.
     * 
     * @param name
     * @param parameterText
     * @param ruleOperator
     * @param ruleActiveIndicator
     */
    public KualiParameterRule(String name, String parameterText, String ruleOperator, boolean ruleActiveIndicator) {
        this.name = name;
        this.parameterText = parameterText;
        this.ruleOperator = ruleOperator;
        this.ruleActiveIndicator = ruleActiveIndicator;
        this.parameterValueSet = makeSet(parameterText);
    }

    /**
     * Checks a given value against the rule based on the following guidelines: 1) If rule is inactive, the rule always passes 2) If
     * ruleOperator is 'A' If value is in set, rule passes else rule fails 3) If ruleOperator is 'D' if value is in set, rule fails
     * else rule passes
     * 
     * @param value - value to check
     * 
     * @return boolean indicating the rule success
     */
    public boolean failsRule(String value) {
        if (isUsable()) {
            if (isAllowedRule() && !parameterValueSet.contains(value)) {
                return true;
            }
            else {
                if (isDeniedRule() && parameterValueSet.contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUsable() {
        return ruleActiveIndicator && !StringUtils.isBlank(parameterText);
    }

    /**
     * @return whether ruleOperator is 'A'
     */
    public boolean isAllowedRule() {
        return Constants.APC_ALLOWED_OPERATOR.equals(ruleOperator);
    }

    /**
     * @return whether ruleOperator is 'D'
     */
    public boolean isDeniedRule() {
        return Constants.APC_DENIED_OPERATOR.equals(ruleOperator);
    }

    /**
     * This method is a convenience method for getting the inverse of failsRule(value). It returns the opposite of whatever
     * failsRule returns for the same value.
     * 
     * @param value - a value to check
     * 
     * @return inverse of failsRule
     */
    public boolean succeedsRule(String value) {
        boolean b = !failsRule(value);
        LOG.debug("succeedsRule() rule: " + this.name + " Value: " + value + " op: " + this.ruleOperator + " values " + this.parameterText + " result " + b);
        return b;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the parameterText
     */
    public String getParameterText() {
        return parameterText;
    }

    /**
     * @return the ruleActiveIndicator
     */
    public boolean getRuleActiveIndicator() {
        return ruleActiveIndicator;
    }

    /**
     * @return the ruleOperator
     */
    public String getRuleOperator() {
        return ruleOperator;
    }

    /**
     * @return the parameterValueSet. A set of each parameter values.
     */
    public Set getParameterValueSet() {
        return parameterValueSet;
    }

    /**
     * @return the error key for the operator
     */
    public String getErrorMessageKey() {
        if (isAllowedRule()) {
            return KeyConstants.ERROR_APPLICATION_PARAMETERS_ALLOWED_RESTRICTION;
        }
        else {
            return KeyConstants.ERROR_APPLICATION_PARAMETERS_DENIED_RESTRICTION;
        }
    }

    protected static Set makeSet(String elementString) {
        Set result = new LinkedHashSet();
        if (!StringUtils.isBlank(elementString)) {
            result.addAll(Arrays.asList(elementString.split("[,;]")));
        }
        return result;
    }

    /**
     * Creates a rule that is the logical AND of the given rules. The created rule succeeds where all of the given rules would
     * succeed, and fails where any of the given rules would fail. Using this single equivalent rule can provide the user with a
     * friendly, consistent error message, instead of inconsistent error messages from applying multiple separate rules to the same
     * property. The name lists the component rules, to help with maintenance.
     * 
     * @param rules
     * @return a single equivalent rule
     */
    public static KualiParameterRule and(KualiParameterRule[] rules) {
        boolean active = false;
        List names = new ArrayList();
        // Using ordered sets for deterministic unit tests.
        TreeSet deniedValues = new TreeSet();
        TreeSet allowedValues = null;
        for (int i = 0; i < rules.length; i++) {
            KualiParameterRule rule = rules[i];
            names.add(rule.getName());
            if (rule.isUsable()) {
                active = true;
                if (rule.isAllowedRule()) {
                    if (allowedValues == null) {
                        allowedValues = new TreeSet(rule.parameterValueSet);
                    }
                    else {
                        allowedValues.retainAll(rule.parameterValueSet);
                    }
                }
                else {
                    if (rule.isDeniedRule()) {
                        deniedValues.addAll(rule.parameterValueSet);
                    }
                }
            }
        }
        if (allowedValues == null) {
            return new KualiParameterRule("and" + names, makeText(deniedValues), Constants.APC_DENIED_OPERATOR, active);
        }
        else {
            allowedValues.removeAll(deniedValues);
            // The ";" is a work-around to enforce allowing disjoint values (i.e., allowing nothing, always failing).
            // If the text were empty then the rule would be ignored, always succeeding.
            String text = allowedValues.isEmpty() ? ";" : makeText(allowedValues);
            return new KualiParameterRule("and" + names, text, Constants.APC_ALLOWED_OPERATOR, active);
        }
    }

    private static String makeText(Set values) {
        return StringUtils.join(values.iterator(), ';');
    }

    /**
     * Convenience method to call {@link #and(KualiParameterRule[])} with two elements.
     * 
     * @see #and(KualiParameterRule[])
     */
    public static KualiParameterRule and(KualiParameterRule ruleA, KualiParameterRule ruleB) {
        // todo: JDK 1.5 ... args will make this method unnecessary
        return and(new KualiParameterRule[] { ruleA, ruleB });
    }

    /**
     * Generates a String with the parameter values formatted prettily, with a comma and space between each value for multivalued
     * param values.
     * 
     * NOTE: order of original values may not be preserved (parse parameterText instead).
     * 
     * For example: value "2;3,4" would be formatted as "2, 3, 4"
     * 
     * @return a pretty string
     */
    public String getPrettyParameterValueString() {
        Iterator i = parameterValueSet.iterator();
        StringBuffer buf = new StringBuffer();
        if (i.hasNext()) {
            buf.append(i.next());
        }
        while (i.hasNext()) {
            // TODO: add an "and" and/or "or" before the last value?
            buf.append(", ").append(i.next());
        }
        return buf.toString();
    }
}