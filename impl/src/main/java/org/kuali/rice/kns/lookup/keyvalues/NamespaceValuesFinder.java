/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.kns.bo.Namespace;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class...
 * 
 * 
 */
public class NamespaceValuesFinder extends KeyValuesBase {

    private static NamespaceComparator comparator = new NamespaceComparator();
    
    public List<KeyLabelPair> getKeyValues() {

        // get a list of all CampusTypes
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List<Namespace> bos = (List) boService.findAll(Namespace.class);

        // sort using comparator.
        Collections.sort(bos, comparator);

        // create a new list (code, descriptive-name)
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>( bos.size() );
        labels.add(new KeyLabelPair("", ""));
        for ( Namespace bo : bos ) {
            labels.add( new KeyLabelPair(bo.getNamespaceCode(), bo.getCodeAndDescription() ) );
        }
        return labels;
    }
}