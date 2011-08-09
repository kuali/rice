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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.namespace.Namespace;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.framework.parameter.ParameterConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.ModuleServiceNotFoundException;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

public class KualiModuleServiceImpl implements KualiModuleService, InitializingBean, ApplicationContextAware {

    private List<ModuleService> installedModuleServices = new ArrayList<ModuleService>();
    private boolean loadRiceInstalledModuleServices;
    private ApplicationContext applicationContext;
    
    /**
	 * @param applicationContext the applicationContext to set
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public List<ModuleService> getInstalledModuleServices() {
        return installedModuleServices;
    }

    @Override
	public ModuleService getModuleService(String moduleId) {
        for (ModuleService moduleService : installedModuleServices) {
            if ( moduleService.getModuleConfiguration().getNamespaceCode().equals( moduleId ) ) {
                return moduleService;
            }
        } 
        return null;
    }

    @Override
	public ModuleService getModuleServiceByNamespaceCode(String namespaceCode) {
        for (ModuleService moduleService : installedModuleServices) {
            if ( moduleService.getModuleConfiguration().getNamespaceCode().equals( namespaceCode ) ) {
                return moduleService;
            }
        } 
        return null;
    }

    @Override
	public boolean isModuleServiceInstalled(String namespaceCode) {
        for (ModuleService moduleService : installedModuleServices) {
            if ( moduleService.getModuleConfiguration().getNamespaceCode().equals( namespaceCode ) ) {
                return true;
            }
        } 
        return false;
    }

    @Override
	public ModuleService getResponsibleModuleService(Class boClass) {
    	if(boClass==null) {
			return null;
		}
    	for (ModuleService moduleService : installedModuleServices) {
    	    if ( moduleService.isResponsibleFor( boClass ) ) {
    	        return moduleService;
    	    }
    	}
    	//Throwing exception only for externalizable business objects
    	if(ExternalizableBusinessObject.class.isAssignableFrom(boClass)){
    	    String message;
    		if(!boClass.isInterface()) {
				message = "There is no responsible module for the externalized business object class: "+boClass;
			} else {
				message = "There is no responsible module for the externalized business object interface: "+boClass;
			}
    		throw new ModuleServiceNotFoundException(message);
    	} 
    	//Returning null for business objects other than externalizable to keep the framework backward compatible
    	return null;
    }

    @Override
	public ModuleService getResponsibleModuleServiceForJob(String jobName){
        for(ModuleService moduleService : installedModuleServices){
            if(moduleService.isResponsibleForJob(jobName)){
                return moduleService;
            }
        }
        return null;
    }
    
    @Override
	public void setInstalledModuleServices(List<ModuleService> installedModuleServices) {
        this.installedModuleServices = installedModuleServices;
    }

    @Override
	public List<String> getDataDictionaryPackages() {
        List<String> packages  = new ArrayList<String>();
        for ( ModuleService moduleService : installedModuleServices ) {
            if ( moduleService.getModuleConfiguration().getDataDictionaryPackages() != null ) {
                packages.addAll( moduleService.getModuleConfiguration().getDataDictionaryPackages() );
            }
        }
        return packages;
    }

    @Override
	public String getNamespaceName(final String namespaceCode){
    	Namespace parameterNamespace = CoreApiServiceLocator.getNamespaceService().getNamespace(namespaceCode);
    	return parameterNamespace==null ? "" : parameterNamespace.getName();
    }
    
	/**
	 * @param loadRiceInstalledModuleServices the loadRiceInstalledModuleServices to set
	 */
	public void setLoadRiceInstalledModuleServices(
			boolean loadRiceInstalledModuleServices) {
		this.loadRiceInstalledModuleServices = loadRiceInstalledModuleServices;
	}

	/***
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(loadRiceInstalledModuleServices){
			try {
				installedModuleServices.addAll(
						GlobalResourceLoader.<KualiModuleService>getService(KualiModuleService.class.getSimpleName().substring(0, 1).toLowerCase() + KualiModuleService.class.getSimpleName().substring(1)).getInstalledModuleServices());
			} catch ( NoSuchBeanDefinitionException ex ) {
				installedModuleServices.addAll( ((KualiModuleService)applicationContext.getBean( KRADServiceLocatorWeb.KUALI_MODULE_SERVICE )).getInstalledModuleServices() );
			}
		}
	}

    @Override
    public String getNamespaceCode(Class<?> documentOrStepClass) {
        if (documentOrStepClass == null) {
            throw new IllegalArgumentException("documentOrStepClass must not be null");
        }

        if (documentOrStepClass.isAnnotationPresent(ParameterConstants.NAMESPACE.class)) {
            return (documentOrStepClass.getAnnotation(ParameterConstants.NAMESPACE.class)).namespace();
        }
        ModuleService moduleService = getResponsibleModuleService(documentOrStepClass);
        if (moduleService != null) {
            return moduleService.getModuleConfiguration().getNamespaceCode();
        }
        if (documentOrStepClass.getName().startsWith("org.kuali.rice.krad")) {
            return KRADConstants.KRAD_NAMESPACE;
        }
        if (documentOrStepClass.getName().startsWith("org.kuali.rice.kew")) {
            return "KR-WKFLW";
        }
        if (documentOrStepClass.getName().startsWith("org.kuali.rice.kim")) {
            return "KR-IDM";
        }
        if (documentOrStepClass.getName().startsWith("org.kuali.rice.core")) {
            return "KR-CORE";
        }
        throw new IllegalArgumentException("Unable to determine the namespace code for documentOrStepClass " + documentOrStepClass.getName());
    }

    @Override
    public String getComponentCode(Class<?> documentOrStepClass) {
        if (documentOrStepClass == null) {
            throw new IllegalArgumentException("documentOrStepClass must not be null");
        }

        if (documentOrStepClass.isAnnotationPresent(ParameterConstants.COMPONENT.class)) {
            return documentOrStepClass.getAnnotation(ParameterConstants.COMPONENT.class).component();
        } else if (TransactionalDocument.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName().replace("Document", "");
        } else if (BusinessObject.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName();
        } else {
            if (STEP_CLASS != null && STEP_CLASS.isAssignableFrom(documentOrStepClass)) {
                return documentOrStepClass.getSimpleName();
            }
        }
        throw new IllegalArgumentException("Unable to determine the component code for documentOrStepClass " + documentOrStepClass.getName());
    }

    private static final Class<?> STEP_CLASS;
    static {
        Class<?> clazz;
        try {
            ClassLoader cl = ClassLoaderUtils.getDefaultClassLoader();
            // TODO: Warning!  Kludge!  Hack!  Will be replaced!  KULRICE-2921
            clazz =  Class.forName("org.kuali.kfs.sys.batch.Step", true, cl);
        } catch (Exception e) {
            //swallowing: really what do we do here?  This is basically asking - are we on kfs?
            clazz = null;
        }
        STEP_CLASS = clazz;
    }

}

