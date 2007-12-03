/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.test.web.framework.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.Script;
import edu.iu.uis.eden.test.web.framework.Util;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * ScriptAction that sets request parameters
 * <pre>
 * &lt;parameters [ retain="true|false" ]&gt;
 *   &lt;param [ overwrite="true|false" ] ( value="..." | variable="..." | literal="..." | resource="..." | url="..." )/&gt;
 *   &lt;param [ overwrite="true|false" ]&gt;...&lt;/param&gt;
 * &lt;/parameters&gt;
 * <pre>
 * The 'value' attribute of param element is resolved via {@link edu.iu.uis.eden.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)},
 * defaulting to literal scheme.
 * If a value variant is not present, the algorithm proceeds again without a specific prefix (i.e., just looks for variable, literal, etc.).
 * If no attribute is found, element content is used.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ParametersAction extends BaseScriptAction {
    private static final String[] NAMES = { "parameters" };

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        Map parameters = script.getState().getRequest();

        if (!"true".equals(Util.getAttribute(node, "retain"))) {
            log.info("Clearing parameters");
            parameters.clear();
        }

        log.info("Setting parameters");
        NodeList params = node.getChildNodes();
        for (int j = 0; j < params.getLength(); j++) {
            Node param = params.item(j);
            if (param.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!"param".equals(param.getNodeName())) {
                log.error("Invalid element: " + param);
                continue;
            }
            String pname = Util.getAttribute(param, "name");
            if (pname == null) {
                log.error("Nameless parameter: " + param);
                continue;
            }

            Object pvalue = null;
            /* first try looking for the attribute "value" */
            Property property = Util.getResolvableAttribute(param, "value", PropertyScheme.LITERAL_SCHEME);
            /* if not found, fall back to looking for any of the property scheme types: literal, variable, resource, url, etc. */
            property = Util.getResolvableAttribute(param, null, null);
            if (property != null) {
                log.info("Obtaining value for parameter '" + pname + "' from variable '" + property + "'");
                pvalue = script.getState().retrieveProperty(property);
            } else {
                log.info("Obtaining value for parameter '" + pname + "' from node content");
                pvalue = Util.getContent(param);
            }
            if (pvalue == null) {
                log.warn("Node does not have any text content: " + XmlHelper.jotNode(param));
                log.info("Removing parameter '" + pname + "'");
                parameters.remove(pname);
            } else {
                String overwrite = Util.getAttribute(param, "overwrite");
                boolean overwriting = Boolean.valueOf(overwrite).booleanValue();
                List l = (List) parameters.get(pname);
                if (l == null) {
                    l = new ArrayList();
                    parameters.put(pname, l);
                }
                if (overwriting) {
                    log.info("Overwriting parameter '" + pname + "' with value '" + pvalue + "'");
                    l.clear();
                } else {
                    log.info("Adding parameter '" + pname + "' with value '" + pvalue + "'");
                }
                if (pvalue instanceof Collection) {
                    l.addAll((Collection) pvalue);
                } else {
                    l.add(pvalue);
                }
            }
        }
    }
}