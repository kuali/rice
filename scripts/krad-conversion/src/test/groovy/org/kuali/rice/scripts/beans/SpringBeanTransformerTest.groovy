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
    void testCopyBeanProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='title' value='test' /><property name='title2' value='value2' />" + "<property name='title3'><list><value>1</value><value>2</value></list></property>" + "</bean></beans>");
        def copyNode = new XmlParser().parseText("<beans><bean parent='SampleAppBean'></bean></beans>");
        def beanNode = rootBean.bean[0];

        copyNode.bean[0].replaceNode {
            bean() {
                springBeanTransformer.copyBeanProperties(delegate, beanNode, ["title", "title3"]);
            }
        }

        def copyBean = copyNode.bean[0];
        checkBeanStructure(copyBean, ["title", "title3"], ["title2"]);
        def copyPropertyList = copyBean.property.find { "title3".equals(it.@name) };
        Assert.assertTrue("copied properties should contain list with values", copyPropertyList?.list?.value?.size() == 2);
    }

    @Test
    void testRenameProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='old' value='value1' /><property name='uncopied' value='value2' />" + "</bean></beans>");
        def copyNode = new XmlParser().parseText("<beans><bean parent='SampleAppBean'></bean></beans>");
        def beanNode = rootBean.bean[0];

        copyNode.bean[0].replaceNode {
            bean() {
                springBeanTransformer.renameProperties(delegate, beanNode, ["old": "new"]);
            }
        }

        checkBeanStructure(copyNode.bean[0], ["new"], ["uncopied"]);
    }

    @Test
    void testRemoveProperties() {
        def rootBean = new XmlParser().parseText("<beans><bean parent='SampleAppBean'>" + "<property name='keptProperty' value='value1' />" + "<property name='removedProperty' value='value2' />" + "</bean></beans>");

        rootBean.bean.each { beanNode -> springBeanTransformer.removeProperties(beanNode, ["removedProperty"]); }

        checkBeanStructure(rootBean.bean[0], ["keptProperty"], ["removedProperty"]);
    }

    @Test
    void testFixNamespaceProperties() {
        def maintDefFilePath = getDictionaryTestDir() + "AttributePropertySample.xml"
        def ddRootNode = getFileRootNode(maintDefFilePath)
        ddRootNode.bean.each { bean -> springBeanTransformer.fixNamespaceProperties(bean) }
        Assert.assertEquals("bean properties size does not match", 5, ddRootNode.bean.property.size())
    }

   @Test
    void testGatherProperties() {
        def ddRootNode = getSimpleSpringXmlNode();

       def searchProperties = ["simpleProperty":"simpleProperty"];
       def properties = springBeanTransformer.gatherPropertyTags(ddRootNode.bean[0], searchProperties);
       Assert.assertTrue("attribute list should contain property tags - " + properties, properties["simpleProperty"] != null);

        def searchAttrs = ["name":"name"];
        def attributes = springBeanTransformer.gatherPropertyAttrs(ddRootNode.bean[0], searchAttrs);
        Assert.assertTrue("attribute list should contain property attribute name - " + attributes, attributes["name"] != null);

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
        String lookupDefFilePath = getDictionaryTestDir() + "LookupDefinitionSample.xml";
        def lookupDefFile = getTestResourceFile(lookupDefFilePath);
        def ddRootNode = new XmlParser().parse(lookupDefFile);
        def beanNode = ddRootNode.bean.find { "BusinessObjectEntry".equals(it.@parent) };
        String parentName = beanNode.@parent;

        springBeanTransformer.removeChildrenBeans(beanNode);
        Assert.assertEquals("child bean still exists", ddRootNode.findAll { parentName.equals(it.@name) }.size(), 0);
    }


    @Test
    public void testGetTranslatedBeanIdsForLookups() {
        Map translateTestCases = ["Book-lookupDefinition": "Book-LookupView",
                "Book-LookupDefinition-parentBean": "Book-LookupView-parentBean",
                "CommitteeLookup": "CommitteeLookup-LookupView",
                "Account-Delegate-Global-parentBean": "Account-Delegate-Global-LookupView-parentBean",
                "Institutional-Proposal-lookupdefinition-4-parentBean": "Institutional-Proposal-LookupView-4-parentBean"];

        translateTestCases.entrySet().each { entry ->
            String actualResult = springBeanTransformer.getTranslatedBeanId(entry.key, "LookupDefinition", "Uif-LookupView");
            Assert.assertEquals("incorrect translation of bean id", entry.value, actualResult);
        }
    }

}