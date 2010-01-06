/*
 * Copyright 2006-2007 The Kuali Foundation
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
import java.util.Collection;
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KeyValuesService;

/**
 * This class...
 * 
 * 
 */
public class CampusValuesFinder extends KeyValuesBase {

	protected static List<KeyLabelPair> campusCache = null;
	
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
	public List<KeyLabelPair> getKeyValues() {
    	if ( campusCache == null ) {
    		synchronized ( this.getClass() ) {
				
		        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
		        Collection<Campus> codes = (Collection<Campus>)boService.findAll(Campus.class);
		        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
		        labels.add(new KeyLabelPair("", ""));
		        for ( Campus campus : codes ) {
		            labels.add(new KeyLabelPair(campus.getCampusCode(), campus.getCampusCode() + " - " + campus.getCampusName()));
		        }
		
		        campusCache = labels;
			}
    	}
    	return campusCache;
    }

}
