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
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.kns.bo.CampusType;
import org.kuali.rice.kns.bo.CampusTypeImpl;
import org.kuali.rice.kns.service.KNSServiceLocatorInternal;
import org.kuali.rice.kns.service.KeyValuesService;

/**
 * This class...
 * 
 * 
 */
public class CampusTypeValuesFinder extends KeyValuesBase {

    @Override
	public List<KeyValue> getKeyValues() {

        // get a list of all CampusTypes
        KeyValuesService boService = KNSServiceLocatorInternal.getKeyValuesService();
        List<CampusType> campusTypes = (List) boService.findAll(CampusTypeImpl.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( campusTypes == null ) {
        	campusTypes = new ArrayList<CampusType>(0);
        } else {
        	campusTypes = new ArrayList<CampusType>( campusTypes );
        }
       

        // sort using comparator.
        Collections.sort(campusTypes, CampusTypeComparator.INSTANCE);

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>();

        for (CampusType campusType : campusTypes) {
            labels.add(new ConcreteKeyValue(campusType.getCampusTypeCode(), campusType.getCampusTypeCode() + " - " + campusType.getCampusTypeName()));
        }

        return labels;
    }
}
