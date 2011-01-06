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
package org.kuali.rice.kns.bo.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocatorInternal;

/**
 * This class...
 */
public class StateValuesFinder extends KeyValuesBase {

	private static List<KeyValue> labels;
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
	public List<KeyValue> getKeyValues() {
    	if ( labels == null ) {
    		List<State> baseCodes = KNSServiceLocatorInternal.getStateService().findAllStates();
    		List<State> codes = new ArrayList<State>( baseCodes );
    		Collections.sort(codes, new Comparator<State> () {
				@Override
				public int compare(State o1, State o2) {
					return o1.getPostalStateName().compareTo(o2.getPostalStateName());
				}
    		});
    		
    		List<KeyValue> newLabels = new ArrayList<KeyValue>();
	        newLabels.add(new ConcreteKeyValue("", ""));
	        for (State state : codes) {
	            if(state.isActive()) {
	                newLabels.add(new ConcreteKeyValue(state.getPostalStateCode(), state.getPostalStateName()));
	            }
	        }
	        labels = newLabels;
    	}

        return labels;
    }

    @Override
    public void clearInternalCache() {
    	labels = null;
    }
}
