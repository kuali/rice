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
 * Tests for the {@link org.kuali.rice.scripts.beans.BusinessObjectEntryBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class BusinessObjectEntryBeanTransformerTest extends BeanTransformerTestBase {

    BusinessObjectEntryBeanTransformer businessObjectEntryBeanTransformer;
    String defaultTestFilePath;

    @Before
    void setUp() {
        super.setUp();
        defaultTestFilePath = getDictionaryTestDir() + "BusinessObjectEntrySample.xml";
        businessObjectEntryBeanTransformer = new BusinessObjectEntryBeanTransformer();
        businessObjectEntryBeanTransformer.init(getConfig());
    }

    @Test
    void testTransformControlProperty() {
        String inqDefFilePath = getDictionaryTestDir() + "ControlFieldSample.xml";
        def ddRootNode = getFileRootNode(inqDefFilePath);
        def renamedControlDefinitions = config.map.convert.dd_bean_control;
        def selectBeanNode = ddRootNode.bean.find { "BookOrder-bookId-parentBean".equals(it.@id) };
        def textAreaBeanNode = ddRootNode.bean.find { "BookOrder-value-parentBean".equals(it.@id) };

        try {
            businessObjectEntryBeanTransformer.transformControlProperty(selectBeanNode, renamedControlDefinitions, true);
            businessObjectEntryBeanTransformer.transformControlProperty(textAreaBeanNode, renamedControlDefinitions, true);
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
    void testTransformValidationPatternBeanProperty() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { "TravelerDetail-id-parentBean".equals(it.@id) };
        try {
            businessObjectEntryBeanTransformer.transformValidationPatternProperty(beanNode, true);
            checkBeanPropertyExists(beanNode, "validCharactersConstraint");

            log.finer("resulting node is " + getNodeString(beanNode));
            def constraintProperty = beanNode.property.find { "validCharactersConstraint".equals(it.@name) };
            checkBeanExistsByParentId(constraintProperty, "NumericPatternConstraint");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }
    }

    @Test
    void testTransformRegexValidationPatternBeanProperty() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { "TravelerDetail-zipCode-parentBean".equals(it.@id) };
        try {
            businessObjectEntryBeanTransformer.transformValidationPatternProperty(beanNode, true);
            checkBeanPropertyExists(beanNode, "validCharactersConstraint");
            def constraintProperty = beanNode.property.find { "validCharactersConstraint".equals(it.@name) };
            checkBeanExistsByParentId(constraintProperty, "JavaClassPatternConstraint");
            def constraintBean = constraintProperty.bean.find { "JavaClassPatternConstraint".equals(it.@parent) };
            checkBeanPropertyExists(constraintBean, "value");
            checkBeanPropertyExists(constraintBean, "messageKey");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }
    }

}
