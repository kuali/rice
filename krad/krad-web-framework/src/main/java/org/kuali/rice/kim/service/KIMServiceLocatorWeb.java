package org.kuali.rice.kim.service;

import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.RunMode;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.KimType;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;

import javax.xml.namespace.QName;

public class KIMServiceLocatorWeb {
    private static final Logger LOG = Logger.getLogger(KIMServiceLocatorWeb.class);

    public static final String KIM_RUN_MODE_PROPERTY = "kim.mode";
    public static final String KIM_TYPE_INFO_SERVICE = "kimTypeInfoService";

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

	/**
	 * Fetches the KimTypeService for the given KimType  If the kimType passed in is null
	 * then this method will return null.  This method will resolve the kimTypeServiceName
	 * on the given KimType and then delegate to {@link #getKimTypeService(QName)}.
	 */
	public static KimTypeService getKimTypeService(KimType kimType) {
		if( kimType == null ) {
			LOG.warn( "null KimType passed into getKimTypeService" );
			return null;
		}
		return getKimTypeService(KimCommonUtils.resolveKimTypeServiceName(kimType.getKimTypeServiceName()));
	}

	/**
	 * Fetches the KimTypeService for the given kim type service name.  If the given {@link QName}
	 * is null, then this method will throw an IllegalArgumentException.
	 *
	 * @throws IllegalArgumentException if the given kimTypeServiceName is null
	 */
	public static KimTypeService getKimTypeService(QName kimTypeServiceName) {
		if (kimTypeServiceName == null) {
			throw new IllegalArgumentException("Invalid service name passed, value was null.");
		}
		try {
			return (KimTypeService) GlobalResourceLoader.getService(kimTypeServiceName);
		} catch (Exception exception) {

			// if we get an exception loading the remote KimTypeService, then instead of completly failing,
			// we'll handle the exception and return a null reference to the service

			LOG.error("Unable to find KIM type service with name: " + kimTypeServiceName, exception);
			return null;
		}
	}

    public static KimTypeInfoService getTypeInfoService() {
        return (KimTypeInfoService) getService(KIM_TYPE_INFO_SERVICE);
    }
}
