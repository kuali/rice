/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.impl.parameter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.api.parameter.ParameterKey;
import org.kuali.rice.core.api.parameter.ParameterQueryResults;
import org.kuali.rice.core.api.parameter.ParameterRepositoryService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ParameterRepositoryServiceImpl implements ParameterRepositoryService {
    private static final String SUB_PARAM_SEPARATOR = "=";

    private BusinessObjectService businessObjectService;

    @Override 
    public void createParameter(Parameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("parameter is null");
        }

        final ParameterKey key = ParameterKey.create(parameter.getApplicationCode(), parameter.getNamespaceCode(), parameter.getComponentCode(), parameter.getName());
        final Parameter existing = getParameter(key);
        if (existing != null && existing.getApplicationCode().equals(parameter.getApplicationCode())) {
            throw new IllegalStateException("the parameter to create already exists: " + parameter);
        }

        businessObjectService.save(ParameterBo.from(parameter));
    } 

    @Override
    public void updateParameter(Parameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("parameter is null");
        }

        final ParameterKey key = ParameterKey.create(parameter.getApplicationCode(), parameter.getNamespaceCode(), parameter.getComponentCode(), parameter.getName());
        final Parameter existing = getParameter(key);
        if (existing == null) {
            throw new IllegalStateException("the parameter does not exist: " + parameter);
        }

        final Parameter toUpdate;
        if (!existing.getApplicationCode().equals(parameter.getApplicationCode())) {
            final Parameter.Builder builder = Parameter.Builder.create(parameter);
            builder.setApplicationCode(existing.getApplicationCode());
            toUpdate = builder.build();
        } else {
            toUpdate = parameter;
        }

        businessObjectService.save(ParameterBo.from(toUpdate));
    }

    @Override
    public Parameter getParameter(ParameterKey key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", key.getName());
        map.put("applicationCode", key.getApplicationCode());
        map.put("namespaceCode", key.getNamespaceCode());
        map.put("componentCode", key.getComponentCode());
        ParameterBo bo =  businessObjectService.findByPrimaryKey(ParameterBo.class, Collections.unmodifiableMap(map));

        if (bo == null & !KNSConstants.DEFAULT_APPLICATION_CODE.equals(key.getApplicationCode())) {
            map.put("applicationCode", KNSConstants.DEFAULT_APPLICATION_CODE);
            bo = businessObjectService.findByPrimaryKey(ParameterBo.class, Collections.unmodifiableMap(map));
        }

        return ParameterBo.to(bo);
    }

    @Override
    public String getParameterValueAsString(ParameterKey key) {
        final Parameter p =  getParameter(key);
        return p != null ? p.getValue() : null;
    }

    @Override
    public Boolean getParameterValueAsBoolean(ParameterKey key) {
        final Parameter p =  getParameter(key);
        final String value =  p != null ? p.getValue() : null;
        if (value == null) {
            return null;
        }

        final Boolean bValue;
        if ("Y".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
            bValue = Boolean.TRUE;
        } else if ("N".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            bValue = Boolean.FALSE;
        } else {
            bValue = null;
        }
        return bValue;
    }

    @Override
    public Collection<String> getParameterValuesAsString(ParameterKey key) {
        return splitOn(getParameterValueAsString(key), ";");
    }

    @Override
    public String getSubParameterValueAsString(ParameterKey key, String subParameterName) {
        if (StringUtils.isBlank(subParameterName)) {
            throw new IllegalArgumentException("subParameterName is blank");
        }

        Collection<String> values = getParameterValuesAsString(key);
        return getSubParameter(values, subParameterName);
    }

    @Override
    public Collection<String> getSubParameterValuesAsString(ParameterKey key, String subParameterName) {
       return splitOn(getSubParameterValueAsString(key, subParameterName), ",");
    }

    private String getSubParameter(Collection<String> values, String subParameterName) {
        for (String value : values) {
            if (subParameterName.equals(StringUtils.substringBefore(value, SUB_PARAM_SEPARATOR))) {
                return StringUtils.trimToNull(StringUtils.substringAfter(value, SUB_PARAM_SEPARATOR));
            }
        }
        return null;
    }

    private Collection<String> splitOn(String strValues, String delim) {
        if (StringUtils.isEmpty(delim)) {
            throw new IllegalArgumentException("delim is empty");
        }

        if (strValues == null || StringUtils.isBlank(strValues)) {
            return Collections.emptyList();
        }

        final Collection<String> values = new ArrayList<String>();
        for (String value : strValues.split(delim)) {
            values.add(value.trim());
        }

        return Collections.unmodifiableCollection(values);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

	@Override
	public ParameterQueryResults findParameters(QueryByCriteria<Parameter> queryByCriteria) {
		
		// TODO - implement this operation
		
		throw new UnsupportedOperationException("implement me!");
	}
    
    
}