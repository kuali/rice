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
package org.kuali.rice.krad.datadictionary.parse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parser for parsing xml bean's created using the custom schema into normal spring bean format.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomSchemaParser extends AbstractSingleBeanDefinitionParser {
    private static final Log LOG = LogFactory.getLog(CustomSchemaParser.class);

    /**
     * Retrieves the class of the bean defined by the xml element.
     *
     * @param bean - The xml element for the bean being parsed.
     * @return The class associated with the provided tag
     */
    protected Class getBeanClass(Element bean) {
        Map<String, BeanTagInfo> beanType = null;

        // Attempt to load the list of tags
        try {
            beanType = CustomTagAnnotations.getBeanTags();
        } catch (Exception e) {
            LOG.error("Error retrieving bean tag information", e);
        }

        Class<?> beanTag = null;
        try {
            // Retrieve the connected class in the tag map using the xml tag's name.

            beanTag = beanType.get(bean.getLocalName()).getBeanClass();
        } catch (Exception e) {
            LOG.error("Error in retrieved bean tag information", e);
        }

        return beanTag;
    }

    /**
     * Parses the xml bean into a standard bean definition format and fills the information in the passed in definition
     * builder
     *
     * @param element - The xml bean being parsed.
     * @param parserContext - Provided information and functionality regarding current bean set.
     * @param bean - A definition builder used to build a new spring bean from the information it is filled with.
     */
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {
        // Retrieve custom schema information build from the annotations
        Map<String, Map<String, BeanTagAttributeInfo>> attributeProperties =
                CustomTagAnnotations.getAttributeProperties();
        Map<String, BeanTagAttributeInfo> entries = attributeProperties.get(element.getLocalName());

        // Log error if there are no attributes found for the bean tag
        if (entries == null) {
            LOG.error("Bean Tag not found " + element.getLocalName());
        }

        // Retrieve the information for the new bean tag and fill in the default parent if needed
        BeanTagInfo tagInfo = CustomTagAnnotations.getBeanTags().get(element.getLocalName());
        if (tagInfo.getParent().compareTo("none") != 0) {
            bean.setParentName(tagInfo.getParent());
        }

        // Create the map for the attributes found in the tag and process them in to the definition builder.
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            processSingleValue(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue(), entries, bean);
        }

        ArrayList<Element> children = (ArrayList<Element>) DomUtils.getChildElements(element);

        // Process the children found in the xml tag
        for (int i = 0; i < children.size(); i++) {
            String tag = children.get(i).getLocalName();
            BeanTagAttributeInfo info = entries.get(tag);

            String propertyName;
            BeanTagAttribute.AttributeType type;

            // Sets the property name to be used when adding the property value
            if (info == null) {
                // If the tag is not in the schema map let spring handle the value by forwarding the tag as the
                // propertyName
                propertyName = tag;
                int temp = DomUtils.getChildElementsByTagName(children.get(i), "value").size();

                if (temp > 0) {
                    type = BeanTagAttribute.AttributeType.LISTVALUE;
                } else {
                    type = BeanTagAttribute.AttributeType.SINGLEBEAN;
                }
            } else {
                // If the tag is found in the schema map use the connected name stored in the attribute information
                propertyName = info.getName();
                type = info.getType();
            }
            // Process the information stored in the child bean
            ArrayList<Element> grandChildren = (ArrayList<Element>) DomUtils.getChildElements(children.get(i));

            if (type == BeanTagAttribute.AttributeType.SINGLEBEAN) {
                processSingleBean(propertyName, grandChildren.get(0), parserContext, bean);
            } else if (type == BeanTagAttribute.AttributeType.LISTBEAN) {
                processListBean(propertyName, grandChildren, parserContext, bean);
            } else if (type == BeanTagAttribute.AttributeType.LISTVALUE) {
                processListValue(propertyName, grandChildren, bean);
            } else if (type == BeanTagAttribute.AttributeType.MAPVALUE) {
                processMapValue(propertyName, grandChildren, bean);
            } else if (type == BeanTagAttribute.AttributeType.MAPBEAN) {
                processMapBean(propertyName, grandChildren, parserContext, bean);
            } else if (type == BeanTagAttribute.AttributeType.SETVALUE) {
                processSetValue(propertyName, grandChildren, bean);
            } else if (type == BeanTagAttribute.AttributeType.SETBEAN) {
                processSetBean(propertyName, grandChildren, parserContext, bean);
            }
        }
    }

    /**
     * Adds the property value to the bean definition based on the name and value of the attribute.
     *
     * @param name - The name of the attribute.
     * @param value - The value of the attribute.
     * @param entries - The property entries for the over all tag.
     * @param bean - The bean definition being created.
     */
    private void processSingleValue(String name, String value, Map<String, BeanTagAttributeInfo> entries,
            BeanDefinitionBuilder bean) {

        if (name.toLowerCase().compareTo("parent") == 0) {
            // If attribute is defining the parent set it in the bean builder.
            bean.setParentName(value);
        } else if (name.toLowerCase().compareTo("abstract") == 0) {
            // If the attribute is defining the parent as  abstract set it in the bean builder.
            bean.setAbstract(Boolean.valueOf(value));
        } else if (name.toLowerCase().compareTo("id") == 0) {
            //nothing - insures that its erased
        } else {
            // If the attribute is not a reserved case find the property name form the connected map and add the new
            // property value.
            BeanTagAttributeInfo info = entries.get(name);
            String propertyName;

            if (info == null) {
                propertyName = name;
            } else {
                propertyName = info.getName();
            }

            bean.addPropertyValue(propertyName, value);
        }
    }

    /**
     * Process the child of a sub tag. Since single entries containing none bean information should have been created
     * as
     * attributes it is assumed that the child being processed is a bean.
     *
     * @param propertyName - Name of the property the bean is added as.
     * @param grandChild - The Xml bean being process (Is the grand child of the bean tag being parsed).
     * @param parserContext - Provided information and functionality regarding current bean set.
     * @param bean - The bean definition being created.
     */
    private void processSingleBean(String propertyName, Element grandChild, ParserContext parserContext,
            BeanDefinitionBuilder bean) {

        // Check if the tag is a Spring bean or custom schema based tag by looking at the namespace definition it.
        if (grandChild.getNamespaceURI().compareTo("http://www.springframework.org/schema/beans") == 0) {
            // check if the tag is a reference to another bean
            if (grandChild.getLocalName().compareTo("ref") == 0) {
                // Create the referenced bean by creating a new bean and setting its parent to the referenced bean then
                // replace grand child with it
                Element temp = grandChild.getOwnerDocument().createElement("bean");
                temp.setAttribute("parent", grandChild.getAttribute("bean"));
                grandChild = temp;
            }

            // Create the bean definition for the grandchild and add it as a property value.
            BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
            bean.addPropertyValue(propertyName, bean2.getBeanDefinition());
        } else {
            if (grandChild.getLocalName().compareTo("ref") == 0) {
                // Create the referenced bean by creating a new bean and setting its parent to the referenced bean then
                // replace grand child with it
                Element temp = grandChild.getOwnerDocument().createElement("bean");
                temp.setAttribute("parent", grandChild.getAttribute("bean"));
                grandChild = temp;
                BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                bean.addPropertyValue(propertyName, bean2.getBeanDefinition());
                return;
            }
            // Create the bean definition for the grandchild and add it as a property value.
            BeanDefinition bean2 = parserContext.getDelegate().parseCustomElement(grandChild, bean.getBeanDefinition());
            bean.addPropertyValue(propertyName, bean2);
        }
    }

    /**
     * Process the children of a sub tag into a list.  All tags are assumed to be value tags.
     *
     * @param propertyName - The name of the property.
     * @param grandChildren - The children of the property that are being processed.
     * @param bean - The bean definition being created.
     */
    private void processListValue(String propertyName, ArrayList<Element> grandChildren, BeanDefinitionBuilder bean) {
        // Process sub tag as a list of content information
        ArrayList<String> list = new ArrayList<String>();
        for (int j = 0; j < grandChildren.size(); j++) {
            list.add(grandChildren.get(j).getTextContent());
        }
        ManagedList beans = new ManagedList(list.size());
        beans.addAll(list);
        bean.addPropertyValue(propertyName, beans);
    }

    /**
     * Process the children of a sub tag into a list.
     *
     * @param propertyName - The name of the property.
     * @param grandChildren - The children of the property that are being processed.
     * @param parserContext - Provided information and functionality regarding current bean set.
     * @param bean - The bean definition being created.
     */
    private void processListBean(String propertyName, ArrayList<Element> grandChildren, ParserContext parserContext,
            BeanDefinitionBuilder bean) {
        // Process sub tag as a list of sub bean definitions
        ArrayList<BeanDefinition> list = new ArrayList<BeanDefinition>();
        for (int j = 0; j < grandChildren.size(); j++) {
            Element grandChild = grandChildren.get(j);
            // Check if the tag is a Spring bean or custom schema based tag by looking at the namespace definition it.
            if (grandChild.getNamespaceURI().compareTo("http://www.springframework.org/schema/beans") == 0) {
                // check if the tag is a reference to another bean
                if (grandChild.getLocalName().compareTo("ref") == 0) {
                    // Create the referenced bean by creating a new bean and setting its parent to the referenced bean
                    // then replace grand child with it
                    Element temp = grandChild.getOwnerDocument().createElement("bean");
                    temp.setAttribute("parent", grandChild.getAttribute("bean"));
                    grandChild = temp;
                }
                // Create the bean definition for the grandchild and return it.
                BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                list.add(bean2.getBeanDefinition());
            } else {
                if (grandChild.getLocalName().compareTo("ref") == 0) {
                    // Create the referenced bean by creating a new bean and setting its parent to the referenced bean
                    // then replace grand child with it
                    Element temp = grandChild.getOwnerDocument().createElement("bean");
                    temp.setAttribute("parent", grandChild.getAttribute("bean"));
                    grandChild = temp;
                    BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                    list.add(bean2.getBeanDefinition());
                    continue;
                }
                // Create the bean definition for the grandchild and return it.
                BeanDefinition bean2 = parserContext.getDelegate().parseCustomElement(grandChild,
                        bean.getBeanDefinition());
                list.add(bean2);
            }
        }
        ManagedList beans = new ManagedList(list.size());
        beans.addAll(list);
        bean.addPropertyValue(propertyName, beans);
    }

    /**
     * Process the children of a sub tag into a map. Assumed the key is the tag name and the value is the text context.
     *
     * @param propertyName - The name of the property.
     * @param grandChildren - The children of the property that are being processed.
     * @param bean - The bean definition being created.
     */
    private void processMapValue(String propertyName, ArrayList<Element> grandChildren, BeanDefinitionBuilder bean) {
        Map<String, String> map = new HashMap<String, String>();
        for (int j = 0; j < grandChildren.size(); j++) {
            String name = grandChildren.get(j).getLocalName();
            String value = grandChildren.get(j).getTextContent();
            map.put(name, value);
        }
        bean.addPropertyValue(propertyName, map);
    }

    /**
     * Process the children of a sub tag into a map.
     *
     * @param propertyName - The name of the property.
     * @param grandChildren - The children of the property that are being processed.
     * @param parserContext - Provided information and functionality regarding current bean set.
     * @param bean - The bean definition being created.
     */
    private void processMapBean(String propertyName, ArrayList<Element> grandChildren, ParserContext parserContext,
            BeanDefinitionBuilder bean) {
        Map<String, BeanDefinition> map = new HashMap<String, BeanDefinition>();
        for (int j = 0; j < grandChildren.size(); j++) {
            String name = grandChildren.get(j).getLocalName();
            Element grandChild = grandChildren.get(j);
            if (grandChild.getNamespaceURI().compareTo("http://www.springframework.org/schema/beans") == 0) {
                // check if the tag is a reference to another bean
                if (grandChild.getLocalName().compareTo("ref") == 0) {
                    // Create the referenced bean by creating a new bean and setting its parent to the referenced bean
                    // then replace grand child with it
                    Element temp = grandChild.getOwnerDocument().createElement("bean");
                    temp.setAttribute("parent", grandChild.getAttribute("bean"));
                    grandChild = temp;
                }

                // Create the bean definition for the grandchild and add it as a property value.
                BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                map.put(name, bean2.getBeanDefinition());
            } else {
                if (grandChild.getLocalName().compareTo("ref") == 0) {
                    // Create the referenced bean by creating a new bean and setting its parent to the referenced bean
                    // then replace grand child with it
                    Element temp = grandChild.getOwnerDocument().createElement("bean");
                    temp.setAttribute("parent", grandChild.getAttribute("bean"));
                    grandChild = temp;
                    BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                    bean.addPropertyValue(propertyName, bean2.getBeanDefinition());
                    return;
                }
                // Create the bean definition for the grandchild and add it as a property value.
                BeanDefinition bean2 = parserContext.getDelegate().parseCustomElement(grandChild,
                        bean.getBeanDefinition());
                map.put(name, bean2);
            }
        }

        bean.addPropertyValue(propertyName, map);

    }

    /**
     * Process the children of a sub tag into a set. Assumed that the value is stored in the tags text context.
     *
     * @param propertyName - The name of the property.
     * @param grandChildren - The children of the property that are being processed.
     * @param bean - The bean definition being created.
     */
    private void processSetValue(String propertyName, ArrayList<Element> grandChildren, BeanDefinitionBuilder bean) {
        Set<String> set = new HashSet<String>();
        for (int j = 0; j < grandChildren.size(); j++) {
            String value = grandChildren.get(j).getTextContent();
            set.add(value);
        }
        bean.addPropertyValue(propertyName, set);
    }

    /**
     * Process the children of a sub tag into a set.
     *
     * @param propertyName - The name of the property.
     * @param grandChildren - The children of the property that are being processed.
     * @param parserContext - Provided information and functionality regarding current bean set.
     * @param bean - The bean definition being created.
     */
    private void processSetBean(String propertyName, ArrayList<Element> grandChildren, ParserContext parserContext,
            BeanDefinitionBuilder bean) {
        Set<BeanDefinition> set = new HashSet<BeanDefinition>();
        for (int j = 0; j < grandChildren.size(); j++) {
            Element grandChild = grandChildren.get(j);
            if (grandChild.getNamespaceURI().compareTo("http://www.springframework.org/schema/beans") == 0) {
                // check if the tag is a reference to another bean
                if (grandChild.getLocalName().compareTo("ref") == 0) {
                    // Create the referenced bean by creating a new bean and setting its parent to the referenced bean
                    // then replace grand child with it
                    Element temp = grandChild.getOwnerDocument().createElement("bean");
                    temp.setAttribute("parent", grandChild.getAttribute("bean"));
                    grandChild = temp;
                }

                // Create the bean definition for the grandchild and add it as a property value.
                BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                set.add(bean2.getBeanDefinition());
            } else {
                if (grandChild.getLocalName().compareTo("ref") == 0) {
                    // Create the referenced bean by creating a new bean and setting its parent to the referenced bean
                    // then replace grand child with it
                    Element temp = grandChild.getOwnerDocument().createElement("bean");
                    temp.setAttribute("parent", grandChild.getAttribute("bean"));
                    grandChild = temp;
                    BeanDefinitionHolder bean2 = parserContext.getDelegate().parseBeanDefinitionElement(grandChild);
                    bean.addPropertyValue(propertyName, bean2.getBeanDefinition());
                    return;
                }
                // Create the bean definition for the grandchild and add it as a property value.
                BeanDefinition bean2 = parserContext.getDelegate().parseCustomElement(grandChild,
                        bean.getBeanDefinition());
                set.add(bean2);
            }
        }
        bean.addPropertyValue(propertyName, set);
    }
}
