/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;

/**
 * This class returns list of parameter operator value pairs.
 * 
 * 
 */
public class ParameterOperatorValuesFinder extends KeyValuesBase {

	private static final List<KeyValue> KEY_VALUES;
	
	static {
		final List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("A", "Allowed"));
        keyValues.add(new ConcreteKeyValue("D", "Denied"));
		KEY_VALUES = Collections.unmodifiableList(keyValues);
	}
	
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
	public List<KeyValue> getKeyValues() {
        return KEY_VALUES;
    }
}
