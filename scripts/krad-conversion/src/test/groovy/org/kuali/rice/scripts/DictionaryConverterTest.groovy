/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.scripts;

import groovy.util.logging.Log;
import groovy.xml.XmlUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link DictionaryConverter} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class DictionaryConverterTest {

    static def dictTestDir = "DictionaryConverterTest/";

    DictionaryConverter dictionaryConverter;
    def config;

    @Before
    void setUp() {
        def configFilePath = "test.config.properties";
        def configFile = ConversionUtils.getResourceFile(configFilePath);
        config = new ConfigSlurper().parse(configFile.text);
        dictionaryConverter = new DictionaryConverter(config);
    }

    // Utilities

    @Test
    void testCopyBeanProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='test' /><property name='title2' value='value2' />" + "<property name='title3'><list><value>1</value><value>2</value></list></property>" + "</bean></beans>");
        def copyNode = new XmlParser().parseText("<beans><bean parent='SampleAppBean'></bean></beans>");
        def beanNode = rootBean.bean[0];

        copyNode.bean[0].replaceNode {
            bean() {
                dictionaryConverter.copyBeanProperties(delegate, beanNode, ["title"]);
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
        def relFilePath = dictTestDir + "AttributePropertySample.xml";
        def maintDefFilePath = ConversionUtils.getResourceFile(relFilePath).absolutePath;
        def ddRootNode = getFileRootNode(maintDefFilePath);
        ddRootNode.bean.each { bean -> dictionaryConverter.fixNamespaceProperties(bean) };
        Assert.assertEquals("bean properties size does not match", 5, ddRootNode.bean.property.size());
    }

    @Test
    void testFindSpringBeanFiles() {
        List files = [ConversionUtils.getResourceFile(dictTestDir + "AttributePropertySample.xml")];
        try {
            def simpleBeanFileList = dictionaryConverter.findSpringBeanFiles(files, [], []);
            def beanBasedFileList = dictionaryConverter.findSpringBeanFiles(files, ["MaintenanceDocumentEntry"], []);
            def emptyBeanFileList = dictionaryConverter.findSpringBeanFiles(files, ["BusinessObjectEntry"], []);
            def propBasedFileList = dictionaryConverter.findSpringBeanFiles(files, [], ["maintainableSections"]);

            Assert.assertEquals("simple bean list count", 1, simpleBeanFileList.size());
            Assert.assertEquals("bean based list count", 1, beanBasedFileList.size());
            Assert.assertEquals("invalid bean type list count", 0, emptyBeanFileList.size());
            Assert.assertEquals("property based list count", 1, propBasedFileList.size());
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing");
        }
    }

    @Test
    void testFindTransformableSpringBeanFiles() {
        List files = [ConversionUtils.getResourceFile(dictTestDir + "AttributePropertySample.xml")];
        List transformableFiles = [];
        try {
            // should find file containing MaintenanceDocumentEntry transformable
            transformableFiles = dictionaryConverter.findTransformableSpringBeanFiles(files);
            Assert.assertEquals("simple bean list count", 1, transformableFiles.size());
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing");
        }
    }



    @Test
    public void testTransformSpringBeans() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='BusinessObjectEntry'>" + "<property name='title' value='test' /><property name='title2' value='value2' />" + "</bean></beans>");
        try {
            dictionaryConverter.transformSpringBeans(rootBean);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }
        checkBeanParentExists(rootBean, "DataObjectEntry");
    }

    /**
     * Removes any children beans that exists from the xml file
     *
     */

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
        String xmlFilePath = ConversionUtils.getResourceFile(dictTestDir + "InquiryDefinitionSample.xml").absolutePath;
        def rootNode = getFileRootNode(xmlFilePath);

        try {
            dictionaryConverter.preloadDefinitionDataObjects(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
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
        def writer = new StringWriter();
        XmlUtil.serialize(rootNode, writer);
        return writer.toString();
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