/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.removereplace.web;

import java.io.Serializable;

import edu.iu.uis.eden.routetemplate.RuleBaseValues;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RemoveReplaceRule implements Serializable {

    private RuleBaseValues rule = new RuleBaseValues();
    private String ruleTemplateName = "";;
    private String warning = "";
    private boolean isSelected;
    private boolean disabled;

    public boolean isSelected() {
        return this.isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public RuleBaseValues getRule() {
        return this.rule;
    }
    public void setRule(RuleBaseValues rule) {
        this.rule = rule;
    }
    public String getRuleTemplateName() {
        return this.ruleTemplateName;
    }
    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }
    public String getWarning() {
        return warning;
    }
    public void setWarning(String warning) {
        this.warning = warning;
    }
    public boolean isDisabled() {
        return this.disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
