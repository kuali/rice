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
import org.junit.Ignore
import org.junit.Test

import groovy.xml.*


/**
 * Tests for the {@link org.kuali.rice.scripts.beans.MaintenanceDocumentEntryBeanTransformer} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class MaintenanceDocumentEntryBeanReferenceTransformerTest extends BeanTransformerTestBase {

    MaintenanceDocumentEntryBeanTransformer maintenanceDocumentEntryBeanTransformer;
    String defaultTestFilePath;
    String customTestFilePath;

    @Before
    void setUp() {
        super.setUp();
        defaultTestFilePath = getDictionaryTestDir() + "MaintenanceDefinitionReference.xml";
        customTestFilePath = getDictionaryTestDir() + "MaintenanceDefinitionCustomDPCSample.xml";
        maintenanceDocumentEntryBeanTransformer = new MaintenanceDocumentEntryBeanTransformer();
        maintenanceDocumentEntryBeanTransformer.init(config);
    }

    /**
     * Verifies maintenance document entry has been converted into a valid maintenance doc entry and view
     */
    @Test
    void testTransformMaintenanceDocumentEntryBeanWithPlaceholder() {
        def ddRootNode = getFileRootNode(defaultTestFilePath);


        def childBeanId = "EntityTypeMaintenanceDocument";
        def generalBeanId = "EntityTypeMaintenanceDocument-General";
        def detailsBeanId = "EntityTypeMaintenanceDocument-Details";
        def testCaseBeanId = "EntityTypeMaintenanceDocument-TestCase";
        def childBeanNode = ddRootNode.bean.find { childBeanId.equals(it.@id) }
        def generalBeanNode = ddRootNode.bean.find { generalBeanId.equals(it.@id) }
        def detailsBeanNode = ddRootNode.bean.find { detailsBeanId.equals(it.@id) }
        def testCaseBeanNode = ddRootNode.bean.find { testCaseBeanId.equals(it.@id) }

        try {
            maintenanceDocumentEntryBeanTransformer.transformMaintenanceDocumentEntryBean(childBeanNode);
            maintenanceDocumentEntryBeanTransformer.transformMaintainableSectionDefinitionBean( generalBeanNode);
            maintenanceDocumentEntryBeanTransformer.transformMaintainableSectionDefinitionBean( detailsBeanNode);
            maintenanceDocumentEntryBeanTransformer.transformMaintainableSectionDefinitionBean( testCaseBeanNode);
        } catch (Exception e) {
             e.printStackTrace();
             Assert.fail("exception occurred in testing");
        }

        checkBeanExistsById(ddRootNode, "EntityTypeMaintenanceDocument-uifMaintenanceDocumentEntry");
        checkBeanExistsById(ddRootNode, "EntityTypeMaintenanceDocument-MaintenanceView");
        checkBeanExistsById(ddRootNode, "EntityTypeMaintenanceDocument-General");
        checkBeanExistsById(ddRootNode, "EntityTypeMaintenanceDocument-Details");
        checkBeanExistsById(ddRootNode, "EntityTypeMaintenanceDocument-TestCase");

        // drill down TestCase bean and validate overrideLookupClass and overrideFieldConversions transformations
        def resultNode = ddRootNode.bean.find { "EntityTypeMaintenanceDocument-TestCase".equals(it.@id) };
        checkBeanPropertyValueExists(resultNode, "items", null);

        def itemNode = resultNode.property.find {"items".equals(it.@name)};
        def codeInputFieldBean = itemNode.list.bean.find { "code" }

        checkBeanPropertyValueExists(codeInputFieldBean, "quickfinder.fieldConversions", null);

        def quickfinderDataObjectClassName =  codeInputFieldBean.property.find {
            "quickfinder.dataObjectClassName".equals(it.@name)};
        Assert.assertNotNull(quickfinderDataObjectClassName);
        checkBeanPropertyValueExists(codeInputFieldBean, "quickfinder.dataObjectClassName",
                "org.kuali.rice.kim.impl.identity.EntityTypeBo");
    }

}
