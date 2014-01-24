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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests for the {@link org.kuali.rice.scripts.beans.InquiryDefinitionBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class InquiryDefinitionBeanTransformerTest extends BeanTransformerTestBase {

    InquiryDefinitionBeanTransformer inquiryDefinitionBeanTransformer;
    String defaultTestFilePath;
    String inquiryDefinitionBeanType = "InquiryDefinition";
    String inquiryDefinitionBeanId = "TravelerDetail-inquiryDefinition";
    String inquiryDefinitionClasspath = "org.kuali.rice.krad.demo.travel.authorization.dataobject.TravelerDetail";

    @Before
    void setUp() {
        super.setUp();
        defaultTestFilePath = getDictionaryTestDir() + "InquiryDefinitionSample.xml";
        inquiryDefinitionBeanTransformer = new InquiryDefinitionBeanTransformer();
        inquiryDefinitionBeanTransformer.init(getConfig());
        inquiryDefinitionBeanTransformer.definitionDataObjects.put(inquiryDefinitionBeanId, inquiryDefinitionClasspath);
    }

    /**
     * Tests that inquiry conversion generates a inquiry view including
     * title, viewname, data object, processes through inquiry fields and collections
     *
     */
    @Test
    public void testTransformInquiryDefinitionBean() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { inquiryDefinitionBeanType.equals(it.@parent) };
        def resultNode = inquiryDefinitionBeanTransformer.transformInquiryDefinitionBean(beanNode);
        checkBeanExistsByParentId(ddRootNode, "Uif-InquiryView");
        checkBeanPropertyExists(resultNode, "items");

    }

    @Test
    public void testTransformInquirySectionsProperty() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { inquiryDefinitionBeanType.equals(it.@parent) };
        Node resultNode = beanNode.replaceNode {
            bean(parent: "Uif-InquiryView") {
                inquiryDefinitionBeanTransformer.transformInquirySectionsProperty(delegate, beanNode)
            }
        };

        checkBeanPropertyExists(resultNode, "items");

    }

    @Test
    public void testTransformInquirySectionDefinitionBean() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def inquiryDefinitionBean = ddRootNode.bean.find { inquiryDefinitionBeanType.equals(it.@parent) };
        def beanNodes = inquiryDefinitionBean.property.list.bean.findAll { "InquirySectionDefinition".equals(it.@parent) };
        def beanNode = beanNodes.get(0);

        Node resultNode = beanNode.replaceNode {
            inquiryDefinitionBeanTransformer.transformInquirySectionDefinitionBean(delegate, beanNode)
        };

        Assert.assertTrue("results contains grid section", "Uif-Disclosure-GridSection".equals(resultNode.@parent));
        checkBeanPropertyExists(resultNode, "headerText");
        checkBeanPropertyExists(resultNode, "layoutManager.numberOfColumns");
        checkBeanPropertyExists(resultNode, "items");
    }

    @Test
    public void testTransformInquirySectionDefinitionBeanWithCollectionDefinition() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def parentBeanId = "InquirySectionDefinition-transformInquiryCollectionDefinition";
        def inquiryDefinitionBean = ddRootNode.bean.find { inquiryDefinitionBeanType.equals(it.@parent) };
        def beanNode = inquiryDefinitionBean.property.list.bean.find { parentBeanId.equals(it.@id) };

        Node resultNode = beanNode.replaceNode {
            inquiryDefinitionBeanTransformer.transformInquirySectionDefinitionBean(delegate, beanNode)
        };

        Assert.assertTrue("results contains grid section", "Uif-StackedCollectionSection".equals(resultNode.@parent));
        checkBeanPropertyExists(resultNode, "headerText");
        checkBeanPropertyExists(resultNode, "layoutManager.numberOfColumns");
        checkBeanPropertyExists(resultNode, "collectionObjectClass");
        checkBeanPropertyExists(resultNode, "propertyName");
        checkBeanPropertyExists(resultNode, "items");
    }


    @Test
    public void testTransformInquiryCollectionDefinitionBean() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def parentBeanId = "InquirySectionDefinition-transformInquiryCollectionDefinition";
        def inquiryDefinitionBean = ddRootNode.bean.find { inquiryDefinitionBeanType.equals(it.@parent) };
        def beanNode = inquiryDefinitionBean.property.list.bean.find { parentBeanId.equals(it.@id) };

        Node resultNode = beanNode.replaceNode {
            inquiryDefinitionBeanTransformer.transformInquiryCollectionDefinitionBean(delegate, beanNode)
        };

        Assert.assertTrue("results contains grid section", "Uif-StackedCollectionSection".equals(resultNode.@parent));
        checkBeanPropertyExists(resultNode, "headerText");
        checkBeanPropertyExists(resultNode, "layoutManager.numberOfColumns");
        checkBeanPropertyExists(resultNode, "collectionObjectClass");
        checkBeanPropertyExists(resultNode, "propertyName");
        checkBeanPropertyExists(resultNode, "items");
    }

}
