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

    public static String testResourceDir = "./src/test/resources/";
    public static String dictionaryTestDir = testResourceDir + "DictionaryConverterTest/";

    ConfigObject config;

    void setUp() {
        String configFilePath = testResourceDir + "test.config.properties";
        config = new ConfigSlurper().parse(new File(configFilePath).toURL());
    }


    // helper functions
    public String getDictionaryTestDir() {
        return dictionaryTestDir;
    }

    def getConfig() {
        return config;
    }

    public void checkBeanParentExists(def rootNode, String parentName) {
        Assert.assertTrue("root should contains parent bean " + parentName, rootNode.bean.findAll { parentName.equals(it.@parent) }.size() > 0);
    }

    /**
     * checks whether property exists as a tag or an attribute in a bean node
     *
     * @param beanNode
     * @param propertyName
     */
    public void checkBeanPropertyExists(def beanNode, String propertyName) {
        def tagAssertion = beanNode.property.findAll { propertyName.equals(it.@name) }.size() > 0;
        def attrAssertion = beanNode.attributes().findAll {
            it.key.contains("p:") && propertyName.equals(it.key.minus("p:")) }.size() > 0;
        Assert.assertTrue("bean should contains property " + propertyName, tagAssertion || attrAssertion);
    }

    public void checkBeanPropertyNotExists(def beanNode, String propertyName) {
        Assert.assertTrue("bean should not contain property " + propertyName, beanNode.property.findAll { propertyName.equals(it.@name) }.size() == 0);
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

    public static Node getSimpleSpringXmlNode() {
        def rootBean = new XmlParser().parseText("<beans>" + "<bean xmlns:p=\"http://www.springframework.org/schema/p\"  id='SimpleBean' p:name='SimpleBean' parent='SpringBean' attributeName='test'>" + "<property name='simpleProperty' value='test' />" + "<property name='propertyWithRef' value='value2' />" + "<property name='propertyList'>" + "<list><value>1</value><value>2</value></list>" + "</property>" + "<property name='propertyListWithBeans'>" + "<list><bean id='test' parent='FieldDefinition' attributeName='builder'/></list>" + "</property>" + "</bean>" + "</beans>");
        return rootBean;
    }

    /**
     * Used to check bean structure has been transformed appropriately.  Helpful for cases which carry over properties
     * that should not be included in the new bean.
     *
     * @param beanNode - bean being validated
     * @param containsProperties - properties that should exist in the bean structure
     * @param invalidProperties - properties that should not exist in the bean structure
     */
    public void checkBeanStructure(Node beanNode, List containsProperties, List invalidProperties) {
        containsProperties?.each { property -> checkBeanPropertyExists(beanNode, property); }
        invalidProperties?.each { property -> checkBeanPropertyNotExists(beanNode, property); }
    }

}
