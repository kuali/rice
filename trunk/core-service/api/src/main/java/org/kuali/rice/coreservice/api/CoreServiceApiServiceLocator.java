/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.rice.coreservice.api.style.StyleRepositoryService;
import org.kuali.rice.coreservice.api.style.StyleService;

import javax.xml.namespace.QName;

public class CoreServiceApiServiceLocator {

	public static final QName NAMESPACE_SERVICE = buildName("namespaceService");
    public static final QName PARAMETER_REPOSITORY_SERVICE = buildName("parameterRepositoryService");
    public static final QName COMPONENT_SERVICE = buildName("componentService");
    public static final QName STYLE_REPOSITORY_SERVICE = buildName("styleRepositoryService");

    public static final String STYLE_SERVICE = "styleService";

    private static QName buildName(String serviceName) {
        return new QName(CoreConstants.Namespaces.CORE_NAMESPACE_2_0, serviceName);
    }

    static <T> T getService(QName serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    static <T> T getService(String serviceName) {
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

    public static StyleService getStyleService() {
    	return getService(STYLE_SERVICE);
    }

    public static StyleRepositoryService getStyleRepositoryService() {
        return getService(STYLE_REPOSITORY_SERVICE);
    }
    
}
