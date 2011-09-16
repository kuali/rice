package org.kuali.rice.kim.impl.services;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;

import javax.xml.namespace.QName;

public class KIMServiceLocatorInternal {
    private static final Logger LOG = Logger.getLogger(KIMServiceLocatorInternal.class);

    public static final String KIM_RUN_MODE_PROPERTY = "kim.mode";
    public static final String RESPONSIBILITY_INTERNAL_SERVICE = "responsibilityInternalService";

    public static Object getService(String serviceName) {
        return getBean(serviceName);
    }

    public static Object getBean(String serviceName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetching service " + serviceName);
        }
        return GlobalResourceLoader.getResourceLoader().getService(
                (RunMode.REMOTE.equals(RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KIM_RUN_MODE_PROPERTY)))) ?
                        new QName(KimConstants.KIM_MODULE_NAMESPACE, serviceName) : new QName(serviceName));
    }

    public static ResponsibilityInternalService getResponsibilityInternalService() {
        return (ResponsibilityInternalService) KIMServiceLocatorInternal.getService(RESPONSIBILITY_INTERNAL_SERVICE);
    }
}
