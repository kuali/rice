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
package org.kuali.rice.krad.uif.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.impl.RemoteModuleServiceBase;

/**
 * Mock module service for testing UIF components without back-end interaction.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockModuleService extends RemoteModuleServiceBase {

    private final List<? extends ExternalizableBusinessObject> instances;
    private final List<Class<?>> lookupable;
    private final List<Class<?>> inquirable;

    /**
     * Create a mock module service for a given set of instances and key properties.
     * 
     * @param instances List of mock instances to make available.
     * @param lookupable List of classes to treat as lookupable.
     * @param inquirable List of classes to treat as inquirable.
     */
    public MockModuleService(List<? extends ExternalizableBusinessObject> instances, List<Class<?>> lookupable,
            List<Class<?>> inquirable) {
        this.instances = instances;
        this.lookupable = lookupable;
        this.inquirable = inquirable;
    }

    /**
     * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObject(java.lang.Class,
     *      java.util.Map)
     */
    @Override
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass,
            Map<String, Object> fieldValues) {
        for (ExternalizableBusinessObject instance : instances) {
            boolean match = true;

            fieldLoop: for (Entry<String, Object> fieldValue : fieldValues.entrySet()) {
                Object matchValue = ObjectPropertyUtils.getPropertyValue(instance, fieldValue.getKey());
                if (matchValue == null ? fieldValue.getValue() != null : !matchValue.equals(fieldValue.getValue())) {
                    match = false;
                    break fieldLoop;
                }
            }

            if (match) {
                return businessObjectClass.cast(instance);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectsList(java.lang.Class,
     *      java.util.Map)
     */
    @Override
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
            Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        List<T> rv = new ArrayList<T>();
        for (ExternalizableBusinessObject instance : instances) {
            boolean match = true;

            fieldLoop: for (Entry<String, Object> fieldValue : fieldValues.entrySet()) {
                Object matchValue = ObjectPropertyUtils.getPropertyValue(instance, fieldValue.getKey());
                if (matchValue == null ? fieldValue.getValue() != null : !matchValue.equals(fieldValue.getValue())) {
                    match = false;
                    break fieldLoop;
                }
            }

            if (match) {
                rv.add(businessObjectClass.cast(instance));
            }
        }
        return rv;
    }

    /**
     * @see org.kuali.rice.krad.service.ModuleService#isExternalizableBusinessObjectLookupable(java.lang.Class)
     */
    @Override
    public boolean isExternalizableBusinessObjectLookupable(@SuppressWarnings("rawtypes") Class boClass) {
        return lookupable.contains(boClass);
    }

    /**
     * @see org.kuali.rice.krad.service.ModuleService#isExternalizableBusinessObjectInquirable(java.lang.Class)
     */
    @Override
    public boolean isExternalizableBusinessObjectInquirable(@SuppressWarnings("rawtypes") Class boClass) {
        return inquirable.contains(boClass);
    }

    boolean isResponsible(Class<?> boClass) {
        if (isExternalizableBusinessObjectInquirable(boClass) || isExternalizableBusinessObjectLookupable(boClass)) {
            return true;
        }
        
        for (ExternalizableBusinessObject instance : instances) {
            if (boClass.isInstance(instance)) {
                return true;
            }
        }
        
        return false;
    }

}
