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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.routetemplate.web.WebRuleBaseValues;

/**
 * A collection of rules uses by the web tier for rendering and interacting
 * with the rules GUI.
 *
 * @see WebRuleBaseValues
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MyRules2 implements Serializable {

	private static final long serialVersionUID = -5405951937698910224L;
	private List rules = new ArrayList();

    public void addRule(WebRuleBaseValues rule) {
        addRule(rule, new Integer(rules.size()));
    }
    
    
    
    public void addRule(WebRuleBaseValues rule, Integer counter) {
        boolean alreadyAdded = false;
        int location = 0;
        for (Iterator ruleIter = getRules().iterator(); ruleIter.hasNext();) {
            WebRuleBaseValues ruleRow = (WebRuleBaseValues)ruleIter.next();

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
    
    public WebRuleBaseValues getRule(int index) {
        while (getRules().size() <= index) {
            addRule(new WebRuleBaseValues());
        }
        return (WebRuleBaseValues) getRules().get(index);
    }
    

    public List getRules() {
        return rules;
    }

    public void setRules(List rules) {
        this.rules = rules;
    }
    
    public int getSize() {
        return getRules().size();
    }
    
    public static class MyRule implements Serializable{

		private static final long serialVersionUID = 1531326501498462883L;
		private RuleBaseValues rule;
        private List ruleTemplateAttributes = new ArrayList();
        private Map fields = new HashMap();
        private List roles = new ArrayList();
        private String fromDate;
        private String toDate;
        
        public MyRule() {
            this(new RuleBaseValues());
        }
        public MyRule(RuleBaseValues rule) {
            this.rule = rule;
        }
        public Map getFields() {
            return fields;
        }
        public void setFields(Map fields) {
            this.fields = fields;
        }
        public RuleBaseValues getRule() {
            return rule;
        }
        public void setRule(RuleBaseValues rule) {
            this.rule = rule;
        }
        public List getRuleTemplateAttributes() {
            return ruleTemplateAttributes;
        }
        public void setRuleTemplateAttributes(List ruleTemplateAttributes) {
            this.ruleTemplateAttributes = ruleTemplateAttributes;
        }
        public List getRoles() {
            return roles;
        }
        public void setRoles(List roles) {
            this.roles = roles;
        }
        
        public String getFromDate() {
            return fromDate;
        }
        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }
        public String getToDate() {
            return toDate;
        }
        public void setToDate(String toDate) {
            this.toDate = toDate;
        }
    }

}