/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kew.impl.rule;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.rule.Rule;
import org.kuali.rice.kew.api.rule.RuleReportCriteria;
import org.kuali.rice.kew.api.rule.RuleService;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.service.KEWServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RuleServiceImpl implements RuleService {
    private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class);

    @Override
    public List<Rule> ruleReport(RuleReportCriteria ruleReportCriteria) {
        incomingParamCheck(ruleReportCriteria, "ruleReportCriteria");
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Executing rule report [responsibleUser=" + ruleReportCriteria.getResponsiblePrincipalId() + ", responsibleWorkgroup=" +
                    ruleReportCriteria.getResponsibleGroupId() + "]");
        }
        Collection<RuleBaseValues> rulesFound = KEWServiceLocator.getRuleService().searchByTemplate(
                ruleReportCriteria.getDocumentTypeName(), ruleReportCriteria.getRuleTemplateName(),
                ruleReportCriteria.getRuleDescription(), ruleReportCriteria.getResponsibleGroupId(),
                ruleReportCriteria.getResponsiblePrincipalId(), Boolean.valueOf(ruleReportCriteria.isConsiderGroupMembership()),
                Boolean.valueOf(ruleReportCriteria.isIncludeDelegations()), Boolean.valueOf(ruleReportCriteria.isActive()), ruleReportCriteria.getRuleExtensions(),
                ruleReportCriteria.getActionRequestCodes());
        List<org.kuali.rice.kew.api.rule.Rule> returnableRules = new ArrayList<Rule>(rulesFound.size());
        for (RuleBaseValues rule : rulesFound) {
            returnableRules.add(RuleBaseValues.to(rule));
        }
        return returnableRules;
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new RiceIllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String) object)) {
            throw new RiceIllegalArgumentException(name + " was blank");
        }
    }
}
