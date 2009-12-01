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

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This class...
 */
public class StateValuesFinder extends KeyValuesBase {

	private static List<KeyLabelPair> labels;
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
    	if ( labels == null ) {
    		List<State> baseCodes = KNSServiceLocator.getStateService().findAllStates();
    		List<State> codes = new ArrayList<State>( baseCodes );
    		Collections.sort(codes, new Comparator<State> () {
				public int compare(State o1, State o2) {
					return o1.getPostalStateName().compareTo(o2.getPostalStateName());
				}
    		});
    		
    		List<KeyLabelPair> newLabels = new ArrayList<KeyLabelPair>();
	        newLabels.add(new KeyLabelPair("", ""));
	        for (State state : codes) {
	            if(state.isActive()) {
	                newLabels.add(new KeyLabelPair(state.getPostalStateCode(), state.getPostalStateName()));
	            }
	        }
	        labels = newLabels;
    	}

        return labels;
    }

}
