/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.coreservice.impl.service;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.coreservice.framework.namespace.NamespaceEbo;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.impl.RemoteModuleServiceBase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CoreServiceRemoteModuleService extends RemoteModuleServiceBase {

    private static final org.apache.log4j.Logger LOG =
            org.apache.log4j.Logger.getLogger(CoreServiceRemoteModuleService.class);

    private static final String NAMESPACE_EBO_PK = "code";

    @Override
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass,
            Map<String, Object> fieldValues) {
        T result = null;
        if(NamespaceEbo.class.isAssignableFrom(businessObjectClass)){
            if(fieldValues.containsKey(NAMESPACE_EBO_PK)){
                Namespace namespace = getNamespaceService().getNamespace((String)fieldValues.get(NAMESPACE_EBO_PK));
                result = (T) NamespaceBo.from(namespace);
            }
        }
        return result;
    }

    @Override
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
            Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        if(NamespaceEbo.class.isAssignableFrom(businessObjectClass)) {

            //
            // sticks and bubblegum query
            //

            List<Namespace> namespaces = getNamespaceService().findAllNamespaces();

            List<NamespaceBo> results = new ArrayList<NamespaceBo>(namespaces.size());

            for (Namespace namespace : namespaces) {
                NamespaceBo namespaceBo = NamespaceBo.from(namespace);
                boolean fieldsMatch = true;
                for (Map.Entry<String, Object> fieldValue : fieldValues.entrySet()) {
                    if (!fieldMatches(namespaceBo, fieldValue)) {
                        fieldsMatch = false;
                        break;
                    }
                }
                if (fieldsMatch) { results.add(namespaceBo); }
            }
            return (List<T>)results;
        }

        return Collections.emptyList();
    }

    private boolean fieldMatches(Object ebo, Map.Entry<String,Object> fieldValue) {
        try {
            return ObjectUtils.equals(fieldValue.getValue(), BeanUtils.getProperty(ebo, fieldValue.getKey()));
        } catch (IllegalAccessException e) {
            LOG.warn("querying " + ebo.getClass().getName() + " for an inaccessible field called '" + fieldValue.getKey() + "'" );
        } catch (InvocationTargetException e) {
            LOG.warn("exception querying " + ebo.getClass().getName() + " for a field called '" + fieldValue.getKey() + "'" );
        } catch (NoSuchMethodException e) {
            LOG.warn("querying " + ebo.getClass().getName() + " for an invalid field called '" + fieldValue.getKey() + "'" );
        }
        return false;
    }

    @Override
    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
        if(NamespaceEbo.class.isAssignableFrom(boClass)) { return true; }
        return false;
    }

    @Override
    public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
        if(NamespaceEbo.class.isAssignableFrom(boClass)) { return true; }
        return false;
    }

    // Lazy init holder class, see Effective Java item 71
    private static class NamespaceServiceHolder {
        static final NamespaceService namespaceService = CoreServiceApiServiceLocator.getNamespaceService();
    }

    private NamespaceService getNamespaceService() {
        return NamespaceServiceHolder.namespaceService;
    }
}
