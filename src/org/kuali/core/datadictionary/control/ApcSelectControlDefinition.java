/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.keyvalues.ApcValuesFinder;

public class ApcSelectControlDefinition extends SelectControlDefinition {

    private String group;
    private String parameterName;

    public ApcSelectControlDefinition() {
        super();
        setValuesFinderClass(new ApcValuesFinder().getClass());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        if (StringUtils.isBlank(group)) {
            throw new IllegalArgumentException("invalid (blank) group in <apcSelect>");
        }
        this.group = group;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("invalid (blank) parameterName in <apcSelect>");
        }
        this.parameterName = parameterName;
    }

    public boolean isApcSelect() {
        return true;
    }

}
