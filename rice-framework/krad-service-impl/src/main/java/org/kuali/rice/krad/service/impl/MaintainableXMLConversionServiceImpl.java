/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.service.MaintainableXMLConversionService;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintainableXMLConversionServiceImpl implements MaintainableXMLConversionService {

    private static final String CONVERSION_RULE_FILE_PARAMETER = "maintainable.conversion.rule.file";
    private static final String SERIALIZATION_ATTRIBUTE = "serialization";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String MAINTENANCE_ACTION_ELEMENT_NAME = "maintenanceAction";

    private Map<String, String> classNameRuleMap;
    private Map<String, Map<String, String>> classPropertyRuleMap;
    private String conversionRuleFile;

    public MaintainableXMLConversionServiceImpl() {
        String conversionRuleFile = ConfigContext.getCurrentContextConfig().getProperty(CONVERSION_RULE_FILE_PARAMETER);
        this.setConversionRuleFile(conversionRuleFile);
    }

    @Override
    public String transformMaintainableXML(String xml) {
        String maintenanceAction = "<" + MAINTENANCE_ACTION_ELEMENT_NAME + ">" + StringUtils.substringAfter("<" + MAINTENANCE_ACTION_ELEMENT_NAME + ">", xml);
        xml = StringUtils.substringBefore(xml, "<" + MAINTENANCE_ACTION_ELEMENT_NAME + ">");
        if(StringUtils.isNotBlank(this.getConversionRuleFile())) {
            try {
                this.setRuleMaps();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(new InputSource(new StringReader(xml)));
                for(Node childNode = document.getFirstChild(); childNode != null;) {
                    Node nextChild = childNode.getNextSibling();
                    transformClassNode(document, childNode);
                    childNode = nextChild;
                }
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer trans = transFactory.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                trans.setOutputProperty(OutputKeys.INDENT, "yes");

                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                DOMSource source = new DOMSource(document);
                trans.transform(source, result);
                xml = writer.toString().replaceAll("(?m)^\\s+\\n", "");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        if(StringUtils.contains(xml, "edu.iu.uis.dp.bo.DataManager") || StringUtils.contains(xml, "edu.iu.uis.dp.bo.DataSteward")){
            xml = StringUtils.replace(xml, "org.kuali.rice.kim.bo.impl.PersonImpl", "org.kuali.rice.kim.impl.identity.PersonImpl");
            xml = xml.replaceAll("<autoIncrementSet.+", "");
            xml = xml.replaceAll("<address.+","");
        }
        return xml + maintenanceAction;
    }

    public String getConversionRuleFile() {
        return conversionRuleFile;
    }

    public void setConversionRuleFile(String conversionRuleFile) {
        this.conversionRuleFile = conversionRuleFile;
    }

    private void transformClassNode(Document document, Node node) throws ClassNotFoundException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        String className = node.getNodeName();
        if(this.classNameRuleMap.containsKey(className)) {
            String newClassName = this.classNameRuleMap.get(className);
            document.renameNode(node, null, newClassName);
            className = newClassName;
        }
        Class<?> dataObjectClass = Class.forName(className);
        if(classPropertyRuleMap.containsKey(className)) {
            transformNode(document, node, dataObjectClass, classPropertyRuleMap.get(className));
        }
        transformNode(document, node, dataObjectClass, classPropertyRuleMap.get("*"));
    }

    private void transformNode(Document document, Node node, Class<?> currentClass, Map<String, String> propertyMappings) throws ClassNotFoundException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        for(Node childNode = node.getFirstChild(); childNode != null;) {
            Node nextChild = childNode.getNextSibling();
            String propertyName = childNode.getNodeName();
            if(childNode.hasAttributes()) {
                XPath xpath = XPathFactory.newInstance().newXPath();
                Node serializationAttribute = childNode.getAttributes().getNamedItem(SERIALIZATION_ATTRIBUTE);
                if(serializationAttribute != null && StringUtils.equals(serializationAttribute.getNodeValue(), "custom")) {
                    Node classAttribute = childNode.getAttributes().getNamedItem(CLASS_ATTRIBUTE);
                    if(classAttribute != null && StringUtils.equals(classAttribute.getNodeValue(), "org.kuali.rice.kns.util.TypedArrayList")) {
                        ((Element)childNode).removeAttribute(SERIALIZATION_ATTRIBUTE);
                        ((Element)childNode).removeAttribute(CLASS_ATTRIBUTE);
                        XPathExpression listSizeExpression = xpath.compile("//" + propertyName + "/org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl/default/size/text()");
                        String size = (String)listSizeExpression.evaluate(childNode, XPathConstants.STRING);
                        List<Node> nodesToAdd = new ArrayList<Node>();
                        if(StringUtils.isNotBlank(size) && Integer.valueOf(size) > 0) {
                            XPathExpression listTypeExpression = xpath.compile("//" + propertyName + "/org.kuali.rice.kns.util.TypedArrayList/default/listObjectType/text()");
                            String listType = (String)listTypeExpression.evaluate(childNode, XPathConstants.STRING);
                            XPathExpression listContentsExpression = xpath.compile("//" + propertyName + "/org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl/" + listType);
                            NodeList listContents = (NodeList)listContentsExpression.evaluate(childNode, XPathConstants.NODESET);
                            for(int i = 0; i < listContents.getLength(); i++) {
                                Node tempNode = listContents.item(i);
                                transformClassNode(document, tempNode);
                                nodesToAdd.add(tempNode);
                            }
                        }
                        for(Node removeNode = childNode.getFirstChild(); removeNode != null;) {
                            Node nextRemoveNode = removeNode.getNextSibling();
                            childNode.removeChild(removeNode);
                            removeNode = nextRemoveNode;
                        }
                        for(Node nodeToAdd : nodesToAdd) {
                            childNode.appendChild(nodeToAdd);
                        }
                    } else {
                        ((Element)childNode).removeAttribute(SERIALIZATION_ATTRIBUTE);

                        XPathExpression mapContentsExpression = xpath.compile("//" + propertyName + "/map/string");
                        NodeList mapContents = (NodeList)mapContentsExpression.evaluate(childNode, XPathConstants.NODESET);
                        List<Node> nodesToAdd = new ArrayList<Node>();
                        if(mapContents.getLength() > 0 && mapContents.getLength() % 2 == 0) {
                            for(int i = 0; i < mapContents.getLength(); i++) {
                                Node keyNode = mapContents.item(i);
                                Node valueNode = mapContents.item(++i);
                                Node entryNode = document.createElement("entry");
                                entryNode.appendChild(keyNode);
                                entryNode.appendChild(valueNode);
                                nodesToAdd.add(entryNode);
                            }
                        }
                        for(Node removeNode = childNode.getFirstChild(); removeNode != null;) {
                            Node nextRemoveNode = removeNode.getNextSibling();
                            childNode.removeChild(removeNode);
                            removeNode = nextRemoveNode;
                        }
                        for(Node nodeToAdd : nodesToAdd) {
                            childNode.appendChild(nodeToAdd);
                        }
                    }
                }
            }
            if(propertyMappings != null && propertyMappings.containsKey(propertyName)) {
                String newPropertyName = propertyMappings.get(propertyName);
                if(StringUtils.isNotBlank(newPropertyName)) {
                    document.renameNode(childNode, null, newPropertyName);
                    propertyName = newPropertyName;
                } else {
                    // If there is no replacement name then the element needs
                    // to be removed and skip all other processing
                    node.removeChild(childNode);
                    childNode = nextChild;
                    continue;
                }
            }
            if(childNode.hasChildNodes() && !(Collection.class.isAssignableFrom(currentClass) || Map.class.isAssignableFrom(currentClass))) {
                if(propertyName.equals("principalId") && (node.getNodeName().equals("dataManagerUser") || node.getNodeName().equals("dataStewardUser"))){
                    currentClass = new org.kuali.rice.kim.impl.identity.PersonImpl().getClass();
                }
                Class<?> propertyClass = PropertyUtils.getPropertyType(currentClass.newInstance(), propertyName);
                if(propertyClass != null && classPropertyRuleMap.containsKey(propertyClass.getName())) {
                    transformNode(document, childNode, propertyClass, this.classPropertyRuleMap.get(propertyClass.getName()));
                }
                transformNode(document, childNode, propertyClass, classPropertyRuleMap.get("*"));
            }
            childNode = nextChild;
        }
    }

    private void setRuleMaps() {
        setupConfigurationMaps();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            AbstractResource resource = null;
            Document doc = null;
            if(StringUtils.startsWith(this.getConversionRuleFile(), "classpath")) {
                resource = new ClassPathResource(this.getConversionRuleFile(), Thread.currentThread().getContextClassLoader());
            } else {
                resource = new FileSystemResource(this.getConversionRuleFile());
            }
            if(!resource.exists()) {
                doc = db.parse(this.getClass().getResourceAsStream(this.getConversionRuleFile()));
            } else {
                doc = db.parse(resource.getInputStream());
            }
            doc.getDocumentElement().normalize();
            XPath xpath = XPathFactory.newInstance().newXPath();

            // Get the moved classes rules
            XPathExpression exprClassNames = xpath.compile("//*[@name='maint_doc_classname_changes']/pattern");
            NodeList classNamesList = (NodeList) exprClassNames.evaluate(doc, XPathConstants.NODESET);
            for (int s = 0; s < classNamesList.getLength(); s++) {
                String matchText = xpath.evaluate("match/text()", classNamesList.item(s));
                String replaceText = xpath.evaluate("replacement/text()", classNamesList.item(s));
                classNameRuleMap.put(matchText, replaceText);
            }

            // Get the property changed rules

            XPathExpression exprClassProperties = xpath.compile(
                    "//*[@name='maint_doc_changed_class_properties']/pattern");
            XPathExpression exprClassPropertiesPatterns = xpath.compile("pattern");
            NodeList propertyClassList = (NodeList) exprClassProperties.evaluate(doc, XPathConstants.NODESET);
            for (int s = 0; s < propertyClassList.getLength(); s++) {
                String classText = xpath.evaluate("class/text()", propertyClassList.item(s));
                Map<String, String> propertyRuleMap = new HashMap<String, String>();
                NodeList classPropertiesPatterns = (NodeList) exprClassPropertiesPatterns.evaluate(
                        propertyClassList.item(s), XPathConstants.NODESET);
                for (int c = 0; c < classPropertiesPatterns.getLength(); c++) {
                    String matchText = xpath.evaluate("match/text()", classPropertiesPatterns.item(c));
                    String replaceText = xpath.evaluate("replacement/text()", classPropertiesPatterns.item(c));
                    propertyRuleMap.put(matchText, replaceText);
                }
                classPropertyRuleMap.put(classText, propertyRuleMap);
            }
        } catch (Exception e) {
            System.out.println("Error parsing rule xml file. Please check file. : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupConfigurationMaps() {
        classNameRuleMap = new HashMap<String, String>();
        classPropertyRuleMap = new HashMap<String, Map<String,String>>();

        // Pre-populate the class property rules with some defaults which apply to every BO
        Map<String, String> defaultPropertyRules = new HashMap<String, String>();
        defaultPropertyRules.put("boNotes", "");
        defaultPropertyRules.put("autoIncrementSet", "");
        classPropertyRuleMap.put("*", defaultPropertyRules);
    }
}

