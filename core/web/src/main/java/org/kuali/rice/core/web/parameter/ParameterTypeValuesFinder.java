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
package org.kuali.rice.core.web.parameter;

import org.kuali.rice.core.impl.parameter.ParameterTypeBo;
import org.kuali.rice.core.impl.parameter.ParameterTypeComparator;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KeyValuesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class...
 * 
 * 
 */
public class ParameterTypeValuesFinder extends KeyValuesBase {
    
    @Override
	public List<KeyValue> getKeyValues() {

        // get a list of all ParameterTypes
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List<ParameterTypeBo> bos = (List) boService.findAll(ParameterTypeBo.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( bos == null ) {
        	bos = new ArrayList<ParameterTypeBo>(0);
        } else {
        	bos = new ArrayList<ParameterTypeBo>( bos );
        }

        // sort using comparator.
        Collections.sort(bos, ParameterTypeComparator.INSTANCE);

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>( bos.size() );

        for ( ParameterTypeBo bo : bos ) {
            labels.add( new ConcreteKeyValue(bo.getCode(), bo.getName() ) );
        }

        return labels;
    }
}
