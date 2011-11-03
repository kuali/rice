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
package org.kuali.rice.core.impl.namespace;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.namespace.Namespace;
import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.krad.service.BusinessObjectService;

import static java.util.Collections.singletonMap;

public class NamespaceServiceImpl implements NamespaceService {

    private BusinessObjectService boService;

    @Override
	public Namespace getNamespace(String code) {
        if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException("the code is blank");
        } 

        return NamespaceBo.to(boService.findByPrimaryKey(NamespaceBo.class, singletonMap("code", code)));
	}

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
    }
}
