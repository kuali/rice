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
package edu.iu.uis.eden.test.web.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Utility class
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class Util {
    private static final Logger LOG = Logger.getLogger(Util.class);

    private Util() {}

    // could DOM be any less usable...
    public static String getAttribute(Node node, String name) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) return null;
        Node attrNode = attributes.getNamedItem(name);
        if (attrNode == null) return null;
        return attrNode.getNodeValue();
    }

    private static String composeAttrName(String name, String type) {
        if (name != null) {
            return name + "-" + type;
        } else {
            return type;
        }
    }

    public static String getAsString(Object o, String name) {
        if (!(o instanceof String)) {
            LOG.warn(name + " is not a string, coercing...");
            if (o == null) {
                return "";
            } else {
                return o.toString();
            }
        } else {
            return (String) o;
        }
    }

    /**
     * Looks up a resolvable Property attribute from the given Element node.  Explicit variants of the attribute
     * name are inspected first, and then the plain name itself is inspected and a defaultScheme is used if none
     * is specified in the value. e.g.:
     * <someelement value-literal="foo" value-resource="foo" value-url="foo" value-variable="foo" value="foo"/>
     * The following code would look for exactly one of the above "value" attributes.  If only the "value" attribute
     * was found, then the defaultScheme would be used to qualify the value if the value did not already contain
     * a scheme.
     * If more than one variant is present, a RuntimeException is thrown.
     * @param node the element whose attributes to check
     * @param name the name of the attribute
     * @param defaultScheme the defaultScheme to qualify an unqualified value found in the attribute with the plain name
     * @return a Property if found, or null
     * @throws RuntimeException if more than one variant is found (or more than one variant of a given scheme, e.g. value-lit/value-literal)
     */
    public static Property getResolvableAttribute(Node node, String name, PropertyScheme defaultScheme) {
        NamedNodeMap allAttributes = node.getAttributes();
        if (allAttributes == null) return null;

        Iterator schemes = PropertyScheme.SCHEMES.iterator();
        Map properties = new HashMap();
        while (schemes.hasNext()) {
            PropertyScheme scheme = (PropertyScheme) schemes.next();
            String[] names = new String[] { scheme.getName(), scheme.getShortName() };
            for (int i = 0; i < names.length; i++) {
                String attrName = composeAttrName(name, names[i]);
                //LOG.info("looking for attribute: " + attrName);
                Node attrNode = allAttributes.getNamedItem(attrName);
                if (attrNode != null) {
                    //LOG.info("found attribute: " + attrNode);
                    if (properties.containsKey(scheme)) {
                        throw new RuntimeException("Already specified explicit attribute for scheme: " + scheme);
                    } else {
                        properties.put(scheme, new Property(names[i], attrNode.getNodeValue()));
                    }
                }
            }
        }

        if (name != null) {
            Node attrNode = allAttributes.getNamedItem(name);
            if (attrNode != null) {
                Property property = new Property(attrNode.getNodeValue());
                if (property.scheme == null && defaultScheme != null) {
                    property.scheme = defaultScheme.getName();
                }
                properties.put(defaultScheme, property);
            }
        }

        if (properties.size() > 1) {
            throw new RuntimeException("Mutually exclusive explicit attributes present");
        } else if (properties.size() == 0) {
            return null;
        }

        // there can only be 1 at this point 
        return (Property) properties.values().iterator().next();
    }

    public static String getContent(Node node) {
        Node textNode = node.getFirstChild();
        if (textNode != null && textNode.getNodeType() == Node.TEXT_NODE) {
            return textNode.getNodeValue();
        } else {
            return null;
        }
    }

    public static String readResource(InputStream stream) throws IOException {
        StringBuffer sb = new StringBuffer(2048);
        InputStreamReader reader = new InputStreamReader(stream);
        char[] buf = new char[1024];
        int read;
        try {
            while ((read = reader.read(buf)) != -1) {
                sb.append(buf, 0, read);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }
}