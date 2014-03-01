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
 * Tests for the {@link org.kuali.rice.scripts.beans.LookupDefinitionBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class LookupDefinitionBeanTransformerTest extends BeanTransformerTestBase {

    LookupDefinitionBeanTransformer lookupDefinitionBeanTransformer;
    String defaultTestFilePath = getDictionaryTestDir() + "LookupDefinitionSample.xml";
    String defaultTestBeanID = "TravelerDetail-lookupDefinition-parentBean";

    @Before
    void setUp() {
        super.setUp();
        lookupDefinitionBeanTransformer = new LookupDefinitionBeanTransformer();
        lookupDefinitionBeanTransformer.init(config);
    }

    /**
     * transform lookup definition is responsible for converting a lookup definition into
     * a uif lookup view.  Verifies that
     *
     */
    @Test
    void testTransformLookupDefinitionBean() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def resultNode;
        def beanNode = ddRootNode.bean.find { defaultTestBeanID.equals(it.@id) };
        try {
            resultNode = lookupDefinitionBeanTransformer.transformLookupDefinitionBean(beanNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }

        // confirm a uif inquiry view was generated and has the correct elements
        checkBeanExistsByParentId(ddRootNode, "Uif-LookupView");
        checkBeanPropertyExists(resultNode, "criteriaFields");
        checkBeanPropertyExists(resultNode, "resultFields");
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
        String beanId = "TravelerDetail-lookupDefinition-parentBean";
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { beanId.equals(it.@id) };
        String parentName = beanNode.@parent;
        beanNode.replaceNode {
            bean(parent: "Uif-LookupView") {
                lookupDefinitionBeanTransformer.transformLookupFieldsProperty(delegate, beanNode);
            }
        }


        // confirm lookup fields has been replaced with criteria fields
        Assert.assertEquals("lookupFields not longer exists", 0, ddRootNode.findAll { parentName.equals(it.@name) }.size());
        Assert.assertEquals("criteriaFields exists", 1, ddRootNode.bean.property.findAll { "criteriaFields".equals(it.@name) }.size());
    }

    @Test
    public void testTransformHelpDefinitionProperty() {
        String beanId = "TravelerDetail-lookupDefinition-withHelpDefinition-parentBean";
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { beanId.equals(it.@id) };
        String parentName = beanNode.@parent;
        beanNode.replaceNode {
            bean(parent: "Uif-LookupView", id:"Result") {
                lookupDefinitionBeanTransformer.transformHelpDefinitionProperty(delegate, beanNode);
            }
        }

        // confirm lookup fields has been replaced with criteria fields
        def helpDefinitionCount = beanNode.property.findAll { "helpDefinition".equals(it.@name) }.size();
        def helpCount = beanNode.property.findAll { "help".equals(it.@name) }.size();
        Assert.assertEquals("helpDefinition should not exist", 0, helpDefinitionCount);
        Assert.assertEquals("help should exists", 1, helpCount);
    }

    @Test
    public void testTransformHelpDefinitionPropertyWithHelpUrl() {
        String beanId = "TravelerDetail-lookupDefinition-withHelpUrl-parentBean";
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { beanId.equals(it.@id) };
        def resultNode = beanNode.replaceNode {
            bean(parent: "Uif-LookupView", id:"Result") {
                lookupDefinitionBeanTransformer.transformHelpDefinitionProperty(delegate, beanNode);
            }
        }

        // confirm lookup fields has been replaced with criteria fields
        def helpDefinitionCount = resultNode.property.findAll { "helpDefinition".equals(it.@name) }.size();
        def helpCount = resultNode.property.findAll { "help".equals(it.@name) }.size();

        Assert.assertEquals("helpDefinition should not exist", 0, helpDefinitionCount);
        Assert.assertEquals("help should exist", 1, helpCount);
        def helpProperty = resultNode.property.find{ "help".equals(it.@name) };
        checkBeanExistsByParentId(helpProperty,"Uif-Help");
        def helpBean = helpProperty.bean.find { "Uif-Help".equals(it.@parent) };
        checkBeanPropertyExists(helpBean, "helpUrl");
    }


    /**
     * Tests conversion of lookup definition's result fields into appropriate property
     *
     */
    @Test
    public void testTransformResultFieldsProperties() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { "LookupDefinition".equals(it.@parent) };
        def resultNode = beanNode.replaceNode {
            bean(parent: "Uif-LookupView") {
                lookupDefinitionBeanTransformer.transformResultFieldsProperty(delegate, beanNode);
            }
        }

        // confirm lookup fields has been replaced with criteria fields
        checkBeanPropertyExists(resultNode, "resultFields");
        def resultsFieldProperty = resultNode.property.find { "resultFields".equals(it.@name) };
        def dataFieldSize = resultsFieldProperty.list.bean.findAll { "Uif-DataField".equals(it.@parent) }.size();
        Assert.assertEquals("number of converted data fields did not match", 11, dataFieldSize);
    }

    /**
     * transform lookup definition is responsible for converting a lookup definition into
     * a uif lookup view.  Verifies that kns disableSearchButtons property generates correct renderCriteriaActions
     *
     */
    @Test
    void testTransformLookupDefinitionBeanTestCase() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def resultNode;
        defaultTestBeanID  = "TestCase-lookupDefinition-parentBean";
        def beanNode = ddRootNode.bean.find { defaultTestBeanID.equals(it.@id) };

        try {
            resultNode = lookupDefinitionBeanTransformer.transformLookupDefinitionBean(beanNode);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }

        // confirm a uif lookup view was generated and has the correct elements
        checkBeanExistsByParentId(ddRootNode, "Uif-LookupView");

        checkBeanPropertyExists(resultNode, "renderCriteriaActions");
        def renderCriteriaActionsNode = resultNode.property.find { "renderCriteriaActions".equals(it.@name) };
        String renderCriteriaActionsProperty =  getNodeString(renderCriteriaActionsNode);
        Assert.assertTrue("renderCriteriaActions property should have false for value",renderCriteriaActionsProperty.contains("value=\"false\""));
    }

}
