/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.test.web.framework.actions;

import org.kuali.rice.kew.test.web.framework.Property;
import org.kuali.rice.kew.test.web.framework.PropertyScheme;
import org.kuali.rice.kew.test.web.framework.Script;
import org.kuali.rice.kew.test.web.framework.Util;
import org.w3c.dom.Node;


/**
 * ScriptAction that echos a property or literal text to console
 * <pre>
 * &lt;echo [ value="..." | variable="..." | literal="..." | resource="..." | url="..." ] /&gt;
 * &lt;echo&gt;...&lt;/echo&gt;
 * </pre> 
 * The 'value' attribute is resolved via {@link org.kuali.rice.kew.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)},
 * defaulting to literal scheme.
 * If a value variant is not present, the algorithm proceeds again without a specific prefix (i.e., just looks for variable, literal, etc.).
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EchoAction extends BaseScriptAction {
    private static final String[] NAMES = { "echo" };

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        /* first try looking for the attribute "value" */
        Property property = Util.getResolvableAttribute(node, "value", PropertyScheme.LITERAL_SCHEME);
        /* if not found, fall back to looking for any of the property scheme types: literal, variable, resource, url, etc. */
        property = Util.getResolvableAttribute(node, null, null);
        if (property == null) {
            String text = Util.getContent(node);
            if (text == null) {
                //String message = "echo element has no text content: " + XmlHelper.jotNode(node);
                //log.error(message);
                //throw new RuntimeException(message);
                text = "";
            }
            log.info(text);
        } else {
            Object o = script.getState().retrieveProperty(property);
            if (o == null) {
                String message = "Could not load value property: " + property;
                log.error(message);
                throw new RuntimeException(message);
            } else {
                if (!PropertyScheme.LITERAL_SCHEME.getName().equals(property.scheme) &&
                    !PropertyScheme.LITERAL_SCHEME.getShortName().equals(property.scheme)) {
                    /* if it's not a literal, print out the locator as well */
                    log.info(property.locator + ": " + o);    
                } else {
                    log.info(o);
                }
                    
            }
        }
    }
}
