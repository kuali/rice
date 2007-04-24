/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.core.workflow;

import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.UrlFactory;
import org.kuali.rice.KNSServiceLocator;
import org.w3c.dom.Document;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.routetemplate.xmlrouting.WorkflowFunctionResolver;
import edu.iu.uis.eden.routetemplate.xmlrouting.WorkflowNamespaceContext;

public class WorkflowUtils {
    private static final String XPATH_ROUTE_CONTEXT_KEY = "_xpathKey";
    public static final String XSTREAM_SAFE_PREFIX = "wf:xstreamsafe('";
    public static final String XSTREAM_SAFE_SUFFIX = "')";
    public static final String XSTREAM_MATCH_ANYWHERE_PREFIX = "//";
    public static final String XSTREAM_MATCH_RELATIVE_PREFIX = "./";

    /**
     * 
     * This method sets up the XPath with the correct workflow namespace and resolver initialized. This ensures that the XPath
     * statements can use required workflow functions as part of the XPath statements.
     * 
     * @param document - document
     * @return a fully initialized XPath instance that has access to the workflow resolver and namespace.
     * 
     */
    public final static XPath getXPath(Document document) {
        XPath xpath = getXPath(RouteContext.getCurrentRouteContext());
        xpath.setNamespaceContext(new WorkflowNamespaceContext());
        WorkflowFunctionResolver resolver = new WorkflowFunctionResolver();
        resolver.setXpath(xpath);
        resolver.setRootNode(document);
        xpath.setXPathFunctionResolver(resolver);
        return xpath;
    }

    public final static XPath getXPath(RouteContext routeContext) {
        if (routeContext == null) {
            return XPathFactory.newInstance().newXPath();
        }
        if (!routeContext.getParameters().containsKey(XPATH_ROUTE_CONTEXT_KEY)) {
            routeContext.getParameters().put(XPATH_ROUTE_CONTEXT_KEY, XPathFactory.newInstance().newXPath());
        }
        return (XPath) routeContext.getParameters().get(XPATH_ROUTE_CONTEXT_KEY);
    }
    

    /**
     * This method is for use by WorkflowLookupableImpl and WorkflowAttribute implementations to derive the fieldHelpUrl for use on
     * edu.iu.uis.eden.lookupable.Fields.
     * 
     * @param field The kuali field that we need to derive a help url for. @ return Returns the help url for the field.
     */
    public static String getHelpUrl(org.kuali.core.web.ui.Field field) {
        Properties params = new Properties();
        params.put(Constants.DISPATCH_REQUEST_PARAMETER, "getAttributeHelpText");
        params.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, field.getBusinessObjectClassName());
        params.put(PropertyConstants.ATTRIBUTE_NAME, field.getPropertyName());
        return UrlFactory.parameterizeUrl(KNSServiceLocator.getKualiConfigurationService().getPropertyString(Constants.APPLICATION_URL_KEY) + "/help.do", params);
    }
}
