/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.engine.node.var.schemes;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathVariableResolver;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.BranchService;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.var.Property;
import edu.iu.uis.eden.engine.node.var.PropertyScheme;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * A PropertyScheme that resolves the Property by evaluating it as an XPath expression.
 * DocumentRouteHeaderValue variables are set on the XPath instance so they are accessible.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class XPathScheme implements PropertyScheme {
    private static final Logger LOG = Logger.getLogger(XPathScheme.class);

    public String getName() {
        return "xpath";
    }

    public String getShortName() {
        return "xpath";
    }

    public Object load(Property property, final RouteContext context) {
        XPath xpath = XPathHelper.newXPath();
        final BranchService branchService = KEWServiceLocator.getBranchService();
        xpath.setXPathVariableResolver(new XPathVariableResolver() {
            public Object resolveVariable(QName name) {
                LOG.debug("Resolving XPath variable: " + name);
                String value = branchService.getScopedVariableValue(context.getNodeInstance().getBranch(), BranchState.VARIABLE_PREFIX + name.getLocalPart());
                LOG.debug("Resolved XPath variable " + name + " to " + value);
                return value;
            }
        });
        try {
            String docContent = context.getDocument().getDocContent();
            LOG.debug("Executing xpath expression '" + property.locator + "' in doc '" + docContent + "'");
            return xpath.evaluate(property.locator, new InputSource(new StringReader(docContent)), XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            throw new RuntimeException("Error evaluating xpath expression '" + property.locator + "'", xpee);
        }
    }
}