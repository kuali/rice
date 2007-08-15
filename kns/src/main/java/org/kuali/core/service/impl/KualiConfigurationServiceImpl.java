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
package org.kuali.core.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessRule;
import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class KualiConfigurationServiceImpl extends AbstractStaticConfigurationServiceImpl implements KualiConfigurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiConfigurationServiceImpl.class);
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.core.service.KualiConfigurationService#getApplicationParameterValues(java.lang.String, java.lang.String)
     */
    public String[] getApplicationParameterValues(String scriptName, String parameter) {
        LOG.debug("getApplicationParameterValues() started");

        FinancialSystemParameter param = getApplicationParameter(scriptName, parameter);
        if (!param.isFinancialSystemMultipleValueIndicator()) {
            throw new ApplicationParameterException(scriptName, parameter, "single value found where multiple expected");
        }
        String text = param.getFinancialSystemParameterText();
        if (StringUtils.isEmpty(text)) {
            return new String[0];
        }
        return text.split(";");
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#getApplicationParameterValue(java.lang.String, java.lang.String)
     */
    public String getApplicationParameterValue(String scriptName, String parameter) {
        LOG.debug("getApplicationParameterValue() started");

        FinancialSystemParameter param = getApplicationParameter(scriptName, parameter);
        if (param.isFinancialSystemMultipleValueIndicator()) {
            throw new ApplicationParameterException(scriptName, parameter, "multiple values found where one expected");
        }
        String value = param.getFinancialSystemParameterText();
        if (StringUtils.isBlank(value)) {
            throw new ApplicationParameterException(scriptName, parameter, "blank value");
        }
        return value;
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#hasApplicationParameterRule(java.lang.String, java.lang.String)
     */
    public boolean hasApplicationParameterRule(String scriptName, String parameter) {
        LOG.debug("hasApplicationParameterRule() started");

        return !getApplicationRules(scriptName, parameter).isEmpty();
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#getApplicationParameterRule(java.lang.String, java.lang.String)
     */
    public KualiParameterRule getApplicationParameterRule(String scriptName, String parameter) {
        LOG.debug("getApplicationParameterRule() started");

        BusinessRule param = getApplicationRule(scriptName, parameter);

        return new KualiParameterRule(param.getRuleGroupName() + ":" + param.getRuleName(), param.getRuleText(), param.getRuleOperatorCode(), param.isFinancialSystemParameterActiveIndicator());
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#hasApplicationParameter(java.lang.String, java.lang.String)
     */
    public boolean hasApplicationParameter(String scriptName, String parameter) {
        LOG.debug("hasApplicationParameter() started");

        return !getApplicationParameters(scriptName, parameter).isEmpty();
    }

    private Collection getApplicationParameters(String scriptName, String parameter) {
        if (StringUtils.isBlank(scriptName)) {
            throw new IllegalArgumentException("blank scriptName: '" + scriptName + "'");
        }
        else if (StringUtils.isBlank(parameter)) {
            throw new IllegalArgumentException("blank parameter: '" + parameter + "'");
        }
        HashMap map = new HashMap();
        map.put(RiceConstants.PARM_SECTION_NAME_FIELD, scriptName);
        map.put(RiceConstants.PARM_PARM_NAME_FIELD, parameter);
        return businessObjectService.findMatching(FinancialSystemParameter.class, map);
    }

    private Collection getApplicationRules(String scriptName, String parameter) {
        if (StringUtils.isBlank(scriptName)) {
            throw new IllegalArgumentException("blank scriptName: '" + scriptName + "'");
        }
        else if (StringUtils.isBlank(parameter)) {
            throw new IllegalArgumentException("blank parameter: '" + parameter + "'");
        }
        HashMap map = new HashMap();
        map.put("ruleGroupName", scriptName);
        map.put("ruleName", parameter);
        return businessObjectService.findMatching(BusinessRule.class, map);
    }

    private FinancialSystemParameter getApplicationParameter(String scriptName, String parameter) {
        LOG.debug("getApplicationParameter() started");

        Collection c = getApplicationParameters(scriptName, parameter);
        switch (c.size()) {
            case 0:
                throw new ApplicationParameterException(scriptName, parameter, "not found");
            case 1:
                return (FinancialSystemParameter) c.iterator().next();
            default:
                throw new ApplicationParameterException(scriptName, parameter, "multiple found");
        }
    }


    /**
     * @see org.kuali.core.service.KualiConfigurationService#getApplicationRule(String, String)
     */
    public BusinessRule getApplicationRule(String scriptName, String parameter) {
        LOG.debug("getApplicationRule() started");

        Collection c = getApplicationRules(scriptName, parameter);
        switch (c.size()) {
            case 0:
                throw new ApplicationParameterException(scriptName, parameter, "not found");
            case 1:
                return (BusinessRule) c.iterator().next();
            default:
                throw new ApplicationParameterException(scriptName, parameter, "multiple found");
        }
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#getApplicationParameterIndicator(java.lang.String,
     *      java.lang.String)
     */
    public boolean getApplicationParameterIndicator(String scriptName, String parameter) {
        LOG.debug("getApplicationParameterIndicator() started");

        FinancialSystemParameter fsp = getApplicationParameter(scriptName, parameter);

        if (fsp.isFinancialSystemMultipleValueIndicator()) {
            throw new ApplicationParameterException(scriptName, parameter, "This parameter is configured incorrectly.  It should be a single value only.");
        }
        if ("Y".equals(fsp.getFinancialSystemParameterText())) {
            return true;
        }
        else {
            if ("N".equals(fsp.getFinancialSystemParameterText())) {
                return false;
            }
            else {
                throw new ApplicationParameterException(scriptName, parameter, "This parameter is configured incorrectly.  The value should be Y or N.");
            }
        }
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#getRulesByGroup(java.lang.String)
     */
    public Map<String, KualiParameterRule> getRulesByGroup(String groupName) {
        LOG.debug("getParametersByGroup() started");

        Map<String, KualiParameterRule> out = new HashMap<String, KualiParameterRule>();

        HashMap map = new HashMap();
        map.put("ruleGroupName", groupName);
        Collection rules = businessObjectService.findMatching(BusinessRule.class, map);

        for (Iterator iter = rules.iterator(); iter.hasNext();) {
            BusinessRule param = (BusinessRule) iter.next();
            KualiParameterRule r = new KualiParameterRule(param.getRuleGroupName() + ":" + param.getRuleName(), param.getRuleText(), param.getRuleOperatorCode(), param.isFinancialSystemParameterActiveIndicator());
            out.put(param.getRuleName(), r);
        }
        return out;
    }

    /**
     * @see org.kuali.core.service.KualiConfigurationService#getParametersByGroup(java.lang.String)
     */
    public Map<String, FinancialSystemParameter> getParametersByGroup(String groupName) {
        LOG.debug("getParametersByGroup() started");
        HashMap map = new HashMap();

        Map<String, FinancialSystemParameter> out = new HashMap<String, FinancialSystemParameter>();

        map.put(RiceConstants.PARM_SECTION_NAME_FIELD, groupName);
        Collection parms = businessObjectService.findMatching(FinancialSystemParameter.class, map);

        for (Iterator iter = parms.iterator(); iter.hasNext();) {
            FinancialSystemParameter param = (FinancialSystemParameter) iter.next();
            out.put(param.getFinancialSystemParameterName(), param);
        }
        return out;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public boolean isProductionEnvironment() {
        return getPropertyString( RiceConstants.PROD_ENVIRONMENT_CODE_KEY ).equals( getPropertyString( RiceConstants.ENVIRONMENT_KEY ) );
    }

}
