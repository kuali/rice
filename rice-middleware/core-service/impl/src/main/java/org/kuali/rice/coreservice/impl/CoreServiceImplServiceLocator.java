/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.coreservice.impl;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.coreservice.impl.style.StyleXmlParser;
import org.springframework.cache.CacheManager;

public class CoreServiceImplServiceLocator {

	public static final String STYLE_XML_LOADER = "styleXmlLoader";
	public static final String STYLE_XML_EXPORTER = "styleXmlExporter";
    public static final String LOCAL_CACHE_MANAGER = "coreServiceLocalCacheManager";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static StyleXmlParser getStyleXmlLoader() {
        return getService(STYLE_XML_LOADER);
    }
        
    public static XmlExporter getStyleXmlExporter() {
        return getService(STYLE_XML_EXPORTER);
    }

    public static CacheManager getLocalCacheManager() {
        return getService(LOCAL_CACHE_MANAGER);
    }

}
