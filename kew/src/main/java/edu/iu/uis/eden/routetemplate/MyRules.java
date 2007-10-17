/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.routetemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of rules uses by the web tier for rendering and interacting
 * with the rules GUI.
 *
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MyRules implements Serializable {


	private static final long serialVersionUID = 9178574188141418571L;
	private List rules;

    public MyRules() {
        rules = new ArrayList();
    }

    public void addRule(RuleBaseValues rule, Integer counter) {
        boolean alreadyAdded = false;
        int location = 0;
        for (Iterator ruleIter = getRules().iterator(); ruleIter.hasNext();) {
            RuleBaseValues ruleRow = (RuleBaseValues) ruleIter.next();

            if (counter != null && counter.intValue() == location) {
                ruleRow.setActiveInd(rule.getActiveInd());
                ruleRow.setCurrentInd(rule.getCurrentInd());
                ruleRow.setLockVerNbr(rule.getLockVerNbr());
                ruleRow.setDescription(rule.getDescription());
                ruleRow.setIgnorePrevious(rule.getIgnorePrevious());
                ruleRow.setDocTypeName(rule.getDocTypeName());
                ruleRow.setFromDate(rule.getFromDate());
                ruleRow.setToDate(rule.getToDate());
                ruleRow.setRuleTemplate(rule.getRuleTemplate());
                ruleRow.setVersionNbr(rule.getVersionNbr());
                ruleRow.setResponsibilities(rule.getResponsibilities());
                ruleRow.setRuleExtensions(rule.getRuleExtensions());
                
                alreadyAdded = true;
            }
            location++;
        }
        if (!alreadyAdded) {
            getRules().add(rule);
        }
    }

    public RuleBaseValues getRule(int index) {
        while (getRules().size() <= index) {
            RuleBaseValues rule = new RuleBaseValues();
//            rule.setPreviousVersion(new RuleBaseValues());
            getRules().add(rule);
        }
        return (RuleBaseValues) getRules().get(index);
    }

    public List getRules() {
        return rules;
    }

    public void setRules(List rules) {
        this.rules = rules;
    }
}