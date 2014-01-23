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
package org.kuali.rice.kim.impl.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.krad.data.DataObjectService;

public class KimTypeInfoServiceImpl implements KimTypeInfoService {

    protected DataObjectService dataObjectService;
    
    @Override
    public KimType getKimType(final String id) throws RiceIllegalArgumentException {
        incomingParamCheck(id, "id");

        return KimTypeBo.to(dataObjectService.find(KimTypeBo.class, id));
    }

    @Override
    public KimType findKimTypeByNameAndNamespace(final String namespaceCode, final String name) throws RiceIllegalArgumentException {
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(name, "name");

        final Map<String, Object> crit = new HashMap<String, Object>(3);
        crit.put("namespaceCode", namespaceCode);
        crit.put("name", name);
        crit.put("active", Boolean.TRUE);

        QueryResults<KimTypeBo> bos = dataObjectService.findMatching(KimTypeBo.class, QueryByCriteria.Builder.andAttributes(crit).build());

        if (bos.getResults().size() > 1) {
            throw new IllegalStateException("multiple active results were found for the namespace code: " + namespaceCode + " and name: " + name);
        }

        return bos.getResults().size() > 0 ? KimTypeBo.to(bos.getResults().get(0)) : null;
    }

    @Override
    public Collection<KimType> findAllKimTypes() {
        QueryResults<KimTypeBo> bos
                = dataObjectService.findMatching(KimTypeBo.class, QueryByCriteria.Builder.forAttribute("active", Boolean.TRUE).build());
        Collection<KimType> ims = new ArrayList<KimType>(bos.getResults().size());

        for (KimTypeBo bo : bos.getResults()) {
            if (bo != null) {
                ims.add(KimTypeBo.to(bo));
            }
        }
        return Collections.unmodifiableCollection(ims);
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new RiceIllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String) object)) {
            throw new RiceIllegalArgumentException(name + " was blank");
        }
    }
}
