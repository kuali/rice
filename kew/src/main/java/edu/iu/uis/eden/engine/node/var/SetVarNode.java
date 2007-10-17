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
package edu.iu.uis.eden.engine.node.var;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.PropertiesUtil;
import edu.iu.uis.eden.engine.node.SimpleNode;
import edu.iu.uis.eden.engine.node.SimpleResult;
import edu.iu.uis.eden.exception.InvalidXmlException;

/**
 * A simple node that allows setting of document variables.
 * The definition of SetVarNode takes the following config parameter elements:
 * <dl>
 *   <dt>name</dt>
 *   <dd>The name of the variable to set</dd>
 *   <dt>value</dt>
 *   <dd>The value to which to set the variable.  This value is parsed according to
 *       Property/PropertyScheme syntax</dd>
 * </dl>
 * The default PropertySchme is LiteralScheme, which will evaluate the value simply
 * as a literal (won't do anything but return it)
 * @see PropertyScheme
 * @see Property
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SetVarNode implements SimpleNode {
    private static final Logger LOG = Logger.getLogger(SetVarNode.class);

    public SimpleResult process(RouteContext context, RouteHelper helper) throws Exception {
        LOG.debug("processing");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String contentFragment = context.getNodeInstance().getRouteNode().getContentFragment();
        LOG.debug("contentFragment=" + contentFragment);
        Document d = db.parse(new InputSource(new StringReader(contentFragment)));
        Element e = d.getDocumentElement();
        String name = e.getElementsByTagName("name").item(0).getTextContent();
        // FIXME: validation should be checked in documenttypexmlparser to start with
        if (name == null) {
            throw new InvalidXmlException("SetVar node required 'name' attribute to be specified");
        }
        String valueRef = e.getElementsByTagName("value").item(0).getTextContent();
        Object retrievedVal = PropertiesUtil.retrieveProperty(valueRef, PropertyScheme.LITERAL_SCHEME, context);
        LOG.debug("retrieved value '" + retrievedVal + " for value '" + valueRef);
        // hack to make variable value fit in column size
        // need to just alter that column
        String stringVal = null;
        if (retrievedVal != null) {
            stringVal = retrievedVal.toString();
            if (stringVal.length() > 255) {
                stringVal = stringVal.substring(0, 255);
            }
        }
        LOG.debug("setting value '" + stringVal + "' for variable " + name);
        KEWServiceLocator.getBranchService().setScopedVariableValue(context.getNodeInstance().getBranch(), BranchState.VARIABLE_PREFIX + name, stringVal);

        return new SimpleResult(true);
    }

}