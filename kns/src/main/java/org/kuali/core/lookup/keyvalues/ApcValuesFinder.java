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
package org.kuali.core.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

public class ApcValuesFinder extends KeyValuesBase {

    private String group;
    private String parameterName;

    public ApcValuesFinder() {
        super();
    }

    public ApcValuesFinder(String group, String parameterName) {
        super();
        this.group = group;
        this.parameterName = parameterName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public List getKeyValues() {
        KualiConfigurationService configService = KNSServiceLocator.getKualiConfigurationService();
        KualiParameterRule rule = configService.getApplicationParameterRule(group, parameterName);
        // now we need to retrieve the parm values
        String[] parmValues = { "" };
        if (StringUtils.isNotBlank(rule.getParameterText())) {
            parmValues = rule.getParameterText().split(";");
        }

        List activeLabels = new ArrayList();
        activeLabels.add(new KeyLabelPair("", ""));
        for (int i = 0; i < parmValues.length; i++) {
            String parm = parmValues[i];
            activeLabels.add(new KeyLabelPair(parm, parm));
        }
        return activeLabels;
    }

}
