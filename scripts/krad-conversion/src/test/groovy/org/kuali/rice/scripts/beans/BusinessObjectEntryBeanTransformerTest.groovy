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
    void testTransformValidationPatternBeanProperty() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);
        def beanNode = ddRootNode.bean.find { "TravelerDetail-id-parentBean".equals(it.@id) };
        try {
            businessObjectEntryBeanTransformer.transformValidationPatternBeanProperty(beanNode, true);
            checkBeanPropertyExists(beanNode, "validCharactersConstraint");

            log.finer("resulting node is " + getNodeString(beanNode));
            def constraintProperty = beanNode.property.find { "validCharactersConstraint".equals(it.@name) };
            checkBeanParentExists(constraintProperty, "NumericPatternConstraint");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception occurred in testing");
        }
    }
}
