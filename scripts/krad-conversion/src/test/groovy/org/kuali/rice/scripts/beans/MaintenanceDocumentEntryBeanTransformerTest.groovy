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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests for the {@link org.kuali.rice.scripts.beans.MaintenanceDocumentEntryBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class MaintenanceDocumentEntryBeanTransformerTest extends BeanTransformerTestBase {

    MaintenanceDocumentEntryBeanTransformer maintenanceDocumentEntryBeanTransformer;
    String defaultTestFilePath;
    String customTestFilePath;
    @Before
    void setUp() {
        super.setUp();
        defaultTestFilePath = getDictionaryTestDir() + "MaintenanceDefinitionSample.xml";
        customTestFilePath = getDictionaryTestDir() + "MaintenanceDefinitionCustomDPCSample.xml";
        maintenanceDocumentEntryBeanTransformer = new MaintenanceDocumentEntryBeanTransformer();
        maintenanceDocumentEntryBeanTransformer.init(config);
    }

    /**
     * Verifies maintenance document entry has been converted into a valid maintenance doc entry and view
     */
    @Test
    void testTransformMaintenanceDocumentEntryBeanPlaceholder() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        ddRootNode.bean.each { bean -> maintenanceDocumentEntryBeanTransformer.fixNamespaceProperties(bean) }

        def beanNode = ddRootNode.bean.find { "AttachmentSampleMaintenanceDocument-parentBean".equals(it.@parent) }
        try {
            maintenanceDocumentEntryBeanTransformer.transformMaintenanceDocumentEntryBean(beanNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }

        checkBeanParentExists(ddRootNode, "AttachmentSampleMaintenanceDocument-uifMaintenanceDocumentEntry-parentBean");
        checkBeanParentExists(ddRootNode, "AttachmentSampleMaintenanceDocument-MaintenanceView-parentBean");
    }

    /**
     * Verifies maintenance document entry has been converted into a valid maintenance doc entry and view
     */
    @Test
    void testTransformMaintenanceDocumentEntryBean() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        ddRootNode.bean.each { bean -> maintenanceDocumentEntryBeanTransformer.fixNamespaceProperties(bean) }

        def beanNode = ddRootNode.bean.find { "MaintenanceDocumentEntry".equals(it.@parent) }
        try {
            maintenanceDocumentEntryBeanTransformer.transformMaintenanceDocumentEntryBean(beanNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }

        checkBeanParentExists(ddRootNode, "uifMaintenanceDocumentEntry");
        def resultMDENode = ddRootNode.bean.find { "uifMaintenanceDocumentEntry".equals(it.@parent) }
        def mdeCheckedProperties = [];
        checkBeanStructure(resultMDENode, mdeCheckedProperties, ["businessObjectEntry"]);
        //checks if the default documentPresentationControllerClass has been added
        checkBeanPropertyValueExists(resultMDENode,"documentPresentationControllerClass","org.kuali.rice.krad.maintenance.MaintenanceViewPresentationControllerBase");
        //checks if the default documentAuthorizerClass has been added
        checkBeanPropertyValueExists(resultMDENode,"documentAuthorizerClass","org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizerBase");

        def umvCheckedProperties = ["dataObjectClassName"];
        checkBeanParentExists(ddRootNode, "Uif-MaintenanceView");
        def resultMVNode = ddRootNode.bean.find { "Uif-MaintenanceView".equals(it.@parent) }
        checkBeanStructure(resultMVNode, umvCheckedProperties, ["maintainableSections"]);

    }

    /**
     * Verifies that a documentPresentationControllerClass property is added with empty value and a comment is added to
     * specify the value if a custom class is specified.
     */
    @Test
    void testTransformMaintenanceDocumentEntryNonDefaultDPCBean() {
        def ddRootNode = getFileRootNode(customTestFilePath);
        ddRootNode.bean.each { bean -> maintenanceDocumentEntryBeanTransformer.fixNamespaceProperties(bean) }

        def beanNode = ddRootNode.bean.find { "MaintenanceDocumentEntry".equals(it.@parent) }
        try {
            maintenanceDocumentEntryBeanTransformer.transformMaintenanceDocumentEntryBean(beanNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }

        checkBeanParentExists(ddRootNode, "uifMaintenanceDocumentEntry");
        def resultMDENode = ddRootNode.bean.find { "uifMaintenanceDocumentEntry".equals(it.@parent) }
        checkBeanPropertyValueExists(resultMDENode,"documentPresentationControllerClass","");

        def foundComment = false;
        beanNode.parent().meta.findAll { it.@key == "comment" }.each{ if (it?.@value?.equals("TODO - Add documentPresentationControllerClass for bean Id: " + beanNode.@id)) {foundComment = true;}  };
        Assert.assertTrue("No comment found for custom documentPresentationControllerClass", foundComment);

        }

    /**
     * Verifies that a documentAuthorizerClass property is added with empty value and a comment is added to
     * specify the value if a custom class is specified.
     */
    @Test
    void testTransformMaintenanceDocumentEntryNonDefaultDACBean() {
        def ddRootNode = getFileRootNode(customTestFilePath);
        ddRootNode.bean.each { bean -> maintenanceDocumentEntryBeanTransformer.fixNamespaceProperties(bean) }

        def beanNode = ddRootNode.bean.find { "MaintenanceDocumentEntry".equals(it.@parent) }
        try {
            maintenanceDocumentEntryBeanTransformer.transformMaintenanceDocumentEntryBean(beanNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }

        checkBeanParentExists(ddRootNode, "uifMaintenanceDocumentEntry");
        def resultMDENode = ddRootNode.bean.find { "uifMaintenanceDocumentEntry".equals(it.@parent) }
        checkBeanPropertyValueExists(resultMDENode,"documentAuthorizerClass","");

        def foundComment = false;
        beanNode.parent().meta.findAll { it.@key == "comment" }.each{ if (it?.@value?.equals("TODO - Add documentAuthorizerClass for bean Id: " + beanNode.@id)) {foundComment = true;}  };
        Assert.assertTrue("No comment found for custom documentAuthorizerClass", foundComment);
    }


    @Test
    public void testTransformMaintainableSectionsProperty() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { "MaintenanceDocumentEntry".equals(it.@parent) };
        Node resultNode = beanNode.replaceNode {
            bean(parent: "Uif-MaintenanceView") {
                maintenanceDocumentEntryBeanTransformer.transformMaintainableSectionsProperty(delegate, beanNode)
            }
        }

        checkBeanPropertyExists(resultNode, "items");
        def refSize = resultNode.property.list.ref.size();
        Assert.assertEquals("number of copied references", 1, refSize)
        def sectionSize = resultNode.property.list.bean.findAll { ["Uif-VerticalBoxSection", "Uif-MaintenanceGridSection", "Uif-MaintenanceStackedCollectionSection"].contains(it.@parent) }.size();
        Assert.assertEquals("number of converted section definitions", 1, sectionSize);

    }

    /**
     * Tests conversion of lookup definition's result fields into appropriate property
     *
     */
    @Test
    public void testTransformMaintainableFieldsProperty() {
        String parentBeanName = "AttachmentSampleMaintenanceDocument-parentBean";
        String sectionDefName = "MultiAttachmentSampleMaintenanceDocument-AttachmentList";
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def collectionDefBean = getCollectionDefinitionBean(ddRootNode, parentBeanName, sectionDefName);
        collectionDefBean = collectionDefBean.replaceNode {
            bean(parent: "Uif-MaintenanceStackedCollectionSection") {
                maintenanceDocumentEntryBeanTransformer.transformMaintainableFieldsProperty(delegate, collectionDefBean);
            }
        }

        def resultsFieldProperty = collectionDefBean.property.find { "items".equals(it.@name) };
        def attrFieldSize = resultsFieldProperty.list.bean.findAll { "Uif-InputField".equals(it.@parent) }.size();
        Assert.assertEquals("number of converted input fields", 2, attrFieldSize);
    }

    /**
     * retrieves collection definition from a maintenance document entry
     *
     * @param rootNode
     * @param parentBeanName
     * @param sectionDefBeanName
     * @return
     */
    Node getCollectionDefinitionBean(def rootNode, String parentBeanName, String sectionDefBeanName) {
        def beanNode = rootNode.bean.find { parentBeanName.equals(it.@id) };
        def maintainableItemsList = beanNode.property.find { "maintainableSections".equals(it.@name) }.list;
        def sectionDefBean = maintainableItemsList.bean.find { sectionDefBeanName.equals(it.@id) };
        def collectionDefBean = sectionDefBean.property.list.bean.find { "MaintainableCollectionDefinition".equals(it.@parent) };
        return collectionDefBean;
    }

    @Test
    public void testTransformMaintainableSectionDefinitionBean() {
        String MAINT_SECT_DEF_BEAN_ID = "AttachmentSampleMaintenanceDocument-EditAttachment-parentBean";
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { MAINT_SECT_DEF_BEAN_ID.equals(it.@id) };
        Node resultNode = beanNode.replaceNode {
            bean(parent: "Uif-MaintenanceView") {
                property(name: "items") {
                    list {
                        maintenanceDocumentEntryBeanTransformer.transformMaintainableSectionDefinitionBean(delegate, beanNode)
                    }
                }
            }
        }
        //
        def sectionResultNode = resultNode.property.find{ "items".equals(it.@name)}.list.bean[0];

        // check that new grid or vertical box section includes help
        checkBeanPropertyExists(sectionResultNode, "items");
        checkBeanPropertyExists(sectionResultNode, "help");
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
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { "MaintainableSectionDefinition".equals(it.@parent) };

        beanNode = beanNode.replaceNode {
            bean(parent: "Uif-MaintenanceGridSection") {
                maintenanceDocumentEntryBeanTransformer.transformMaintainableItemsProperty(delegate, beanNode);
            }
        }

        // confirm lookup fields has been replaced with criteria fields
        checkBeanPropertyExists(beanNode, "items");
        def resultsFieldProperty = beanNode.property.find { "items".equals(it.@name) };
        def attrFieldSize = resultsFieldProperty.list.bean.findAll { "Uif-InputField".equals(it.@parent) }.size();
        Assert.assertEquals("number of converted data fields did not match", 3, attrFieldSize);
    }

}
