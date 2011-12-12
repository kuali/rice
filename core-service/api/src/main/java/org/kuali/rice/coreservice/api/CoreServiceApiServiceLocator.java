/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.coreservice.api;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.impex.xml.XmlExporterService;
import org.kuali.rice.core.api.impex.xml.XmlIngesterService;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.style.StyleService;

import javax.xml.namespace.QName;

public class CoreServiceApiServiceLocator {

	public static final String NAMESPACE_SERVICE = "namespaceService";


    public static final QName PARAMETER_REPOSITORY_SERVICE = new QName(CoreConstants.Namespaces.CORE_NAMESPACE_2_0, "parameterRepositoryService");
    public static final QName COMPONENT_SERVICE = new QName(CoreConstants.Namespaces.CORE_NAMESPACE_2_0, "componentService");
	
    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    static <T> T getService(QName serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static NamespaceService getNamespaceService() {
        return getService(NAMESPACE_SERVICE);
    }

    public static ParameterRepositoryService getParameterRepositoryService() {
        return getService(PARAMETER_REPOSITORY_SERVICE);
    }

    public static ComponentService getComponentService() {
        return getService(COMPONENT_SERVICE);
    }
    
}
