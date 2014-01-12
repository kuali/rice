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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Mock module service for supporting UIF calls.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockKualiModuleService implements KualiModuleService, ApplicationContextAware, InitializingBean {

    private static MockKualiModuleService bootstrap;
    private static ApplicationContext applicationContext;
    private static Map<String, ModuleService> installedModuleServices = new LinkedHashMap<String, ModuleService>();
    
    private Map<String, List<String>> resourceBundleNames;

    private static ModuleConfiguration createMockModuleConfiguration(String namespaceCode,
            Class<? extends ExternalizableBusinessObject> boClass) {
        ModuleConfiguration rv = new ModuleConfiguration();
        rv.setApplicationContext(applicationContext);
        rv.setDataDictionaryService(KRADServiceLocatorWeb.getDataDictionaryService());
        rv.setInitializeDataDictionary(false);
        rv.setNamespaceCode(namespaceCode);
        @SuppressWarnings("rawtypes")
        Map<Class, Class> externalizableBusinessObjectImplementations = new HashMap<Class, Class>();
        externalizableBusinessObjectImplementations.put(boClass, boClass);
        rv.setExternalizableBusinessObjectImplementations(externalizableBusinessObjectImplementations);
        List<String> packagePrefixes = new ArrayList<String>();
        packagePrefixes.add(boClass.getPackage().getName());
        rv.setPackagePrefixes(packagePrefixes);
        rv.setResourceBundleNames(bootstrap.resourceBundleNames.get(namespaceCode));
        return rv;
    }

    public static <T extends ExternalizableBusinessObject> void registerModuleService(String moduleId,
            String namespaceCode, Class<T> boClass, boolean lookupable, boolean inquirable, List<T> instances) {
        MockModuleService mockService = new MockModuleService(instances, lookupable ? Collections
                .<Class<?>> singletonList(boClass) : Collections.<Class<?>> emptyList(), inquirable ? Collections
                .<Class<?>> singletonList(boClass) : Collections.<Class<?>> emptyList());
        mockService.setKualiModuleService(bootstrap);
        mockService.setModuleConfiguration(createMockModuleConfiguration(namespaceCode, boClass));
        installedModuleServices.put(moduleId, mockService);
    }

    public static class MockBusinessObject implements ExternalizableBusinessObject {

        @Override
        public void refresh() {
        }
    }
    
    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        bootstrap = this;
        if (resourceBundleNames == null) {
            resourceBundleNames = Collections.emptyMap();
        } else {
            for (Map.Entry<String, List<String>> resourceBundleEntry : resourceBundleNames.entrySet()) {
                registerModuleService(UUID.randomUUID().toString(), resourceBundleEntry.getKey(),
                        MockBusinessObject.class, false, false, Collections.<MockBusinessObject> emptyList());
            }
        }
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MockKualiModuleService.applicationContext = applicationContext;
    }

    /**
     * @return the resourceBundleNames
     */
    public Map<String, List<String>> getResourceBundleNames() {
        return this.resourceBundleNames;
    }

    /**
     * @param resourceBundleNames the resourceBundleNames to set
     */
    public void setResourceBundleNames(Map<String, List<String>> resourceBundleNames) {
        this.resourceBundleNames = resourceBundleNames;
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getInstalledModuleServices()
     */
    @Override
    public List<ModuleService> getInstalledModuleServices() {
        return new ArrayList<ModuleService>(installedModuleServices.values());
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getModuleService(java.lang.String)
     */
    @Override
    public ModuleService getModuleService(String moduleId) {
        return installedModuleServices.get(moduleId);
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getModuleServiceByNamespaceCode(java.lang.String)
     */
    @Override
    public ModuleService getModuleServiceByNamespaceCode(String namespaceCode) {
        for (ModuleService moduleService : installedModuleServices.values()) {
            if (moduleService.getModuleConfiguration().getNamespaceCode().equals(namespaceCode)) {
                return moduleService;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#isModuleServiceInstalled(java.lang.String)
     */
    @Override
    public boolean isModuleServiceInstalled(String namespaceCode) {
        for (ModuleService moduleService : installedModuleServices.values()) {
            if (moduleService.getModuleConfiguration().getNamespaceCode().equals(namespaceCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getResponsibleModuleService(java.lang.Class)
     */
    @Override
    public ModuleService getResponsibleModuleService(Class boClass) {
        for (ModuleService moduleService : installedModuleServices.values()) {
            if (moduleService.isResponsibleFor(boClass)) {
                return moduleService;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#setInstalledModuleServices(java.util.List)
     */
    @Override
    public void setInstalledModuleServices(List<ModuleService> moduleServices) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getDataDictionaryPackages()
     */
    @Override
    public List<String> getDataDictionaryPackages() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getNamespaceName(java.lang.String)
     */
    @Override
    public String getNamespaceName(String namespaceCode) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getNamespaceCode(java.lang.Class)
     */
    @Override
    public String getNamespaceCode(Class<?> documentOrStepClass) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.krad.service.KualiModuleService#getComponentCode(java.lang.Class)
     */
    @Override
    public String getComponentCode(Class<?> documentOrStepClass) {
        throw new UnsupportedOperationException();
    }

}
