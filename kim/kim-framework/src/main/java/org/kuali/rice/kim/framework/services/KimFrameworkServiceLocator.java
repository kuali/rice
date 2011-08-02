package org.kuali.rice.kim.framework.services;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeUtils;
import org.kuali.rice.kim.framework.type.KimTypeService;

import javax.xml.namespace.QName;

public class KimFrameworkServiceLocator {
    private static final Logger LOG = Logger.getLogger(KimFrameworkServiceLocator.class);

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    	/**
	 * Fetches the KimTypeService for the given KimType  If the kimType passed in is null
	 * then this method will return null.  This method will resolve the kimTypeServiceName
	 * on the given KimType and then delegate to {@link #getKimTypeService(javax.xml.namespace.QName)}.
	 */
	public static KimTypeService getKimTypeService(KimType kimType) {
		if( kimType == null ) {
            throw new IllegalArgumentException("Invalid service name passed, value was null.");
		}
		return getKimTypeService(KimTypeUtils.resolveKimTypeServiceName(kimType.getServiceName()));
	}

	/**
	 * Fetches the KimTypeService for the given kim type service name.  If the given {@link javax.xml.namespace.QName}
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
}
