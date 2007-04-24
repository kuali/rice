/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.kuali.core.rule.event.PreRulesCheckEvent;

/**
 * Interface providing callback method from maintenance documents to check warning/ questions conditions before rules are called.
 */
public interface PreRulesCheck {

    /**
     * Callback method from Maintenance action that allows checks to be done and response redirected via the PreRulesCheckEvent
     * 
     * @param form
     * @param request
     * @param event
     * @return boolean indicating whether routing should stop or continue
     */
    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event);
}
