/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class returns all rule types of rules.
 */
public class RuleTypeValuesFinder extends KeyValuesBase {

    private boolean blankOption;

    @Override
	public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        if(blankOption){
            keyValues.add(new ConcreteKeyValue("", ""));
        }

        // TODO: Only select the rules for the specific context of the agenda
        // Map<String, String> fieldValues = new HashMap<String, String>();
        // fieldValues.put(KrmsImplConstants.PropertyNames.Context.CONTEXT_ID, form.getAgenda().getContextId);
        // Collection<ContextValidRuleBo> contextValidRules = KRADServiceLocator.getBusinessObjectService().findMatching(ContextValidRuleBo.class, fieldValues);
        Collection<ContextValidRuleBo> contextValidRules = KRADServiceLocator.getBusinessObjectService().findAll(ContextValidRuleBo.class);
        for (ContextValidRuleBo contextValidRule : contextValidRules) {
            keyValues.add(new ConcreteKeyValue(contextValidRule.getRuleType().getId(), contextValidRule.getRuleType().getName()));
        }
        return keyValues;
    }

    /**
     * @return the blankOption
     */
    public boolean isBlankOption() {
        return this.blankOption;
    }

    /**
     * @param blankOption the blankOption to set
     */
    public void setBlankOption(boolean blankOption) {
        this.blankOption = blankOption;
    }

}
