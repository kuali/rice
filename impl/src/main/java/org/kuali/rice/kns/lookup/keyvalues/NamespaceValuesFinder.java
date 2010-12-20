/*
 * Copyright 2006-2008 The Kuali Foundation
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

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kns.bo.Namespace;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KeyValuesService;

/**
 * This class...
 * 
 * 
 */
public class NamespaceValuesFinder extends KeyValuesBase {

    private static NamespaceComparator comparator = new NamespaceComparator();
    
    @Override
	public List<KeyValue> getKeyValues() {

        // get a list of all CampusTypes
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List<Namespace> bos = (List) boService.findAll(Namespace.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( bos == null ) {
        	bos = new ArrayList<Namespace>(0);
        } else {
        	bos = new ArrayList<Namespace>( bos );
        }

        // sort using comparator.
        Collections.sort(bos, comparator);

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>( bos.size() );
        labels.add(new ConcreteKeyValue("", ""));
        for ( Namespace bo : bos ) {
            labels.add( new ConcreteKeyValue(bo.getNamespaceCode(), bo.getCodeAndDescription() ) );
        }
        return labels;
    }
}
