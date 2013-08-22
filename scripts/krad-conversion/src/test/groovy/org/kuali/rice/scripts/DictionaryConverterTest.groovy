/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.scripts

import groovy.util.logging.Log
import groovy.xml.XmlUtil
import org.apache.commons.lang.StringUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests for the {@link DictionaryConverter} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class DictionaryConverterTest {
    static def testResourceDir = "./src/test/resources/"
    static def dictTestDir = testResourceDir + "DictionaryConverterTest/"

    DictionaryConverter dictionaryConverter
    def config

    @Before
    void setUp() {
        def configFilePath = testResourceDir + "test.config.properties"
        config = new ConfigSlurper().parse(new File(configFilePath).toURL())
        dictionaryConverter = new DictionaryConverter(config)
    }

    // Utilities

    @Test
    void testCopyProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='test' /><property name='title2' value='value2' />" + "<property name='title3'><list><value>1</value><value>2</value></list></property>" + "</bean></beans>");
        def copyNode = new XmlParser().parseText("<beans><bean parent='SampleAppBean'></bean></beans>");
        def beanNode = rootBean.bean[0];

        copyNode.bean[0].replaceNode {
            bean() {
                dictionaryConverter.copyProperties(delegate, beanNode, ["title"]);
            }
        }

        Assert.assertTrue(copyNode.bean[0].property.findAll { it.@name == "title" }.size() > 0);
        Assert.assertTrue(copyNode.bean[0].property.findAll { it.@name == "title2" }.size() == 0);
    }

    @Test
    void testRenameProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='value1' /><property name='title2' value='value2' />" + "</bean></beans>");
        def copyNode = new XmlParser().parseText("<beans><bean parent='SampleAppBean'></bean></beans>");
        def beanNode = rootBean.bean[0];

        copyNode.bean[0].replaceNode {
            bean() {
                dictionaryConverter.renameProperties(delegate, beanNode, ["title": "title3"]);
            }
        }

        Assert.assertTrue(copyNode.bean[0].property.findAll { it.@name == "title3" }.size() > 0);
        Assert.assertTrue(copyNode.bean[0].property.findAll { it.@name == "title2" }.size() == 0);
    }

    @Test
    void testRemoveProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='value1' /><property name='title2' value='value2' />" + "</bean></beans>");

        rootBean.bean.each { beanNode -> dictionaryConverter.removeProperties(beanNode, ["title"]); }

        Assert.assertTrue(rootBean.bean[0].property.findAll { it.@name == "title" }.size() == 0);
        Assert.assertTrue(rootBean.bean[0].property.findAll { it.@name == "title2" }.size() > 0);
    }

    @Test
    void testFixNamespaceProperties() {
        def maintDefFilePath = dictTestDir + "AttributePropertySample.xml"
        def ddRootNode = getFileRootNode(maintDefFilePath)
        ddRootNode.bean.each { bean -> dictionaryConverter.fixNamespaceProperties(bean) }
        Assert.assertEquals("bean properties size does not match", 5, ddRootNode.bean.property.size())
    }

    @Test
    void testTransformBusinessObjectEntryBean() {
        String xmlFilePath = dictTestDir + "InquiryDefinitionSample.xml"
        def rootNode = getFileRootNode(xmlFilePath)
        def beanNode = rootNode.bean.find { "BusinessObjectEntry" == it.@parent }
        Node resultNode = null;
        try {
            resultNode = dictionaryConverter.transformBusinessObjectEntryBean(beanNode)
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }

        log.finer "result for transforming business object entry - " + getNodeString(resultNode);
        def expectedProperties = [];
        Assert.assertEquals("properties copied or renamed properly", beanNode.findAll { expectedProperties.contains(it.@name) }.size(), expectedProperties.size())
    }

    @Test
    void testTransformAttributeDefinitionBeanUsingTransformBusinessObjectEntryBean() {
        String xmlFilePath = dictTestDir + "InquiryDefinitionSample.xml"
        def rootNode = getFileRootNode(xmlFilePath)
        def beanNode = rootNode.bean.find { "TravelerDetail-id-parentBean" == it.@id }
        Node resultNode = null;
        try {
            resultNode = dictionaryConverter.transformBusinessObjectEntryBean(beanNode)
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }


        checkBeanPropertyExists(resultNode, "validCharactersConstraint");
        def constraintProperty = resultNode.property.find { "validCharactersConstraint".equals(it.@name) };
        Assert.assertTrue("constraint bean not converted", "NumericPatternConstraint".equals(constraintProperty.bean[0].@parent));
        checkBeanPropertyExists(resultNode, "controlField");
    }

    @Test
    public void testTransformSpringBeans() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='BusinessObjectEntry'>" + "<property name='title' value='test' /><property name='title2' value='value2' />" + "</bean></beans>");
        try {
            dictionaryConverter.transformSpringBeans(rootBean);
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }
        checkBeanParentExists(rootBean, "DataObjectEntry");
    }

    /**
     * Removes any children beans that exists from the xml file
     *
     */
    @Test
    public void testRemoveChildrenBeans() {
        String lookupDefFilePath = dictTestDir + "LookupDefinitionSample.xml"
        def lookupDefFile = new File(lookupDefFilePath)
        def ddRootNode = new XmlParser().parse(lookupDefFile);
        def beanNode = ddRootNode.bean.find { "BusinessObjectEntry".equals(it.@parent) };
        String parentName = beanNode.@parent;

        dictionaryConverter.removeChildrenBeans(beanNode);
        Assert.assertEquals("child bean still exists", ddRootNode.findAll { parentName.equals(it.@name) }.size(), 0);
    }

    @Test
    public void testIsBeanTransformable() {
        // test lookup definition bean
        def inquiryDefNode = getSimpleBean(["parent": "InquiryDefinition"], "");
        def lookupDefNode = getSimpleBean(["parent": "InquiryDefinition"], "");
        def nonTransformNode = getSimpleBean(["parent": "LookupDefinition2"], "");
        Assert.assertTrue("inquiry definition should be transformable", dictionaryConverter.isBeanTransformable(inquiryDefNode));
        Assert.assertTrue("lookup definition should be transformable", dictionaryConverter.isBeanTransformable(lookupDefNode));
        Assert.assertTrue("inquiry definition should not be transformable", !dictionaryConverter.isBeanTransformable(nonTransformNode));
    }

    @Test
    public void testIsPropertyTransformable() {
        // test control property
        Assert.assertTrue("property method not found", dictionaryConverter.isPropertyTransformable("control"));
        Assert.assertTrue("property method not found but returned true", !dictionaryConverter.isPropertyTransformable("controls"));
    }

    @Test
    void testPreloadDefinitionDataObjects() {
        String xmlFilePath = dictTestDir + "InquiryDefinitionSample.xml"
        def rootNode = getFileRootNode(xmlFilePath)

        try {
            dictionaryConverter.preloadDefinitionDataObjects(rootNode);
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }
        Assert.assertTrue("lookup definition entry added", dictionaryConverter.definitionDataObjects.containsKey("TravelerDetail-lookupDefinition"));

    }

    // helper functions

    public void checkBeanParentExists(def rootNode, String parentName) {
        Assert.assertTrue("root should contains parent bean " + parentName, rootNode.bean.findAll { parentName.equals(it.@parent) }.size() > 0);
    }

    public void checkBeanPropertyExists(def beanNode, String propertyName) {
        Assert.assertTrue("bean should contains property " + propertyName, beanNode.property.findAll { propertyName.equals(it.@name) }.size() > 0);
    }

    public Node getFileRootNode(String filepath) {
        def file = new File(filepath);
        return new XmlParser().parse(file);
    }

    public static String getNodeString(Node rootNode) {
        def writer = new StringWriter()
        XmlUtil.serialize(rootNode, writer)
        return writer.toString()
    }

    public static getSimpleBean(Map attributes, String value) {
        return getSimpleNode("bean", attributes, value);
    }

    public static getSimpleProperty(Map attributes, String value) {
        return getSimpleNode("property", attributes, value);
    }

    public static getSimpleNode(String nodeType, Map attributes, String value) {
        Node node = new Node(null, nodeType, attributes, value);
        return node;
    }

}