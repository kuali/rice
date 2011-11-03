/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.core.web.namespace;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.impl.namespace.NamespaceBo;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KeyValuesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NamespaceValuesFinder extends KeyValuesBase {

    @Override
	public List<KeyValue> getKeyValues() {

        // get a list of all Namespaces
        KeyValuesService boService = KRADServiceLocator.getKeyValuesService();
        List<NamespaceBo> bos = (List<NamespaceBo>) boService.findAll(NamespaceBo.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( bos == null ) {
        	bos = new ArrayList<NamespaceBo>(0);
        } else {
        	bos = new ArrayList<NamespaceBo>( bos );
        }

        // sort using comparator.
        Collections.sort(bos, NamespaceComparator.INSTANCE); 

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>( bos.size() );
        labels.add(new ConcreteKeyValue("", ""));
        for ( NamespaceBo bo : bos ) {
            labels.add( new ConcreteKeyValue(bo.getCode(), bo.getName() ) );
        }
        return labels;
    }

    private static class NamespaceComparator implements Comparator<NamespaceBo> {
        public static final Comparator<NamespaceBo> INSTANCE = new NamespaceComparator();

        @Override
        public int compare(NamespaceBo o1, NamespaceBo o2) {
            return o1.getCode().compareTo( o2.getCode() );
        }
    }
}
