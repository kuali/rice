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
package org.kuali.core.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.core.bo.ParameterType;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class...
 * 
 * 
 */
public class ParameterTypeValuesFinder extends KeyValuesBase {

    private static ParameterTypeComparator comparator = new ParameterTypeComparator();
    
    public List<KeyLabelPair> getKeyValues() {

        // get a list of all CampusTypes
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List<ParameterType> bos = (List) boService.findAll(ParameterType.class);

        // sort using comparator.
        Collections.sort(bos, comparator);

        // create a new list (code, descriptive-name)
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>( bos.size() );

        for ( ParameterType bo : bos ) {
            labels.add( new KeyLabelPair(bo.getParameterTypeCode(), bo.getParameterTypeName() ) );
        }

        return labels;
    }
}