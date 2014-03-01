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
package org.kuali.rice.scripts.beans

import groovy.xml.XmlUtil
import org.junit.Assert

import groovy.xml.QName

/**
 * This class contains many of the helper methods used in the other bean transformer tests
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class BeanTransformerTestBase {

    public static String TEST_RESOURCE_DIR = "./src/test/resources/";
    public static String DICT_TEST_DIR = "DictionaryConverterTest/";
    public static String TEST_CONFIG_FILE_PATH = "test.config.properties"
    ConfigObject config;

    void setUp() {
        File configFile = getTestResourceFile(TEST_CONFIG_FILE_PATH);
        config = new ConfigSlurper().parse(configFile.text);
    }


    // helper functions
    public String getDictionaryTestDir() {
        return DICT_TEST_DIR;
    }

    def getConfig() {
        return config;
    }

    public void checkBeanExistsByParentId(def rootNode, String parentName) {
        Assert.assertTrue("root should contains bean with parent id " + parentName, rootNode.bean.findAll { parentName.equals(it.@parent) }.size() > 0);
    }

    public void checkBeanExistsById(def rootNode, String beanId) {
        Assert.assertTrue("root should contains bean with id " + beanId, rootNode.bean.findAll { beanId.equals(it.@id) }.size() > 0);
    }

    public File getTestResourceFile(String relativeFilePath) {
        return new File(this.getClass().getClassLoader().getResource(relativeFilePath).file);
    }

    /**
     * checks whether property exists as a tag or an attribute in a bean node
     *
     * @param beanNode
     * @param propertyName
     */
    public void checkBeanPropertyExists(def beanNode, String propertyName) {
        def tagAssertion = beanNode.property.findAll { propertyName.equals(it.@name) }.size() == 1;
        def attrAssertion = beanNode.attributes().findAll {
            it.key instanceof QName && propertyName.equals(it.key.localPart) }.size() == 1;
        Assert.assertTrue("bean should contain property " + propertyName + " " + getNodeString(beanNode), tagAssertion || attrAssertion);
    }

    public void checkBeanPropertyNotExists(def beanNode, String propertyName) {
        Assert.assertTrue("bean should not contain property " + propertyName, beanNode.property.findAll { propertyName.equals(it.@name) }.size() == 0);
    }

    public void checkBeanPropertyValueExists(def beanNode, String propertyName, String propertyValue) {
        checkBeanPropertyExists(beanNode,propertyName)
        def value = beanNode.property.find { propertyName.equals(it.@name) }?.@value;
        Assert.assertEquals("" + propertyName + "exists but should contains value", propertyValue, value);
    }

    public Node getFileRootNode(String filepath) {
        def file = getTestResourceFile(filepath);
        return new XmlParser().parse(file);
    }

    public static String getNodeString(Node rootNode) {
        def writer = new StringWriter()
        XmlUtil.serialize(rootNode, writer)
        return writer.toString()
    }

    public static Node getSimpleSpringXmlNode() {
        def rootBean = new XmlParser().parseText("<beans>" + "<bean xmlns:p=\"http://www.springframework.org/schema/p\"  id='SimpleBean' p:name='SimpleBean' parent='SpringBean' attributeName='test'>" + "<property name='simpleProperty' value='test' />" + "<property name='propertyWithRef' value='value2' />" + "<property name='propertyList'>" + "<list><value>1</value><value>2</value></list>" + "</property>" + "<property name='propertyListWithBeans'>" + "<list><bean id='test' parent='FieldDefinition' attributeName='builder'/></list>" + "</property>" + "</bean>" + "</beans>");
        return rootBean;
    }

    /**
     * Used to check bean structure has been transformed appropriately.  Helpful for cases which carry over properties
     * that should not be included in the new bean.
     *
     * @param beanNode bean being validated
     * @param containsProperties properties that should exist in the bean structure
     * @param invalidProperties properties that should not exist in the bean structure
     */
    public void checkBeanStructure(Node beanNode, List containsProperties, List invalidProperties) {
        containsProperties?.each { property -> checkBeanPropertyExists(beanNode, property); }
        invalidProperties?.each { property -> checkBeanPropertyNotExists(beanNode, property); }
    }

    /**
     * Checks property name exists with expected value within the bean (either as a property tag or namespace attr)
     *
     * @param beanNode bean being reviewed for property
     * @param propertyName
     * @param propertyValue expected value tied to property name
     * @return
     */
    public boolean hasPropertyValue(Node beanNode, String propertyName, String propertyValue) {
        if(beanNode?.property?.find { it.@name == propertyName  && it.@value == propertyValue}) {
            return true;
        } else if (beanNode.attributes()?.find { key, value -> key instanceof QName &&
                ((QName) key).getLocalPart() == propertyName  && value == propertyValue}) {
            return true;
        }
        return false;
    }

}
