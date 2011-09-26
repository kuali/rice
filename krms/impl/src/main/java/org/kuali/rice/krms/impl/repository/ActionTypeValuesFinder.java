/*
 * Copyright 2011 The Kuali Foundation
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

import org.hibernate.mapping.TableOwner;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krms.impl.util.KrmsImplConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class returns all action types of rules.
 */
public class ActionTypeValuesFinder extends KeyValuesBase {

    private boolean blankOption;

    @Override
	public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        if(blankOption){
            keyValues.add(new ConcreteKeyValue("", ""));
        }

        // TODO: Only select the actions for the specific context of the agenda
        // Map<String, String> fieldValues = new HashMap<String, String>();
        // fieldValues.put(KrmsImplConstants.PropertyNames.Context.CONTEXT_ID, form.getAgenda().getContextId);
        // Collection<ContextValidActionBo> contextValidActions = KRADServiceLocator.getBusinessObjectService().findMatching(ContextValidActionBo.class, fieldValues);
        Collection<ContextValidActionBo> contextValidActions = KRADServiceLocator.getBusinessObjectService().findAll(ContextValidActionBo.class);
        for (ContextValidActionBo contextValidAction : contextValidActions) {
            keyValues.add(new ConcreteKeyValue(contextValidAction.getActionType().getId(), contextValidAction.getActionType().getName()));
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
