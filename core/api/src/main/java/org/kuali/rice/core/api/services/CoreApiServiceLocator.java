package org.kuali.rice.core.api.services;

import org.kuali.rice.core.api.impex.xml.XmlExporterService;
import org.kuali.rice.core.api.impex.xml.XmlIngesterService;
import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

public class CoreApiServiceLocator {

	public static final String NAMESPACE_SERVICE = "namespaceService";
	public static final String XML_EXPORTER_SERVICE = "xmlExporterService";
	public static final String XML_INGESTER_SERVICE = "xmlIngesterService";
	
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
    
    
    
     
}
