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
package edu.iu.uis.eden.routetemplate.web;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * A Struts ActionForm for {@link RuleAttributeReportAction}.  It just 
 * takes the id of the RuleAttribute to display.  It stores the RuleAttribute
 * it looks up internally, for view read purposes only.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleAttributeReportForm extends ActionForm {
    private Long ruleAttributeId;
    private RuleAttribute attribute;

    protected void setRuleAttribute(RuleAttribute ra) {
        attribute = ra;
    }

    public RuleAttribute getRuleAttribute() {
        return attribute;
    }

    public void setRuleAttributeId(Long id) {
        this.ruleAttributeId = id;
    }

    public Long getRuleAttributeId() {
        return ruleAttributeId;
    }
}