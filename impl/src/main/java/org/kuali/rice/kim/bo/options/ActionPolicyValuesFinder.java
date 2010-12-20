/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ActionPolicyValuesFinder extends KeyValuesBase {
    @Override
	public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        for (Object key : CodeTranslator.approvePolicyLabels.keySet()) {
        	labels.add(new ConcreteKeyValue((String)key,(String)CodeTranslator.approvePolicyLabels.get(key)));
        }
        return labels;
    }    

}
