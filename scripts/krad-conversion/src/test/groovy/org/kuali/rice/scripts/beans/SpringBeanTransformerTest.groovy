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
import groovy.xml.QName
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests for the {@link org.kuali.rice.scripts.beans.SpringBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class SpringBeanTransformerTest extends BeanTransformerTestBase {

    SpringBeanTransformer springBeanTransformer;

    @Before
    void setUp() {
        super.setUp();
        springBeanTransformer = new SpringBeanTransformer();
        springBeanTransformer.init(config);
    }

    // Utilities

    @Test
    void testCopyProperties() {
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
        def maintDefFilePath = getDictionaryTestDir() + "AttributePropertySample.xml"
        def ddRootNode = getFileRootNode(maintDefFilePath)
        ddRootNode.bean.each { bean -> springBeanTransformer.fixNamespaceProperties(bean) }
        Assert.assertEquals("bean properties size does not match", 5, ddRootNode.bean.property.size())
    }

    @Test
    void testTransformControlProperty() {
        String inqDefFilePath = getDictionaryTestDir() + "ControlFieldSample.xml";
        def ddRootNode = getFileRootNode(inqDefFilePath);
        def renamedControlDefinitions = config.map.convert.dd_bean_control;
        def selectBeanNode = ddRootNode.bean.find { "BookOrder-bookId-parentBean".equals(it.@id) };
        def textAreaBeanNode = ddRootNode.bean.find { "BookOrder-value-parentBean".equals(it.@id) };

        try {
            springBeanTransformer.transformControlProperty(selectBeanNode, renamedControlDefinitions);
            springBeanTransformer.transformControlProperty(textAreaBeanNode, renamedControlDefinitions);
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }

        // validate a control field and options finder were generated
        Assert.assertEquals("control field count", 1, selectBeanNode.property.findAll { it.@name == "controlField" }.size());
        Assert.assertEquals("options finder count", 1, selectBeanNode.property.findAll { it.@name == "optionsFinder" }.size());
        Assert.assertEquals("control count", 0, selectBeanNode.property.findAll { it.@name == "control" }.size());

        // testing text area control transform
        def textAreaControlField = textAreaBeanNode.property.findAll { it.@name == "controlField" };
        Assert.assertEquals("control field count", 1, textAreaControlField.size());

    }

    @Test
    void testGenericNodeTransform() {
        def ddRootNode = getSimpleSpringXmlNode();
        def searchAttrs = ["*name": "p:propertyName"];
        def attributes = springBeanTransformer.genericGatherAttributes(ddRootNode.bean[0], searchAttrs);
        Assert.assertTrue("attribute list should contain propertyName", attributes["p:propertyName"] != null);

        ddRootNode.bean[0].replaceNode {
            list {
                springBeanTransformer.genericNodeTransform(delegate, "bean", ["id": "helloworld"], "");
                springBeanTransformer.genericNodeTransform(delegate, "value", [:], "1");
            }

        }
        Assert.assertTrue("bean count should be 1", ddRootNode.list.bean.size() == 1);
        Assert.assertTrue("value count should be 1", ddRootNode.list.value.size() == 1);

    }

    /**
     * Removes any children beans that exists from the xml file
     *
     */
    @Test
    public void testRemoveChildrenBeans() {
        String lookupDefFilePath = getDictionaryTestDir() + "LookupDefinitionSample.xml"
        def lookupDefFile = new File(lookupDefFilePath)
        def ddRootNode = new XmlParser().parse(lookupDefFile);
        def beanNode = ddRootNode.bean.find { "BusinessObjectEntry".equals(it.@parent) };
        String parentName = beanNode.@parent;

        springBeanTransformer.removeChildrenBeans(beanNode);
        Assert.assertEquals("child bean still exists", ddRootNode.findAll { parentName.equals(it.@name) }.size(), 0);
    }

    @Test
    public void testGetTranslatedBeanIdForParentBean() {
        String beanId = "Book-LookupDefinition-parentBean";
        String actualResult = springBeanTransformer.getTranslatedBeanId(beanId, "LookupDefinition", "Uif-LookupView");
        String expectedResult = "Book-LookupView-parentBean";
        Assert.assertEquals("incorrect translation of bean id", expectedResult, actualResult);
    }

}