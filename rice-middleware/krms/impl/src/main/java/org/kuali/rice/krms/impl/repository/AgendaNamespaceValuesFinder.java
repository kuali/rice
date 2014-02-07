/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Helper class that returns all namespaces that have contexts associated w/ them.
 */
public class AgendaNamespaceValuesFinder extends UifKeyValuesFinderBase {

    @Override
    public List<KeyValue> getKeyValues(ViewModel model) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        // TODO: this is not efficient -- do a smart 'select distinct' and make sure we have a good index!

        QueryByCriteria contextCrit = QueryByCriteria.Builder.create().build();
        QueryResults<ContextBo> contexts = KRADServiceLocator.getDataObjectService().findMatching(ContextBo.class, contextCrit);

        QueryResults<NamespaceBo> namespaceBos = KradDataServiceLocator.getDataObjectService().findMatching(NamespaceBo.class,
                QueryByCriteria.Builder.create().build());
        Map<String, String> namespaceCodeToName = new HashMap<String, String>();
        if (!namespaceBos.getResults().isEmpty()) {
            for (NamespaceBo namespaceBo : namespaceBos.getResults()) {
                namespaceCodeToName.put(namespaceBo.getCode(), namespaceBo.getName());
            }
        }

        List<String> namespaceCodes = new ArrayList<String>();

        if (!CollectionUtils.isEmpty(contexts.getResults())) {
            for (ContextBo context : contexts.getResults()) {
                if (!namespaceCodes.contains(context.getNamespace())) {
                    // add if not already there
                    namespaceCodes.add(context.getNamespace());
                }
            }
        }

        Collections.sort(namespaceCodes);

        for (String namespaceCode : namespaceCodes) {
            String namespaceName = namespaceCode;
            if (namespaceCodeToName.containsKey(namespaceCode)) {
                namespaceName = namespaceCodeToName.get(namespaceCode);
            }
            keyValues.add(new ConcreteKeyValue(namespaceCode, namespaceName));
        }

        return keyValues;
    }
}
