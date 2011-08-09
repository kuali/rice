package org.kuali.rice.krad.service.impl;

import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

import java.util.List;

/**
 * Module service that adds support for the kfs Step class.
 */
public class KFSModuleServiceImpl implements KualiModuleService {

    //inject with the rice service
    private KualiModuleService kualiModuleService;

    private static final Class<?> STEP_CLASS;
    static {
        Class<?> clazz;
        try {
            ClassLoader cl = ClassLoaderUtils.getDefaultClassLoader();
            //once this is pushed into kfs should not use class token directly
            clazz =  Class.forName("org.kuali.kfs.sys.batch.Step", true, cl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        STEP_CLASS = clazz;
    }


    @Override
    public List<ModuleService> getInstalledModuleServices() {
        return kualiModuleService.getInstalledModuleServices();
    }

    @Override
    public ModuleService getModuleService(String moduleId) {
        return kualiModuleService.getModuleService(moduleId);
    }

    @Override
    public ModuleService getModuleServiceByNamespaceCode(String namespaceCode) {
        return kualiModuleService.getModuleServiceByNamespaceCode(namespaceCode);
    }

    @Override
    public boolean isModuleServiceInstalled(String namespaceCode) {
        return kualiModuleService.isModuleServiceInstalled(namespaceCode);
    }

    @Override
    public ModuleService getResponsibleModuleService(Class boClass) {
        return kualiModuleService.getResponsibleModuleService(boClass);
    }

    @Override
    public ModuleService getResponsibleModuleServiceForJob(String jobName) {
        return kualiModuleService.getResponsibleModuleServiceForJob(jobName);
    }

    @Override
    public void setInstalledModuleServices(List<ModuleService> moduleServices) {
        kualiModuleService.setInstalledModuleServices(moduleServices);
    }

    @Override
    public List<String> getDataDictionaryPackages() {
        return kualiModuleService.getDataDictionaryPackages();
    }

    @Override
    public String getNamespaceName(String namespaceCode) {
        return kualiModuleService.getNamespaceName(namespaceCode);
    }

    @Override
    public String getNamespaceCode(Class<?> documentOrStepClass) {
        if (STEP_CLASS != null && STEP_CLASS.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName();
        }

        return kualiModuleService.getNamespaceCode(documentOrStepClass);
    }

    @Override
    public String getComponentCode(Class<?> documentOrStepClass) {
        return kualiModuleService.getComponentCode(documentOrStepClass);
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
