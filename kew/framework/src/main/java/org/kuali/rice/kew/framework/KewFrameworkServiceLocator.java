/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.framework;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomizationHandlerService;
import org.kuali.rice.kew.framework.document.security.DocumentSecurityHandlerService;
import org.kuali.rice.kew.framework.validation.RuleValidationAttributeExporterService;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;

import javax.xml.namespace.QName;

/**
 * A static service locator which aids in locating the various KEW framework services.
 */
public class KewFrameworkServiceLocator {

    public static final String DOCUMENT_LOOKUP_CUSTOMIZATION_HANDLER_SERVICE = "documentLookupCustomizationHandlerServiceSoap";
    public static final String DOCUMENT_SECURITY_HANDLER_SERVICE = "documentSecurityHandlerServiceSoap";
    public static final String RULE_VALIDATION_ATTRIBUTE_EXPORTER_SERVICE = "ruleValidationAttributeExporterServiceSoap";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    static <T> T getServiceOnBus(String serviceName, String applicationId) {
        return (T)KsbApiServiceLocator.getServiceBus().getService(new QName(KewApiConstants.Namespaces.KEW_NAMESPACE_2_0, serviceName), applicationId);
    }

    public static DocumentLookupCustomizationHandlerService getDocumentLookupCustomizationHandlerService() {
        return getDocumentLookupCustomizationHandlerService(null);
    }

    public static DocumentLookupCustomizationHandlerService getDocumentLookupCustomizationHandlerService(
            String applicationId) {
        return getServiceOnBus(DOCUMENT_LOOKUP_CUSTOMIZATION_HANDLER_SERVICE, applicationId);
    }

    public static DocumentSecurityHandlerService getDocumentSecurityHandlerService() {
        return getDocumentSecurityHandlerService(null);
    }

    public static DocumentSecurityHandlerService getDocumentSecurityHandlerService(
            String applicationId) {
        return getServiceOnBus(DOCUMENT_SECURITY_HANDLER_SERVICE, applicationId);
    }

    public static RuleValidationAttributeExporterService getRuleValidationAttributeExporterService() {
        return getRuleValidationAttributeExporterService(null);
    }

    public static RuleValidationAttributeExporterService getRuleValidationAttributeExporterService(String applicationId) {
        return getServiceOnBus(RULE_VALIDATION_ATTRIBUTE_EXPORTER_SERVICE, applicationId);
    }
}