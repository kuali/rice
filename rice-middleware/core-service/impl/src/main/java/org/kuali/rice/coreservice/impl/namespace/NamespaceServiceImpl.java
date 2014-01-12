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
package org.kuali.rice.coreservice.impl.namespace;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.singletonMap;

public class NamespaceServiceImpl implements NamespaceService {
    private DataObjectService dataObjectService;

    @Override
	public Namespace getNamespace(String code) {
        if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException("the code is blank");
        }
        return NamespaceBo.to(dataObjectService.find(NamespaceBo.class, new CompoundKey(singletonMap("code", code))));
	}

    @Override
    public List<Namespace> findAllNamespaces() {
        QueryResults<NamespaceBo> namespaceBos = dataObjectService.findMatching(
                    NamespaceBo.class,QueryByCriteria.Builder.andAttributes(new HashMap<String, String>()).build());
        List<Namespace> namespaces = new ArrayList<Namespace>();
        if(namespaceBos != null){
            for (NamespaceBo bo : namespaceBos.getResults()) {
                namespaces.add(NamespaceBo.to(bo));
            }
        }

        return Collections.unmodifiableList(namespaces);
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }
    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
