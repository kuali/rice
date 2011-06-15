package org.kuali.rice.core.api;

import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.impex.xml.XmlExporterService;
import org.kuali.rice.core.api.impex.xml.XmlIngesterService;
import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.style.StyleService;

public class CoreApiServiceLocator {

	public static final String NAMESPACE_SERVICE = "namespaceService";
	public static final String XML_EXPORTER_SERVICE = "xmlExporterService";
	public static final String XML_INGESTER_SERVICE = "xmlIngesterService";
	public static final String STYLE_SERVICE = "styleService";
	
    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static NamespaceService getNamespaceService() {
        return getService(NAMESPACE_SERVICE);
    }
    
    public static XmlExporterService getXmlExporterService() {
        return getService(XML_EXPORTER_SERVICE);
    }
    
    public static XmlIngesterService getXmlIngesterService() {
        return getService(XML_INGESTER_SERVICE);
    }
    
    public static final EncryptionService getEncryptionService() {
        return getService(CoreConstants.Services.ENCRYPTION_SERVICE);
    }
    
    public static StyleService getStyleService() {
    	return getService(STYLE_SERVICE);
    }

	public static DateTimeService getDateTimeService() {
	    return getService(CoreConstants.Services.DATETIME_SERVICE);
	}
}
