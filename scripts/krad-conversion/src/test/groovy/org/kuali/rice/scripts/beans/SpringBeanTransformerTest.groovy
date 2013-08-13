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

import groovy.util.logging.Log
import groovy.xml.XmlUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests for the {@link org.kuali.rice.scripts.SpringBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class SpringBeanTransformerTest {
    static def testResourceDir = "./src/test/resources/"
    static def dictTestDir = testResourceDir + "DictionaryConverterTest/"

    SpringBeanTransformer springBeanTransformer
    def config

    @Before
    void setUp() {
        def configFilePath = testResourceDir + "test.config.properties";
        config = new ConfigSlurper().parse(new File(configFilePath).toURL());
        log.info "loading spring transformer"
        springBeanTransformer = new SpringBeanTransformer();
        springBeanTransformer.init(config);
    }

    // Utilities

    @Test
    void testCopyProperties() {
        log.info "processing"
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='test' /><property name='title2' value='value2' />" + "<property name='title3'><list><value>1</value><value>2</value></list></property>" + "</bean></beans>");
        def copyNode = new XmlParser().parseText("<beans><bean parent='SampleAppBean'></bean></beans>");
        def beanNode = rootBean.bean[0];

        copyNode.bean[0].replaceNode {
            bean() {
                springBeanTransformer.copyProperties(delegate, beanNode, ["title"]);
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
                springBeanTransformer.renameProperties(delegate, beanNode, ["title": "title3"]);
            }
        }

        Assert.assertTrue(copyNode.bean[0].property.findAll { it.@name == "title3" }.size() > 0);
        Assert.assertTrue(copyNode.bean[0].property.findAll { it.@name == "title2" }.size() == 0);
    }

    @Test
    void testRemoveProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='value1' /><property name='title2' value='value2' />" + "</bean></beans>");

        rootBean.bean.each { beanNode -> springBeanTransformer.removeProperties(beanNode, ["title"]); }

        Assert.assertTrue(rootBean.bean[0].property.findAll { it.@name == "title" }.size() == 0);
        Assert.assertTrue(rootBean.bean[0].property.findAll { it.@name == "title2" }.size() > 0);
    }

    @Test
    void testFixNamespaceProperties() {
        def maintDefFilePath = dictTestDir + "AttributePropertySample.xml"
        def ddRootNode = getFileRootNode(maintDefFilePath)
        ddRootNode.bean.each { bean -> springBeanTransformer.fixNamespaceProperties(bean) }
        Assert.assertEquals("bean properties size does not match", 5, ddRootNode.bean.property.size())
    }

    @Test
    void testTransformControlProperty() {
        def inqDefFilePath = dictTestDir + "ControlFieldSample.xml"
        def ddRootNode = getFileRootNode(inqDefFilePath)
        try {
            springBeanTransformer.transformControlProperty(ddRootNode.bean, config.map.convert.dd_bean_control)
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }

        // validate a control field and options finder were generated
        Assert.assertEquals("control field count", 1, ddRootNode.bean.property.findAll { it.@name == "controlField" }.size())
        Assert.assertEquals("options finder count", 1, ddRootNode.bean.property.findAll { it.@name == "optionsFinder" }.size())
        Assert.assertEquals("control count", 0, ddRootNode.bean.property.findAll { it.@name == "control" }.size())

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

        springBeanTransformer.removeChildrenBeans(beanNode);
        Assert.assertEquals("child bean still exists", ddRootNode.findAll { parentName.equals(it.@name) }.size(), 0);
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

}