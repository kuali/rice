package org.kuali.rice.core.impl.services;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.core.impl.style.StyleXmlParser;

public class CoreImplServiceLocator {

	public static final String STYLE_XML_LOADER = "styleXmlLoader";
	public static final String STYLE_XML_EXPORTER = "styleXmlExporter";
	
    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static StyleXmlParser getStyleXmlLoader() {
        return getService(STYLE_XML_LOADER);
    }
        
    public static XmlExporter getStyleXmlExporter() {
        return getService(STYLE_XML_EXPORTER);
    }
    
}
