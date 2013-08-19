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

    /**
     * Converts lookup fields properties into criteria fields and handles all children bean nodes properly
     *
     * Success Criteria
     *  - property is renamed as criteria fields
     *  - all 'field definitions' that are attributeNames are turned into LookupCriteriaInputField
     */
    @Test
    public void testTransformLookupFieldsProperties() {
        String lookupDefFilePath = dictTestDir + "LookupDefinitionSample.xml"
        def ddRootNode = getFileRootNode(lookupDefFilePath);
        def beanNode = ddRootNode.bean.find { "LookupDefinition".equals(it.@parent) };
        String parentName = beanNode.@parent;
        beanNode.replaceNode {
            bean(parent: "Uif-LookupView") {
                dictionaryConverter.transformLookupFieldsProperty(delegate, beanNode);
            }
        }


        // confirm lookup fields has been replaced with criteria fields
        Assert.assertEquals("lookupFields not longer exists", 0, ddRootNode.findAll { parentName.equals(it.@name) }.size());
        Assert.assertEquals("criteriaFields exists", 1, ddRootNode.bean.property.findAll { "criteriaFields".equals(it.@name) }.size());
    }

    /**
     * Tests conversion of lookup definition's result fields into appropriate property
     *
     */
    @Test
    public void testTransformResultFieldsProperties() {
        String lookupDefFilePath = dictTestDir + "LookupDefinitionSample.xml"
        def ddRootNode = getFileRootNode(lookupDefFilePath);
        def beanNode = ddRootNode.bean.find { "LookupDefinition".equals(it.@parent) };
        String parentName = beanNode.@parent;
        beanNode = beanNode.replaceNode {
            bean(parent: "Uif-LookupView") {
                dictionaryConverter.transformResultFieldsProperty(delegate, beanNode);
            }
        }

        // confirm lookup fields has been replaced with criteria fields
        checkBeanPropertyExists(beanNode, "resultFields");
        beanNode = ddRootNode.bean.find { "Uif-LookupView".equals(it.@parent) };
        def resultsFieldProperty = beanNode.property.find { "resultFields".equals(it.@name) };
        def dataFieldSize = resultsFieldProperty.list.bean.findAll { "Uif-DataField".equals(it.@parent) }.size();
        Assert.assertEquals("number of converted data fields did not match", 11, dataFieldSize);
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

    /**
     * Tests that inquiry conversion generates a inquiry view including
     * title, viewname, data object, processes through inquiry fields and collections
     *
     */
    @Test
    public void testTransformInquiryDefinitionBean() {
        String inquiryDefPath = dictTestDir + "InquiryDefinitionSample.xml"
        def ddRootNode = getFileRootNode(inquiryDefPath);
        def beanNode = ddRootNode.bean.find { "InquiryDefinition".equals(it.@parent) };
        String parentName = beanNode.@parent;
        dictionaryConverter.definitionDataObjects.put("TravelerDetail-lookupDefinition", "org.kuali.rice.krad.demo.travel.authorization.dataobject.TravelerDetail");
        dictionaryConverter.transformInquiryDefinitionBean(beanNode);
        checkBeanParentExists(ddRootNode, "Uif-InquiryView");

    }

    @Test
    public void testTransformInquirySectionsProperty() {
        String inquiryDefPath = dictTestDir + "InquiryDefinitionSample.xml"
        def ddRootNode = getFileRootNode(inquiryDefPath);
        def beanNode = ddRootNode.bean.find { "InquiryDefinition".equals(it.@parent) };
        String parentName = beanNode.@parent;
        dictionaryConverter.definitionDataObjects.put("TravelerDetail-lookupDefinition", "org.kuali.rice.krad.demo.travel.authorization.dataobject.TravelerDetail");
        Node resultNode = beanNode.replaceNode {
            bean(parent: "Uif-InquiryView") {
                dictionaryConverter.transformInquirySectionsProperty(delegate, beanNode)
            }
        };

        checkBeanPropertyExists(resultNode, "items");

    }

    @Test
    public void testTransformInquirySectionDefinitionBean() {
        String inquiryDefPath = dictTestDir + "InquiryDefinitionSample.xml"
        def ddRootNode = getFileRootNode(inquiryDefPath);
        def beanNode = ddRootNode.bean.find { "InquiryDefinition".equals(it.@parent) }.property.list.bean.find { "InquirySectionDefinition".equals(it.@parent) };
        dictionaryConverter.definitionDataObjects.put("TravelerDetail-inquiryDefinition", "org.kuali.rice.krad.demo.travel.authorization.dataobject.TravelerDetail");
        Node resultNode = beanNode.replaceNode {
            dictionaryConverter.transformInquirySectionDefinitionBean(delegate, beanNode)
        };

        Assert.assertTrue("results contains grid section", "Uif-Disclosure-GridSection".equals(resultNode.@parent));
        checkBeanPropertyExists(resultNode, "layoutManager.numberOfColumns");
        checkBeanPropertyExists(resultNode, "items");
    }

    @Test
    public void testTransformMaintainableSectionsProperty() {
        String inquiryDefPath = dictTestDir + "MaintenanceDefinitionSample.xml"
        def ddRootNode = getFileRootNode(inquiryDefPath);
        def beanNode = ddRootNode.bean.find { "MaintenanceDocumentEntry".equals(it.@parent) };
        Node resultNode = beanNode.replaceNode {
            bean(parent: "Uif-MaintenanceView") {
                dictionaryConverter.transformMaintainableSectionsProperty(delegate, beanNode)
            }
        }

        checkBeanPropertyExists(resultNode, "items");
        def refSize = resultNode.property.list.ref.size();
        Assert.assertEquals("number of copied references", 1, refSize)
        def sectionSize = resultNode.property.list.bean.findAll { ["Uif-VerticalBoxSection", "Uif-MaintenanceGridSection", "Uif-MaintenanceStackedCollectionSection"].contains(it.@parent) }.size();
        Assert.assertEquals("number of converted section definitions", 1, sectionSize);

    }

    @Test
    public void testTransformMaintainableSectionDefinitionBean() {
        String inquiryDefPath = dictTestDir + "MaintenanceDefinitionSample.xml"
        def ddRootNode = getFileRootNode(inquiryDefPath);
        def beanNode = ddRootNode.bean.find { "AttachmentSampleMaintenanceDocument-EditAttachment-parentBean".equals(it.@id) };
        Node resultNode = beanNode.replaceNode {
            bean(parent: "Uif-MaintenanceView") {
                property(name: "items") {
                    list {
                        dictionaryConverter.transformMaintainableSectionDefinitionBean(delegate, beanNode)
                    }
                }
            }
        }

        checkBeanPropertyExists(resultNode, "items");
        def refSize = resultNode.property.find { "items".equals(it.@name) }.list.bean.size();
        Assert.assertEquals("number of beans created", 1, refSize)
        //def sectionSize = resultNode.property.list.bean.findAll { ["Uif-MaintenanceGridSection", "Uif-MaintenanceStackedCollectionSection"].contains(it.@parent) }.size();
        //Assert.assertEquals("number of converted section definitions", 2, sectionSize);

    }

    /**
     * Tests conversion of lookup definition's result fields into appropriate property
     *
     */
    @Test
    public void testTransformMaintainableItemsProperty() {
        String lookupDefFilePath = dictTestDir + "MaintenanceDefinitionSample.xml"
        def ddRootNode = getFileRootNode(lookupDefFilePath);
        def beanNode = ddRootNode.bean.find { "MaintainableSectionDefinition".equals(it.@parent) };

        beanNode = beanNode.replaceNode {
            bean(parent: "Uif-MaintenanceGridSection") {
                dictionaryConverter.transformMaintainableItemsProperty(delegate, beanNode);
            }
        }

        // confirm lookup fields has been replaced with criteria fields
        checkBeanPropertyExists(beanNode, "items");
        def resultsFieldProperty = beanNode.property.find { "items".equals(it.@name) };
        def attrFieldSize = resultsFieldProperty.list.bean.findAll { "Uif-InputField".equals(it.@parent) }.size();
        Assert.assertEquals("number of converted data fields did not match", 3, attrFieldSize);
    }

    /**
     * Tests conversion of lookup definition's result fields into appropriate property
     *
     */
    @Test
    public void testTransformMaintainableFieldsProperty() {
        String lookupDefFilePath = dictTestDir + "MaintenanceDefinitionSample.xml"
        String parentBeanName = "AttachmentSampleMaintenanceDocument-parentBean";
        String sectionDefName = "MultiAttachmentSampleMaintenanceDocument-AttachmentList";
        def ddRootNode = getFileRootNode(lookupDefFilePath);
        def collectionDefBean = getCollectionDefinitionBean(ddRootNode, parentBeanName, sectionDefName);
        collectionDefBean = collectionDefBean.replaceNode {
            bean(parent: "Uif-MaintenanceStackedCollectionSection") {
                dictionaryConverter.transformMaintainableFieldsProperty(delegate, collectionDefBean);
            }
        }

        def resultsFieldProperty = collectionDefBean.property.find { "items".equals(it.@name) };
        def attrFieldSize = resultsFieldProperty.list.bean.findAll { "Uif-InputField".equals(it.@parent) }.size();
        Assert.assertEquals("number of converted input fields", 2, attrFieldSize);
    }

    def getCollectionDefinitionBean(def rootNode, String parentBeanName, String sectionDefBeanName) {
        def beanNode = rootNode.bean.find { parentBeanName.equals(it.@id) };
        def maintainableItemsList = beanNode.property.find { "maintainableSections".equals(it.@name) }.list;
        def sectionDefBean = maintainableItemsList.bean.find { sectionDefBeanName.equals(it.@id) };
        def collectionDefBean = sectionDefBean.property.list.bean.find { "MaintainableCollectionDefinition".equals(it.@parent) };
        return collectionDefBean;
    }

    @Test
    void testTransformMaintenanceDocumentEntryBean() {
        String maintDefFilePath = dictTestDir + "MaintenanceDefinitionSample.xml"
        def ddRootNode = getFileRootNode(maintDefFilePath)
        def beanNode = ddRootNode.bean.find { "MaintenanceDocumentEntry".equals(it.@parent) }
        try {
            dictionaryConverter.transformMaintenanceDocumentEntryBean(beanNode)
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }

        checkBeanParentExists(ddRootNode, "Uif-MaintenanceView");
        checkBeanParentExists(ddRootNode, "MaintenanceDocumentEntry");
    }

    /**
     * transform lookup definition is responsible for converting a lookup definition into
     * a uif lookup view.  Verifies that
     *
     */
    @Test
    void testTransformLookupDefinitionBean() {
        String lookupDefFilePath = dictTestDir + "LookupDefinitionSample.xml"
        def ddRootNode = getFileRootNode(lookupDefFilePath)
        log.finer "Before " + ddRootNode.toString()
        def beanNode = ddRootNode.bean.find { it.@parent == "LookupDefinition" }
        try {
            dictionaryConverter.transformLookupDefinitionBean(beanNode)
        } catch (Exception e) {
            e.printStackTrace()
            Assert.fail("exception occurred in testing")
        }

        log.finer "After  " + getNodeString(ddRootNode)
        // confirm a uif inquiry view was generated and has the correct elements
        Assert.assertEquals("uif lookup view count", 1, ddRootNode.bean.findAll { it.@parent == "Uif-LookupView" }.size())
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